package observability.otel;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.DoubleCounter;
import io.opentelemetry.api.metrics.DoubleCounterBuilder;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import observability.otel.annotation.ObservabilityParam;
import observability.otel.annotation.Param;
import observability.otel.service.SpanAttributesService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
public class ObservabilityAspect {
    private final LongCounter requestCounter;
    private final LongCounter memoryUsageCounter;
    private final Meter meter;
    private double networkFirstTransferData;
    private static final String PROMETHEUS_API_URL_QUERY = "http://172.17.0.1:9090/api/v1/query?query=";

    @Autowired
    private SpanAttributesService spanAttributesService;

    public JSONArray getPrometheusMetric(String prometheusUrl) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(prometheusUrl);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(jsonResponse);
                JSONArray resultArray = jsonObject.getJSONObject("data").getJSONArray("result");

                return resultArray;
            }
        }
        catch (Exception e) {
            System.err.println("Error: " + e);
        }

        return new JSONArray();
    }

    @Autowired
    public ObservabilityAspect(OpenTelemetry openTelemetry) {
        this.meter = openTelemetry.getMeter(OtelApplication.class.getName());

        this.requestCounter = meter.counterBuilder("observability_requests_total")
                .setDescription("Total number of requests")
                .setUnit("requests")
                .build();

        this.memoryUsageCounter = meter.counterBuilder("observability_memory_usage")
                .setDescription("Current JVM memory usage")
                .setUnit("bytes")
                .build();
    }

    @Around("@annotation(observabilityParam)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, ObservabilityParam observabilityParam) throws Throwable {
        Param[] params = observabilityParam.params();
        Method methodName = spanAttributesService.getMethod(joinPoint);

        for (Param param : params) {
            String key = param.key();
            String value = String.valueOf(param.value());
            Span.current().setAttribute(AttributeKey.stringKey(key), value);
        }

        Span.current().setAttribute("serviceName", methodName.getName());

        Object proceed = joinPoint.proceed();

        SpanContext spanContext = Span.current().getSpanContext();
        Attributes attributes = Attributes.builder()
                .put(AttributeKey.stringKey("method"), methodName.getName())
                .put(AttributeKey.stringKey("spanId"), spanContext.getSpanId())
                .build();

        double cpuUsage = getCpuUsage();
        long memoryUsage = getMemoryUsage();

        requestCounter.add(1, attributes);
        memoryUsageCounter.add(memoryUsage, attributes);
        this.meter.gaugeBuilder("observability_cpu_usage")
                .setDescription("Current JVM memory usage")
                .setUnit("percentage")
                .buildWithCallback(measurement -> measurement.record(cpuUsage, attributes));

        return proceed;
    }

    @Before("@annotation(observability.otel.annotation.ObservabilityParam)")
    public void logBefore(JoinPoint joinPoint) {
        networkFirstTransferData = getSumNetworkIo();
    }

    @After("@annotation(observability.otel.annotation.ObservabilityParam)")
    public void logAfter(JoinPoint joinPoint) {
        double secondTransferData = getSumNetworkIo();
        double throughput = (secondTransferData - networkFirstTransferData)/10;
        System.out.println("secondTransferData: " + secondTransferData);
        System.out.println("networkFirstTransferData: " + networkFirstTransferData);
        System.out.println("secondTransferData - networkFirstTransferData: " + (secondTransferData - networkFirstTransferData));
        SpanContext spanContext = Span.current().getSpanContext();
        Attributes attributes = Attributes.builder()
                .put(AttributeKey.stringKey("spanId"), spanContext.getSpanId())
                .build();

        this.meter.gaugeBuilder("observability_throughput")
                .setUnit("bytes")
                .buildWithCallback(measurement -> measurement.record(throughput, attributes));
    }

    private double getSumNetworkIo() {
        JSONArray networkArray = getPrometheusMetric(PROMETHEUS_API_URL_QUERY + "system_network_io_bytes_total");
        double receive = 0, transmit = 0;

        for (int i = 0; i < networkArray.length(); i++) {
            JSONObject result = networkArray.getJSONObject(i);
            String resultDevice = result.getJSONObject("metric").getString("device");
            String resultDirection = result.getJSONObject("metric").getString("direction");

            if (resultDevice.equals("eth0") && resultDirection.equals("receive")) {
                receive = result.getJSONArray("value").getDouble(1);
            }
            if (resultDevice.equals("eth0") && resultDirection.equals("transmit")) {
                transmit = result.getJSONArray("value").getDouble(1);
            }
        }

        return receive + transmit;
    }

    private long getMemoryUsage() {
        JSONArray memoryArray = getPrometheusMetric(PROMETHEUS_API_URL_QUERY + "system_memory_usage_bytes");
        long usedValue = -1;
        for (int i = 0; i < memoryArray.length(); i++) {
            JSONObject jsonObject = memoryArray.getJSONObject(i);
            JSONObject metric = jsonObject.getJSONObject("metric");
            String state = metric.getString("state");

            if ("used".equals(state)) {
                JSONArray value = jsonObject.getJSONArray("value");
                usedValue = value.getLong(1);
                break;
            }
        }

        return usedValue;
    }

    private double getCpuUsage() {
        JSONArray cpusArray = getPrometheusMetric(PROMETHEUS_API_URL_QUERY + "system_cpu_time_seconds_total");
        double cpuUsage = -1;

        Map<String, Double> cpuIdleTimes = new HashMap<>();
        Map<String, Double> cpuTotalTimes = new HashMap<>();

        for (int i = 0; i < cpusArray.length(); i++) {
            JSONObject result = cpusArray.getJSONObject(i);
            String cpu = result.getJSONObject("metric").getString("cpu");
            String state = result.getJSONObject("metric").getString("state");
            double value = result.getJSONArray("value").getDouble(1);

            if (state.equals("idle")) {
                cpuIdleTimes.put(cpu, value);
            }

            cpuTotalTimes.put(cpu, cpuTotalTimes.getOrDefault(cpu, 0.0) + value);
        }

        double totalIdleTime = cpuIdleTimes.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalTime = cpuTotalTimes.values().stream().mapToDouble(Double::doubleValue).sum();
        cpuUsage = 100 * (1 - (totalIdleTime / totalTime));

        return Math.round(cpuUsage * 100.0) / 100.0;
    }
}
