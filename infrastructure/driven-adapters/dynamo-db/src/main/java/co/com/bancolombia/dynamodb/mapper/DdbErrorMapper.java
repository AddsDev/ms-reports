package co.com.bancolombia.dynamodb.mapper;

import co.com.bancolombia.model.common.codes.BusinessCode;
import co.com.bancolombia.model.common.exceptions.DomainException;
import co.com.bancolombia.model.common.exceptions.ExternalServiceException;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.dynamodb.model.*;

public class DdbErrorMapper {
    private DdbErrorMapper() {
    }

    public static DomainException toDomain(Throwable t) {
        if (t instanceof DomainException de) return de; // ya mapeada

        if (t instanceof DynamoDbException ddb) {
            BusinessCode bc = mapBusinessCode(ddb);
            String msg = (ddb.getMessage() != null) ? ddb.getMessage() : bc.getLog();
            return new ExternalServiceException(msg) {
                @Override
                public BusinessCode getCode() {
                    return bc;
                }
            };
        }

        if (t instanceof SdkClientException sce) {
            BusinessCode bc = BusinessCode.E502000;
            String msg = (sce.getMessage() != null) ? sce.getMessage() : bc.getLog();
            return new ExternalServiceException(msg) {
                @Override
                public BusinessCode getCode() {
                    return bc;
                }
            };
        }

        BusinessCode bc = BusinessCode.E502000;
        String msg = (t.getMessage() != null) ? t.getMessage() : bc.getLog();
        return new ExternalServiceException(msg) {
            @Override
            public BusinessCode getCode() {
                return bc;
            }
        };
    }

    public static BusinessCode mapBusinessCode(DynamoDbException dbException) {
        if (dbException instanceof ResourceNotFoundException || dbException instanceof TableNotFoundException)
            return BusinessCode.E404000;
        if (dbException instanceof ConditionalCheckFailedException) return BusinessCode.E409000;
        if (dbException instanceof TransactionConflictException || dbException instanceof ResourceInUseException || dbException instanceof TableInUseException)
            return BusinessCode.E409000;
        if (dbException instanceof ThrottlingException) return BusinessCode.E429000;
        if (dbException instanceof InternalServerErrorException) return BusinessCode.E502000;
        if (dbException instanceof ExpiredIteratorException || dbException instanceof TrimmedDataAccessException)
            return BusinessCode.E410000;
        return BusinessCode.E502000;
    }
}
