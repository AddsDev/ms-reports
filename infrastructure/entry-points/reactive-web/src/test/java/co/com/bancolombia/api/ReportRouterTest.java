package co.com.bancolombia.api;

import co.com.bancolombia.api.handler.GlobalErrorAttributes;
import co.com.bancolombia.api.handler.GlobalExceptionHandler;
import co.com.bancolombia.api.model.ApiResponse;
import co.com.bancolombia.api.report.dto.ReportResponse;
import co.com.bancolombia.api.report.handler.ReportHandler;
import co.com.bancolombia.api.report.mapper.ReportMapper;
import co.com.bancolombia.model.common.gateways.TraceLoggerPort;
import co.com.bancolombia.model.report.ReportSummary;
import co.com.bancolombia.model.report.gateways.ReportCounterPort;
import co.com.bancolombia.usecase.ReportUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;

@WebFluxTest
@ContextConfiguration(classes = {ReportRouter.class, ReportHandler.class, GlobalExceptionHandler.class, GlobalErrorAttributes.class})
class ReportRouterTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    @Qualifier("entryPointLogger")
    private TraceLoggerPort logger;

    @MockitoBean
    private ReportUseCase reportUseCase;

    @MockitoBean
    private ReportCounterPort reportCounterPort;

    @MockitoBean
    private ReportMapper reportMapper;

    private ReportSummary reportSummary;
    private ReportResponse reportResponse;
    private ApiResponse<ReportResponse> apiResponse;

    @BeforeEach
    void setUp() {
        reportSummary = new ReportSummary(1L, BigDecimal.valueOf(1000000000000L), "2025-09-20T12:00:00Z");
        reportResponse = new ReportResponse(1L, 1000000000000L, "2025-09-20T12:00:00Z");
        apiResponse = new ApiResponse<>(reportResponse);
        lenient().when(reportUseCase.execute()).thenReturn(Mono.just(reportSummary));
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @Test
    void testReports() {
        when(reportMapper.toReportResponse(any(ReportSummary.class))).thenReturn(reportResponse);
        when(reportMapper.toResponse(any(ReportSummary.class))).thenReturn(apiResponse);
        when(reportUseCase.execute()).thenReturn(Mono.just(reportSummary));

        webTestClient.mutateWith(mockJwt())
                .get()
                .uri("/api/v1/reportes")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.data.approvedTotal").isEqualTo(1);
    }
}
