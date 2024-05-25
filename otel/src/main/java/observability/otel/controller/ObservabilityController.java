package observability.otel.controller;

import observability.otel.ErrorStatistics;
import observability.otel.service.MetricDataService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/observability")
public class ObservabilityController {
    @Autowired
    private MetricDataService metricDataService;

    @GetMapping("/metrics/errors")
    public String getMetrics() {
        ErrorStatistics errorStatistics = metricDataService.getErrorCount();
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("fetchs", errorStatistics.getTotalCalls());
        jsonResponse.put("errors", errorStatistics.getTotalErrors());
        return jsonResponse.toString();
    }

    @GetMapping("/metrics/errors/service/{serviceName}")
    public String getMetricsByServiceName(@PathVariable String serviceName) {
        ErrorStatistics errorStatistics = metricDataService.getErrorCountByServiceName(serviceName);
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("specific fetchs", errorStatistics.getTotalCalls());
        jsonResponse.put("specific errors", errorStatistics.getTotalErrors());
        return jsonResponse.toString();
    }

    @GetMapping("/metrics/requests")
    public String getRequests() {
        double value = metricDataService.getRequestCountBySecond();
        JSONObject jsonResponse = new JSONObject();
        jsonResponse.put("requests by second: ", value);
        return jsonResponse.toString();
    }
}
