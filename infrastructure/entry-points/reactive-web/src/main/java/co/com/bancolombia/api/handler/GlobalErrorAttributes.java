package co.com.bancolombia.api.handler;

import co.com.bancolombia.model.common.codes.BusinessCode;
import co.com.bancolombia.model.common.codes.BusinessCoded;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.resource.NoResourceFoundException;
import org.springframework.web.server.ServerWebInputException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class GlobalErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(ServerRequest request, ErrorAttributeOptions options) {
        Throwable ex = getError(request);
        Resolved r = resolve(ex);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        body.put("path", request.path());
        body.put("code", r.code.getCode());
        body.put("message", r.message);
        return body;
    }

    private Resolved resolve(Throwable ex) {

        if (ex instanceof BusinessCoded bcx) {
            BusinessCode bc = bcx.getCode();
            return new Resolved(HttpStatus.resolve(bc.getDefaultStatus()), bc, ex.getMessage() != null ? ex.getMessage() : bc.getLog());
        }

        if (ex instanceof WebExchangeBindException bind) {
            String msg = "errors: " + bind.getBindingResult().getAllErrors()
                    .stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
            return new Resolved(HttpStatus.BAD_REQUEST, BusinessCode.E400001, msg);
        }
        if (ex instanceof ConstraintViolationException cve) {
            return new Resolved(HttpStatus.UNPROCESSABLE_ENTITY, BusinessCode.E422000, cve.getMessage());
        }

        if (ex instanceof ServerWebInputException swi) {
            if (swi.getCause() instanceof DecodingException de && de.getCause() instanceof InvalidFormatException ife) {
                String field = fieldName(ife);
                String val   = String.valueOf(ife.getValue());
                String type  = ife.getTargetType() != null ? ife.getTargetType().getSimpleName() : "Unknown";
                String msg   = "The value '%s' for field '%s' is not a valid format for data type '%s'."
                        .formatted(val, field, type);
                return new Resolved(HttpStatus.BAD_REQUEST, BusinessCode.E400002, msg);
            }
            return new Resolved(HttpStatus.BAD_REQUEST, BusinessCode.E400002, "Format error in the request body.");
        }


        if (ex instanceof NoResourceFoundException) {
            return new Resolved(HttpStatus.METHOD_NOT_ALLOWED, BusinessCode.E405000, "The requested resource was not found.");
        }

        String msg = ex.getMessage() != null ? ex.getMessage() : BusinessCode.E500000.getLog();
        return new Resolved(HttpStatus.INTERNAL_SERVER_ERROR, BusinessCode.E500000, msg);
    }

    private String fieldName(InvalidFormatException ife) {
        String ref = ife.getPathReference();
        if (ref != null && ref.contains("\"")) return ref.split("\"")[1];
        return "unknown";
    }

    private record Resolved(HttpStatus status, BusinessCode code, String message) {}
}
