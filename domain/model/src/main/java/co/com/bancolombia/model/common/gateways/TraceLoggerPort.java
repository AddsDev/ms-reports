package co.com.bancolombia.model.common.gateways;

public interface TraceLoggerPort {

    void trace(String message);

    void trace(String message, Object... args);

    void info(String message);

    void info(String message, Object... args);

    void warn(String message);

    void warn(String message, Object... args);

    void error(String message);

    void error(String message, Object... args);

    void error(String message, Throwable t);

    boolean isTraceEnabled();

    boolean isDebugEnabled();
}
