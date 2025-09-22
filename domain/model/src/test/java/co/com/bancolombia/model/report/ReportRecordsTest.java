package co.com.bancolombia.model.report;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ReportRecordsTest {
    @Test
    void reportIncrementEventAccessorsWork() {
        var e = new ReportIncrementEvent(new BigDecimal("123.45"), "L-1", "2025-09-20T10:00:00Z");
        assertEquals(new BigDecimal("123.45"), e.amount());
        assertEquals("L-1", e.loanId());
        assertEquals("2025-09-20T10:00:00Z", e.approvedAt());
    }

    @Test
    void reportSummaryAccessorsWork() {
        var s = new ReportSummary(5L, new BigDecimal("999.99"), "2025-09-20T11:00:00Z");
        assertEquals(5L, s.approvedTotal());
        assertEquals(new BigDecimal("999.99"), s.approvedAmountTotal());
        assertEquals("2025-09-20T11:00:00Z", s.updatedAt());
    }

}