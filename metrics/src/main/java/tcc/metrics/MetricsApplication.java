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

	public static void main(String[] args) { SpringApplication.run(MetricsApplication.class, args);
		// Create a SystemInfo object
		SystemInfo systemInfo = new SystemInfo();

		// Schedule memory monitoring
		int monitoringInterval = 5000; // 5 seconds

		while (true) {
			// Get the memory information
			GlobalMemory memory = systemInfo.getHardware().getMemory();

			// Get total memory, available memory, and used memory
			long totalMemory = memory.getTotal();
			long availableMemory = memory.getAvailable();
			long usedMemory = totalMemory - availableMemory;

			// Print memory information in bytes
			System.out.println("Total memory in bytes: " + totalMemory);
			System.out.println("Available memory in bytes: " + availableMemory);
			System.out.println("Used memory in bytes: " + usedMemory);

			// Print memory information in gigabytes
			System.out.println("Total memory in gigabytes: " + bytesToGigabytes(totalMemory) + " GB");
			System.out.println("Available memory in gigabytes: " + bytesToGigabytes(availableMemory) + " GB");
			System.out.println("Used memory in gigabytes: " + bytesToGigabytes(usedMemory) + " GB");

			try {
				Thread.sleep(monitoringInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static double bytesToGigabytes(long bytes) {
		return bytes / 1024.0 / 1024.0 / 1024.0;
	}
	@Bean
	public MetricDataService metricDataService() {
		return new MetricDataServiceImpl();
	}
}
