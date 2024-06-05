package observability.otel.controller;

import observability.otel.AllMetrics;
import observability.otel.GeneralMetrics;
import observability.otel.service.MetricDataService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/observability")
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
}
