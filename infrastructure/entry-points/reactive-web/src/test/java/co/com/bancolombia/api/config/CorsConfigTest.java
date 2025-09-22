package co.com.bancolombia.api.config;

import co.com.bancolombia.api.ReportRouter;
import co.com.bancolombia.api.report.handler.ReportHandler;
import co.com.bancolombia.api.report.mapper.ReportMapper;
import co.com.bancolombia.model.common.gateways.TraceLoggerPort;
import co.com.bancolombia.model.report.gateways.ReportCounterPort;
import co.com.bancolombia.usecase.ReportUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockJwt;


@WebFluxTest
@ContextConfiguration(classes = {CorsConfig.class, ReportRouter.class, ReportHandler.class})
class CorsConfigTest {
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


    @BeforeEach
    void setUp() {

        webTestClient = webTestClient.mutateWith(mockJwt());
    }

    @Test
    void testCorsRequestFromForbiddenOrigin() {
        webTestClient.post().uri("/api/v1/usuarios")
                .header(HttpHeaders.ORIGIN, "https://forbidden-domain.com")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void testPreflightFromForbiddenOrigin_isForbidden() {
        webTestClient.method(org.springframework.http.HttpMethod.OPTIONS)
                .uri("/api/v1/usuarios")
                .header(HttpHeaders.ORIGIN, "https://forbidden-domain.com")
                .header(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD, "POST")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void testActualRequestFromForbiddenOrigin_noCorsHeaders() {
        webTestClient.post().uri("/api/v1/usuarios")
                .header(HttpHeaders.ORIGIN, "https://forbidden-domain.com")
                .exchange()
                .expectHeader().doesNotExist("Access-Control-Allow-Origin");
    }
}