package co.com.bancolombia.model.common.codes;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BusinessCodeTest {

    @Test
    void shouldExposeCodeLogAndStatus() {
        BusinessCode ok = BusinessCode.S200000;
        assertEquals("S200-000", ok.getCode());
        assertEquals("Operation carried out successfully", ok.getLog());
        assertEquals(200, ok.getDefaultStatus());

        BusinessCode unauthorized = BusinessCode.E401000;
        assertEquals("E401-000", unauthorized.getCode());
        assertEquals("Unauthorized", unauthorized.getLog());
        assertEquals(401, unauthorized.getDefaultStatus());
    }

    @Test
    void allEnumValuesHaveNonNullFields() {
        for (BusinessCode bc : BusinessCode.values()) {
            assertNotNull(bc.getCode());
            assertNotNull(bc.getLog());
            assertTrue(bc.getDefaultStatus() >= 100 && bc.getDefaultStatus() < 600);
        }
    }
}