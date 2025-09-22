package co.com.bancolombia.api;

import co.com.bancolombia.api.report.doc.ApiResponseReportResponseDoc;
import co.com.bancolombia.api.report.dto.ReportResponse;
import co.com.bancolombia.api.report.handler.ReportHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@Tag(name = "Reportes", description = "Operaciones de reportes")
public class ReportRouter {
    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/reportes",
                    beanClass = ReportHandler.class,
                    beanMethod = "showReport",
                    operation = @Operation(
                            summary = "Mostrar reporte agregado",
                            description = "Retorna totales aprobados, monto total aprobado y fecha de actualización",
                            tags = {"Reportes"},
                            security = { @SecurityRequirement(name = "bearerAuth") },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "OK",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(implementation = ApiResponseReportResponseDoc.class)
                                            )
                                    ),
                                    @ApiResponse(responseCode = "401", description = "No autorizado"),
                                    @ApiResponse(responseCode = "403", description = "Prohibido"),
                                    @ApiResponse(responseCode = "500", description = "Error interno")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> reportRoutes(ReportHandler reportHandler) {
        return route()
                .path("/api/v1", builder ->
                        builder.GET("/reportes", reportHandler::showReport)
                )
                .build();
    }
}
