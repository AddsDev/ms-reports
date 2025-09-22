package co.com.bancolombia.usecase;

import co.com.bancolombia.model.common.gateways.TraceLoggerPort;
import co.com.bancolombia.model.report.ReportSummary;
import co.com.bancolombia.model.report.gateways.ReportCounterPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportUseCaseTest {

    @Mock
    ReportCounterPort reportCounterPort;
    @Mock
    TraceLoggerPort logger;

    @Test
    void executeShouldReturnSummaryAndLogTraceInfo() {
        var summary = new ReportSummary(3L, new BigDecimal("1234.56"), "2025-09-20T12:00:00Z");
        when(reportCounterPort.getSummary()).thenReturn(Mono.just(summary));

        var useCase = new ReportUseCase(reportCounterPort, logger);

        StepVerifier.create(useCase.execute())
                .expectNext(summary)
                .verifyComplete();

        verify(logger).trace("ReportUseCase started");
        verify(logger).info("ReportUseCase finished: {}", summary);
        verify(reportCounterPort).getSummary();
        verify(logger, never()).error(anyString(), any(Throwable.class));
    }

    @Test
    void executeShouldPropagateErrorAndLogError() {
        var boom = new RuntimeException("boom");
        when(reportCounterPort.getSummary()).thenReturn(Mono.error(boom));

        var useCase = new ReportUseCase(reportCounterPort, logger);

        StepVerifier.create(useCase.execute())
                .expectErrorMatches(e -> e == boom)
                .verify();

        verify(logger).trace("ReportUseCase started");
        verify(logger).error("ReportUseCase failed", boom);
        verify(logger, never()).info(anyString(), any());
    }
}