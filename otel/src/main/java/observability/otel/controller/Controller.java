package observability.otel.controller;

import observability.otel.annotation.ObservabilityParam;
import observability.otel.annotation.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class Controller {
    @GetMapping("/testGet/{name}")
    @ResponseStatus(HttpStatus.OK)
    @ObservabilityParam(params = {
            @Param(key = "cpuStorage", value = "10"),
            @Param(key = "memory", value = "20"),
            @Param(key = "responseTime", value = "2")
    })
    public ResponseEntity<String> testGetEndPoint(@PathVariable String name) {
        if (name == null || name.isEmpty()) {
            return new ResponseEntity<>("Nome não pode ser vazio", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>("ola", HttpStatus.OK);
    }

    @GetMapping("/memory")
    @ResponseStatus(HttpStatus.OK)
    @ObservabilityParam(params = {
            @Param(key = "cpuStorage", value = "10"),
            @Param(key = "memory", value = "20"),
            @Param(key = "responseTime", value = "55")
    })
    public String testando() {
        List<LargeObject> largeObjectList = new ArrayList<>();

        for (int i = 0; i < 1000; i++) { // Ajuste o valor para aumentar o consumo de memória
            largeObjectList.add(new LargeObject("Data " + i, new byte[1024])); // Cada objeto terá 1 KB de dados
        }

        // Simula o uso da lista para evitar que o compilador otimize o código
        long totalSize = largeObjectList.size();

        return "Created " + totalSize + " large objects";
    }

    @GetMapping("/metrica")
    @ResponseStatus(HttpStatus.OK)
    @ObservabilityParam(params = {
            @Param(key = "cpuStorage", value = "10"),
            @Param(key = "memory", value = "20"),
            @Param(key = "responseTime", value = "55")
    })
    public String metrica() throws InterruptedException {
        List<LargeObject> largeObjectList = new ArrayList<>();

        for (int i = 0; i < 100000; i++) {
            largeObjectList.add(new LargeObject("Data " + i, new byte[1024]));
        }

        Thread.sleep(5000);

        long totalSize = largeObjectList.size();

        return "Created " + totalSize + " large objects";
    }

    @GetMapping("/large-metrica")
    @ResponseStatus(HttpStatus.OK)
    @ObservabilityParam(params = {
            @Param(key = "cpuStorage", value = "10"),
            @Param(key = "memory", value = "20"),
            @Param(key = "responseTime", value = "55")
    })
    public String metricaLarge() throws InterruptedException {
        List<LargeObject> largeObjectList = new ArrayList<>();

        for (int i = 0; i < 599000; i++) {
            largeObjectList.add(new LargeObject("Data " + i, new byte[1024]));
        }

        Thread.sleep(5000);
        long totalSize = largeObjectList.size();

        return "Created " + totalSize + " large objects";
    }

    static class LargeObject {
        private String name;
        private byte[] data;

        public LargeObject(String name, byte[] data) {
            this.name = name;
            this.data = data;
        }

    }

    @GetMapping("/transfer")
    @ObservabilityParam(params = {
            @Param(key = "cpuStorage", value = "10"),
            @Param(key = "memory", value = "20"),
            @Param(key = "responseTime", value = "55")
    })    public String transfer(@RequestParam(value = "size", defaultValue = "1024") int size) {
        return generateData(size);
    }

    private String generateData(int size) {
        StringBuilder sb = new StringBuilder(size);
        for (int i = 0; i < size; i++) {
            sb.append('a');
        }
        return sb.toString();
    }
}
