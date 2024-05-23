package observability.otel;

import observability.otel.service.MetricDataService;
import observability.otel.service.impl.MetricDataServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "observability.otel")
public class AopConfig {
    @Bean
    public MetricDataService metricDataService() {
        return new MetricDataServiceImpl();
    }
}
