package co.com.bancolombia.api.report.handler;

import co.com.bancolombia.api.report.mapper.ReportMapper;
import co.com.bancolombia.model.common.gateways.TraceLoggerPort;
import co.com.bancolombia.usecase.ReportUseCase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class ReportHandler {
    private final ReportUseCase reportUseCase;
    private final ReportMapper reportMapper;
    private final TraceLoggerPort logger;

    public ReportHandler(ReportUseCase reportUseCase, ReportMapper reportMapper, @Qualifier("entryPointLogger") TraceLoggerPort logger) {
        this.reportUseCase = reportUseCase;
        this.reportMapper = reportMapper;
        this.logger = logger;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    public Mono<ServerResponse> showReport(ServerRequest serverRequest) {
        return reportUseCase.execute().map(reportMapper::toResponse).flatMap(body -> ServerResponse.ok().bodyValue(body)).doOnError(e -> logger.error("Error getting report", e));
    }
}
