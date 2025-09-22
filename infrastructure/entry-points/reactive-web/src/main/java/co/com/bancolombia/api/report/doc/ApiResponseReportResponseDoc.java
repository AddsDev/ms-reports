package co.com.bancolombia.api.report.doc;

import co.com.bancolombia.api.report.dto.ReportResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "ApiResponse_ReportResponse", description = "Endpoint para generar reportes")
public class ApiResponseReportResponseDoc {
    @Schema(description = "Payload de la respuesta")
    public ReportResponse data;

    @Schema(description = "Código interno o de negocio", example = "OK")
    public String code;

    @Schema(description = "Mensaje de la respuesta", example = "Success")
    public String message;
}
