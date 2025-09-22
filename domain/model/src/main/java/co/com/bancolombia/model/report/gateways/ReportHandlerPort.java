package co.com.bancolombia.model.report.gateways;

import co.com.bancolombia.model.report.ReportIncrementEvent;
import reactor.core.publisher.Mono;

public interface ReportHandlerPort {
    Mono<Void> handle(ReportIncrementEvent reportIncrementEvent);
}
