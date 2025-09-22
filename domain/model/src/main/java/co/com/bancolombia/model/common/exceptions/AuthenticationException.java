package co.com.bancolombia.model.common.exceptions;

import co.com.bancolombia.model.common.codes.BusinessCode;

public class AuthenticationException extends DomainException {
    public AuthenticationException(String message) {
        super(BusinessCode.E401000, message);
    }
}
