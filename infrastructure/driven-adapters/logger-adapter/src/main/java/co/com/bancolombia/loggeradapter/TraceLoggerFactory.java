package co.com.bancolombia.loggeradapter;

import co.com.bancolombia.model.common.gateways.TraceLoggerPort;
import org.springframework.stereotype.Component;

@Component
public class TraceLoggerFactory {
    public TraceLoggerPort getLogger() {
        return new TraceLoggerAdapter();
    }
}