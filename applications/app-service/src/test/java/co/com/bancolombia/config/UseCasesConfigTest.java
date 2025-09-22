package co.com.bancolombia.config;

import co.com.bancolombia.model.common.gateways.TraceLoggerPort;
import co.com.bancolombia.model.report.gateways.ReportCounterPort;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestConfiguration
@Import(UseCasesConfig.class)
class UseCasesConfigTest {

    @Test
    void testUseCaseBeansExist() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(this.getClass())) {
            String[] beanNames = context.getBeanDefinitionNames();

            boolean useCaseBeanFound = false;
            for (String beanName : beanNames) {
                if (beanName.endsWith("UseCase")) {
                    useCaseBeanFound = true;
                    break;
                }
            }

            assertTrue(useCaseBeanFound, "No beans ending with 'Use Case' were found");
        }
    }

    @Bean
    public ReportCounterPort reportCounterPort() {
        return Mockito.mock(ReportCounterPort.class);
    }

    @Bean
    public TraceLoggerPort traceLoggerPort() {
        return Mockito.mock(TraceLoggerPort.class);
    }
}