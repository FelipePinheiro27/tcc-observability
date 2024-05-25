package observability.otel.controller;

import observability.otel.GeneralMetrics;
import observability.otel.annotation.ObservabilityParam;
import observability.otel.annotation.Param;
import observability.otel.service.MetricDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class Controller {
    @Autowired
    private MetricDataService metricDataService;

    @GetMapping("/testGet/{name}")
    @ResponseStatus(HttpStatus.OK)
    @ObservabilityParam(params = {
            @Param(key = "cpuStorage", value = "10"),
            @Param(key = "memory", value = "20"),
            @Param(key = "responseTime", value = "55")
    })
    public ResponseEntity<String> testGetEndPoint(@PathVariable String name) {
        if (name == null || name.isEmpty()) {
            return new ResponseEntity<>("Nome não pode ser vazio", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("ola", HttpStatus.OK);
    }

    @GetMapping("/test")
    public Map<String, GeneralMetrics[]> getTest () {
        Map<String, GeneralMetrics[]> metric = metricDataService.getConnectWithPrometheus();

        return metric;
    }
}
