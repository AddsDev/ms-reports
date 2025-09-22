package co.com.bancolombia.config;

import co.com.bancolombia.loggeradapter.TraceLoggerFactory;
import co.com.bancolombia.model.common.gateways.TraceLoggerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
@RequiredArgsConstructor
public class LoggerConfiguration {
    private final TraceLoggerFactory traceLoggerFactory;

    @Bean
    @Primary
    public TraceLoggerPort domainLayerLogger() {
        return traceLoggerFactory.getLogger();
    }

    @Bean("entryPointLogger")
    public TraceLoggerPort entryPointLogger() {
        return traceLoggerFactory.getLogger();
    }
}
