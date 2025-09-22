package co.com.bancolombia.api.model;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public record ApiResponse<T>(T data, String message, String timestamp) {

    public ApiResponse(T data) {
        this(data, "Ok", OffsetDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
