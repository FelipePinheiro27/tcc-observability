package observability.otel.controller;

import observability.otel.annotation.ObservabilityParam;
import observability.otel.annotation.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class Controller {
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
}
