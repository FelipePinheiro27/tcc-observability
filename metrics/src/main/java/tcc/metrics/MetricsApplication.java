package tcc.metrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import tcc.metrics.service.MetricDataService;
import tcc.metrics.service.impl.MetricDataServiceImpl;

@SpringBootApplication
public class MetricsApplication {

	public static void main(String[] args) { SpringApplication.run(MetricsApplication.class, args);}

	@Bean
	public MetricDataService metricDataService() {
		return new MetricDataServiceImpl();
	}
}
