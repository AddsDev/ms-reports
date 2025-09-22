package co.com.bancolombia.usecase;

import co.com.bancolombia.model.common.gateways.TraceLoggerPort;
import co.com.bancolombia.model.report.ReportIncrementEvent;
import co.com.bancolombia.model.report.gateways.ReportCounterPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class ReportHandlerUseCaseTest {
    @Mock
    ReportCounterPort reportCounterPort;
    @Mock
    TraceLoggerPort logger;

    @InjectMocks
    ReportHandlerUseCase useCase;

    @Test
    void handleShouldLogAndIncrementAndComplete() {
        var evt = new ReportIncrementEvent(new BigDecimal("1000.00"), "L-77", "2025-09-20T12:00:00Z");
        when(reportCounterPort.incrementApproved(evt)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.handle(evt))
                .verifyComplete();

        verify(logger).info("Received report: id={} amount={}", "L-77", new BigDecimal("1000.00"));
        verify(reportCounterPort, times(1)).incrementApproved(evt);
        verifyNoMoreInteractions(reportCounterPort);
    }

    @Test
    void handleShouldPropagateErrorIfCounterFails() {
        var evt = new ReportIncrementEvent(new BigDecimal("10"), "L-ERR", "now");
        when(reportCounterPort.incrementApproved(evt)).thenReturn(Mono.error(new IllegalStateException("boom")));

        StepVerifier.create(useCase.handle(evt))
                .expectErrorMatches(e -> e instanceof IllegalStateException && e.getMessage().equals("boom"))
                .verify();

        verify(logger).info("Received report: id={} amount={}", "L-ERR", new BigDecimal("10"));
        verify(reportCounterPort).incrementApproved(evt);
    }
}