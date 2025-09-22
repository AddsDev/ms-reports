package co.com.bancolombia.model.common.exceptions;

import co.com.bancolombia.model.common.codes.BusinessCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DomainExceptionTest {
    @Test
    void authenticationExceptionShouldCarryE401000() {
        AuthenticationException ex = new AuthenticationException("no auth");
        assertEquals(BusinessCode.E401000, ex.getCode());
        assertEquals("no auth", ex.getMessage());
    }

    @Test
    void externalServiceExceptionShouldCarryE502000() {
        ExternalServiceException ex = new ExternalServiceException("downstream failed");
        assertEquals(BusinessCode.E502000, ex.getCode());
        assertEquals("downstream failed", ex.getMessage());
    }
}