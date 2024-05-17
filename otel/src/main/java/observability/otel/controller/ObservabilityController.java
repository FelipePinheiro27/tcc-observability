package observability.otel.controller;

import observability.otel.annotation.ObservabilityParam;
import observability.otel.annotation.Param;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ObservabilityController {
    @GetMapping("/testGet/{name}")
    @ResponseStatus(HttpStatus.OK)
    @ObservabilityParam(params = {
            @Param(key = "cpuStorage", value = 10),
            @Param(key = "memory", value = 20),
            @Param(key = "responseTime", value = 55)
    })
    public String testGetEndPoint(@PathVariable String name) {
        return "ola";
    }
}
