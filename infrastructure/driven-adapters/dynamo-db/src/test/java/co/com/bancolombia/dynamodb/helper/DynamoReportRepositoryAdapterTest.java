package co.com.bancolombia.dynamodb.helper;

import co.com.bancolombia.dynamodb.entities.DedupEventEntity;
import co.com.bancolombia.dynamodb.adapter.DynamoReportRepositoryAdapter;
import co.com.bancolombia.dynamodb.entities.ReportAggregateEntity;
import co.com.bancolombia.model.common.gateways.TraceLoggerPort;
import co.com.bancolombia.model.report.ReportIncrementEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.reactivecommons.utils.ObjectMapper;
import reactor.test.StepVerifier;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsRequest;
import software.amazon.awssdk.services.dynamodb.model.TransactWriteItemsResponse;


import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class DynamoReportRepositoryAdapterTest {

    @Mock
    private DynamoDbEnhancedAsyncClient enhanced;

    @Mock
    private DynamoDbAsyncClient raw;

    @Mock
    private ObjectMapper mapper;

    @Mock
    private DynamoDbAsyncTable<ReportAggregateEntity> reportsTable;

    @Mock
    private DynamoDbAsyncTable<DedupEventEntity> dedupTable;

    @Mock
    private TraceLoggerPort logger;

    private DynamoReportRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(enhanced.table("loan_reports", TableSchema.fromBean(ReportAggregateEntity.class)))
                .thenReturn(reportsTable);
        when(enhanced.table("loan_events_dedup", TableSchema.fromBean(DedupEventEntity.class)))
                .thenReturn(dedupTable);

        adapter = new DynamoReportRepositoryAdapter(enhanced, raw, mapper, logger);
    }

    @Test
    void entityBeanShouldExposeNonNullPropertiesWhenConstructed() {
        ReportAggregateEntity e = new ReportAggregateEntity();
        e.setPk("REPORT#APPROVED");
        e.setSk("TOTAL");
        e.setApprovedCount(0L);
        e.setApprovedAmountSum(BigDecimal.ZERO);
        e.setUpdatedAt("1970-01-01T00:00:00Z");

        assertNotNull(e.getPk());
        assertNotNull(e.getSk());
        assertNotNull(e.getApprovedCount());
        assertNotNull(e.getApprovedAmountSum());
        assertNotNull(e.getUpdatedAt());
    }

    @Test
    void getSummaryShouldReturnValuesFromItem() {
        ReportAggregateEntity entity = new ReportAggregateEntity();
        entity.setPk("REPORT#APPROVED");
        entity.setSk("TOTAL");
        entity.setApprovedCount(123L);
        entity.setApprovedAmountSum(BigDecimal.valueOf(999000000L));
        entity.setUpdatedAt("2025-09-19T22:10:00Z");

        when(reportsTable.getItem(any(GetItemEnhancedRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(entity));

        StepVerifier.create(adapter.getSummary())
                .assertNext(summary -> {
                    assertEquals(123L, summary.approvedTotal());
                    assertEquals(BigDecimal.valueOf(999000000L), summary.approvedAmountTotal());
                    assertEquals("2025-09-19T22:10:00Z", summary.updatedAt());
                })
                .verifyComplete();
    }

    @Test
    void getSummaryShouldReturnZerosWhenItemIsNull() {
        when(reportsTable.getItem(any(GetItemEnhancedRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(null));

        StepVerifier.create(adapter.getSummary())
                .assertNext(summary -> {
                    assertEquals(0L, summary.approvedTotal());
                    assertEquals(BigDecimal.ZERO, summary.approvedAmountTotal());
                    assertEquals("1969-12-31T00:00:00Z", summary.updatedAt());
                })
                .verifyComplete();
    }

    @Test
    void incrementApprovedShouldCompleteOnSuccess() {
        when(raw.transactWriteItems(any(TransactWriteItemsRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        TransactWriteItemsResponse.builder().build()
                ));

        StepVerifier.create(adapter.incrementApproved(new ReportIncrementEvent(BigDecimal.valueOf(3500000L), "97f3a076-7ddb-40b2-9b6f-4ee60c15dc23", "2025-09-19T21:59:10Z")))
                .verifyComplete();
    }

    @Test
    void incrementApprovedShouldBeIdempotentOnConditionalFailure() {
        when(raw.transactWriteItems(any(TransactWriteItemsRequest.class)))
                .thenReturn(CompletableFuture.failedFuture(
                        ConditionalCheckFailedException.builder().message("duplicate").build()
                ));

        StepVerifier.create(adapter.incrementApproved(new ReportIncrementEvent(BigDecimal.valueOf(2000000L), "97f3a076-7ddb-40b2-9b6f-4ee60c15dc32", "2025-09-19T22:00:00Z")))
                .verifyComplete();
    }
}