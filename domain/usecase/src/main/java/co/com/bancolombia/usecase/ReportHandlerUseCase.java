package co.com.bancolombia.usecase;

import co.com.bancolombia.model.common.gateways.TraceLoggerPort;
import co.com.bancolombia.model.report.ReportIncrementEvent;
import co.com.bancolombia.model.report.gateways.ReportCounterPort;
import co.com.bancolombia.model.report.gateways.ReportHandlerPort;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class ReportHandlerUseCase implements ReportHandlerPort {
    private final ReportCounterPort reportCounterPort;
    private final TraceLoggerPort logger;

    @Override
    public Mono<Void> handle(ReportIncrementEvent reportIncrementEvent) {
        return Mono.defer(() -> {
                    logger.info("Received report: id={} amount={}", reportIncrementEvent.loanId(), reportIncrementEvent.amount());

                    return reportCounterPort.incrementApproved(reportIncrementEvent).thenReturn(Mono.empty());
                })
                .then();
    }
}
