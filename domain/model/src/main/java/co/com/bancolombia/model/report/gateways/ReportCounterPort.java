package co.com.bancolombia.model.report.gateways;

import co.com.bancolombia.model.report.ReportIncrementEvent;
import co.com.bancolombia.model.report.ReportSummary;
import reactor.core.publisher.Mono;

public interface ReportCounterPort {
    Mono<Void> incrementApproved(ReportIncrementEvent reportIncrementEvent);
    Mono<ReportSummary> getSummary();
}
