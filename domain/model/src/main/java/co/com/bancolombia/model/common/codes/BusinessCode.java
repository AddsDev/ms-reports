package co.com.bancolombia.model.common.codes;

public enum BusinessCode {
    S200000("S200-000", "Operation carried out successfully", 200),

    E401000("E401-000", "Unauthorized", 401),
    E403000("E403-000", "Forbidden", 403),

    E400000("E400-000", "Bad request", 400),
    E400001("E400-001", "Invalid request data", 400),
    E400002("E400-002", "Invalid format", 400),
    E422000("E422-000", "Unprocessable entity", 422),

    E404000("E404-000", "Resource not found", 404),
    E405000("E405-000", "Method not allowed", 405),
    E409000("E409-000", "Conflict", 409),
    E410000("E410-000", "Resource is gone/expired", 410),
    E429000("E429-000", "Too many requests / throttling", 429),

    E502000("E502-000", "Bad gateway / External service error", 502),
    E503000("E503-000", "Service unavailable", 503),

    E500000("E500-000", "Internal server error", 500);

    private final String code;
    private final String log;
    private final int defaultStatus;

    BusinessCode(String code, String log, int defaultStatus) {
        this.code = code;
        this.log = log;
        this.defaultStatus = defaultStatus;
    }
    public String getCode() { return code; }
    public String getLog() { return log; }
    public int getDefaultStatus() { return defaultStatus; }
}
