package co.com.bancolombia.dynamodb.adapter;

import co.com.bancolombia.dynamodb.entities.DedupEventEntity;
import co.com.bancolombia.dynamodb.entities.ReportAggregateEntity;
import co.com.bancolombia.dynamodb.helper.TemplateAdapterOperations;
import co.com.bancolombia.dynamodb.mapper.DdbErrorMapper;
import co.com.bancolombia.model.common.gateways.TraceLoggerPort;
import co.com.bancolombia.model.report.ReportIncrementEvent;
import co.com.bancolombia.model.report.ReportSummary;
import co.com.bancolombia.model.report.gateways.ReportCounterPort;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.endpoints.internal.Value;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;


@Repository
public class DynamoReportRepositoryAdapter extends TemplateAdapterOperations<ReportSummary, String, ReportAggregateEntity> implements ReportCounterPort {
    private static final String TABLE_REPORTS = "loan_reports";
    private static final String TABLE_DEDUP = "loan_events_dedup";

    private static final String PK_VALUE = "REPORT#APPROVED";
    private static final String SK_VALUE = "TOTAL";

    static final String DEFAULT_DATE = "1969-12-31T00:00:00Z";

    private final DynamoDbAsyncClient asyncClient;
    private final DynamoDbAsyncTable<ReportAggregateEntity> reportsTable;

    private final TraceLoggerPort logger;

    public DynamoReportRepositoryAdapter(DynamoDbEnhancedAsyncClient connectionFactory, DynamoDbAsyncClient asyncClient, ObjectMapper mapper, TraceLoggerPort logger) {
        super(connectionFactory, mapper, e -> new ReportSummary(
                e.getApprovedCount() == null ? 0L : e.getApprovedCount(),
                e.getApprovedAmountSum() == null ? BigDecimal.ZERO : e.getApprovedAmountSum(),
                e.getUpdatedAt() == null ? DEFAULT_DATE : e.getUpdatedAt()
        ), TABLE_REPORTS);

        this.asyncClient = asyncClient;
        this.logger = logger;

        this.reportsTable = connectionFactory.table(TABLE_REPORTS, TableSchema.fromBean(ReportAggregateEntity.class));
        connectionFactory.table(TABLE_DEDUP, TableSchema.fromBean(DedupEventEntity.class));
    }

    @Override
    public Mono<Void> incrementApproved(ReportIncrementEvent reportIncrementEvent) {
        final var now = Instant.now().toString();

        //Actualizar y validar que no exista el evento en la tabla de eventos duplicados
        var putDedup = TransactWriteItem.builder()
                .put(build -> build
                        .tableName(TABLE_DEDUP)
                        .item(Map.of(
                                "event_id", AttributeValue.builder().s(reportIncrementEvent.loanId()).build(),
                                "created_at", AttributeValue.builder().s(now).build()
                        ))
                        .conditionExpression("attribute_not_exists(event_id)")
                        .build())
                .build();
        var updateTotal = TransactWriteItem.builder()
                .update(build -> build
                        .tableName(TABLE_REPORTS)
                        .key(Map.of(
                                "pk", AttributeValue.builder().s(PK_VALUE).build(),
                                "sk", AttributeValue.builder().s(SK_VALUE).build()
                        ))
                        .updateExpression("ADD approved_count :increment, approved_amount_sum :amount SET updated_at = :now")
                        .conditionExpression("attribute_exists(pk)")
                        .expressionAttributeValues(Map.of(
                                ":increment", AttributeValue.builder().n("1").build(),
                                ":amount", AttributeValue.builder().n(reportIncrementEvent.amount().toString()).build(),
                                ":now", AttributeValue.builder().s(now).build()
                        ))
                        .build())
                .build();
        var request = TransactWriteItemsRequest.builder()
                .transactItems(List.of(putDedup, updateTotal))
                .build();
        return Mono.fromFuture(() -> asyncClient.transactWriteItems(request))
                .onErrorResume(ConditionalCheckFailedException.class, e -> {
                    logger.error("Error incrementing approved count", e);
                    return Mono.empty();
                })
                .doOnSuccess(r -> logger.info("Approved count incremented"))
                .then()
                .onErrorMap(DdbErrorMapper::toDomain);
    }

    @Override
    public Mono<ReportSummary> getSummary() {

        var request = GetItemEnhancedRequest.builder()
                .key(builder -> builder
                        .partitionValue(AttributeValue.builder().s(PK_VALUE).build())
                        .sortValue(AttributeValue.builder().s(SK_VALUE).build())
                        .build())
                .consistentRead(false)
                .build();
        return Mono.fromFuture(() -> reportsTable.getItem(request))
                .map(e -> {
                            logger.trace("Report summary: {}", e);
                            return new ReportSummary(
                                    e.getApprovedCount() == null ? 0L: e.getApprovedCount(),
                                    e.getApprovedAmountSum() == null ? BigDecimal.ZERO : e.getApprovedAmountSum(),
                                    e.getUpdatedAt() == null ? DEFAULT_DATE : e.getUpdatedAt());
                        }
                )
                .defaultIfEmpty(new ReportSummary(0L, BigDecimal.ZERO, DEFAULT_DATE))
                .onErrorMap(DdbErrorMapper::toDomain);
    }
}
