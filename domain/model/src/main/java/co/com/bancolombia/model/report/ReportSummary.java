package co.com.bancolombia.model.report;

import java.math.BigDecimal;

public record ReportSummary(Long approvedTotal, BigDecimal approvedAmountTotal, String updatedAt) {}
