package co.com.bancolombia.usecase;

import co.com.bancolombia.model.common.gateways.TraceLoggerPort;
import co.com.bancolombia.model.report.ReportSummary;
import co.com.bancolombia.model.report.gateways.ReportCounterPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ReportUseCase {
    private final ReportCounterPort reportCounterPort;
    private final TraceLoggerPort logger;

    public Mono<ReportSummary> execute() {
        logger.trace("ReportUseCase started");
        return reportCounterPort.getSummary()
                .doOnNext(summary -> logger.info("ReportUseCase finished: {}", summary))
                .doOnError(e -> logger.error("ReportUseCase failed", e));

    }
}
