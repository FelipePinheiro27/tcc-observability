package observability.otel;

import observability.otel.service.SpanAttributesService;
import observability.otel.service.impl.SpanAttributesServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = "observability.otel")
public class AopConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    @Bean
    public SpanAttributesService spanAttributesService() { return new SpanAttributesServiceImpl(); }
}
