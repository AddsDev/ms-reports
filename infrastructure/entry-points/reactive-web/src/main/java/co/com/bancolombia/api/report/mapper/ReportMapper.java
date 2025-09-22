package co.com.bancolombia.api.report.mapper;

import co.com.bancolombia.api.model.ApiResponse;
import co.com.bancolombia.api.report.dto.ReportResponse;
import co.com.bancolombia.model.report.ReportSummary;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ReportMapper {

    ReportResponse toReportResponse(ReportSummary reportSummary);

    default ApiResponse<ReportResponse> toResponse(ReportSummary summary) {
        return new ApiResponse<>(toReportResponse(summary));
    }
}
