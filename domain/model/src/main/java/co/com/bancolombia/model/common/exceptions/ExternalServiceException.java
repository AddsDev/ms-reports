package co.com.bancolombia.model.common.exceptions;

import co.com.bancolombia.model.common.codes.BusinessCode;

public class ExternalServiceException extends DomainException {
    public ExternalServiceException(String message) {
        super(BusinessCode.E502000, message);
    }
}
