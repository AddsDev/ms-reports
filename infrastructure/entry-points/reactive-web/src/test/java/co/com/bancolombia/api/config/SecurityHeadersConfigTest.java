package co.com.bancolombia.api.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

class SecurityHeadersConfigTest {
    private WebTestClient buildClient(SecurityHeadersConfig filter) {
        RouterFunction<?> routes =
                RouterFunctions.route(RequestPredicates.GET("/ping"),
                        req -> ServerResponse.ok().contentType(MediaType.TEXT_PLAIN).bodyValue("pong"));

        return WebTestClient.bindToRouterFunction(routes)
                .webFilter(filter)
                .configureClient()
                .build();
    }

    @Test
    @DisplayName("El filtro debe agregar todos los headers de seguridad")
    void filterShouldAddAllSecurityHeaders() {
        WebTestClient client = buildClient(new SecurityHeadersConfig());

        client.get()
                .uri("/ping")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Security-Policy",
                        "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().valueEquals("Strict-Transport-Security", "max-age=31536000;")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().valueEquals("Server", "")
                .expectHeader().valueEquals("Cache-Control", "no-store")
                .expectHeader().valueEquals("Pragma", "no-cache")
                .expectHeader().valueEquals("Referrer-Policy", "strict-origin-when-cross-origin")
                .expectBody(String.class).isEqualTo("pong");
    }

}