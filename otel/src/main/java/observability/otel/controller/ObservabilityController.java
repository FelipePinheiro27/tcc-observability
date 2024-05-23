package observability.otel.controller;

import observability.otel.ErrorStatistics;
import observability.otel.annotation.ObservabilityParam;
import observability.otel.annotation.Param;
import observability.otel.service.MetricDataService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/observability")
public class ObservabilityController {
    @Autowired
    private MetricDataService metricDataService;
    @GetMapping("/testGet/{name}")
    @ResponseStatus(HttpStatus.OK)
    @ObservabilityParam(params = {
            @Param(key = "cpuStorage", value = 10),
            @Param(key = "memory", value = 20),
            @Param(key = "responseTime", value = 55)
    })
    public ResponseEntity<String> testGetEndPoint(@PathVariable String name) {
        if (name == null || name.isEmpty()) {
            return new ResponseEntity<>("Nome não pode ser vazio", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("ola", HttpStatus.OK);
    }

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
