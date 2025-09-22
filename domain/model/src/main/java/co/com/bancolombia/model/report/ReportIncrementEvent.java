package co.com.bancolombia.model.report;

import java.math.BigDecimal;

public record ReportIncrementEvent(BigDecimal amount, String loanId, String approvedAt) {
}
