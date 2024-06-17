package tcc.metrics.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import tcc.metrics.AllMetrics;
import tcc.metrics.GeneralMetrics;
import tcc.metrics.PrometheusMetrics;
import tcc.metrics.service.MetricDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/metrics")
public class ObservabilityController {
    @Autowired
    private MetricDataService metricDataService;
    @GetMapping("/system-info")
    public GeneralMetrics getSystemInfo() {
        return metricDataService.getSystemMetrics();
    }

    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping("all")
    public List<AllMetrics> getAllServices() {
        return metricDataService.getAllMetrics();
    }

    @GetMapping("prometheus-metrics")
    public PrometheusMetrics getPrometheusMetrics() {
        return metricDataService.getPrometheusMetric();
    }
}
