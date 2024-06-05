package tcc.metrics.controller;

import tcc.metrics.AllMetrics;
import tcc.metrics.GeneralMetrics;
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
        GeneralMetrics generalMetrics = metricDataService.getSystemMetrics();

        return generalMetrics;
    }

    @GetMapping("all")
    public List<AllMetrics> getAllServices() {
        List<AllMetrics> metrics = metricDataService.getAllMetrics();

        return metrics;

    }

    @GetMapping("test")
    public String test() {
        return "testando";
    }
}
