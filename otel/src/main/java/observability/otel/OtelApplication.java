package observability.otel;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OtelApplication {

	public static void main(String[] args) {
		SpringApplication.run(OtelApplication.class, args);
	}

	@Bean
	public OpenTelemetry openTelemetry() {
		return GlobalOpenTelemetry.get();
	}
}