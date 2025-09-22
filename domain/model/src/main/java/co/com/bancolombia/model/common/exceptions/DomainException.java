package co.com.bancolombia.model.common.exceptions;

import co.com.bancolombia.model.common.codes.BusinessCode;
import co.com.bancolombia.model.common.codes.BusinessCoded;
import lombok.Getter;

@Getter
public abstract class DomainException extends RuntimeException  implements BusinessCoded {
    private final BusinessCode code;

    protected DomainException(BusinessCode code, String message) {
        super(message);
        this.code = code;
    }
}
