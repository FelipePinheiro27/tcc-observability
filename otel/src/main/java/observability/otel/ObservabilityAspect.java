package observability.otel;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import observability.otel.annotation.ObservabilityParam;
import observability.otel.annotation.Param;
import observability.otel.service.SpanAttributesService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class ObservabilityAspect {
    private final LongCounter requestCounter;
    private final LongCounter memoryUsageCounter;
    private long memoryUsageFirstValue;
    private final Meter meter;
    private double networkTransferDataFirstValue;
    private final Metric metric = new Metric();
    private String serviceName;
    @Autowired
    private SpanAttributesService spanAttributesService;

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
        serviceName = methodName.getName();

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

        networkTransferDataFirstValue = metric.getSumNetworkIo();

        double cpuUsage = metric.getCpuUsage();
        memoryUsageFirstValue = metric.getMemoryUsage();

        requestCounter.add(1, attributes);
        this.meter.gaugeBuilder("observability_cpu_usage")
                .setDescription("Current JVM memory usage")
                .setUnit("percentage")
                .buildWithCallback(measurement -> measurement.record(cpuUsage, attributes));

        return proceed;
    }

    @After("@annotation(observability.otel.annotation.ObservabilityParam)")
    public void logAfter(JoinPoint joinPoint) {
        double networkTransferDataSecondValue = metric.getSumNetworkIo();
        long memoryUsageSecondValue = metric.getMemoryUsage();
        double throughput = networkTransferDataSecondValue - networkTransferDataFirstValue;
        long memoryUsage = memoryUsageSecondValue - memoryUsageFirstValue;
        System.out.println("networkTransferDataSecondValue: " + networkTransferDataSecondValue);
        System.out.println("networkTransferDataFirstValue: " + networkTransferDataFirstValue);
        System.out.println("networkTransferDataSecondValue - networkTransferDataFirstValue: " + (networkTransferDataSecondValue - networkTransferDataFirstValue));
        SpanContext spanContext = Span.current().getSpanContext();
        Attributes attributes = Attributes.builder()
                .put(AttributeKey.stringKey("spanId"), spanContext.getSpanId())
                .put(AttributeKey.stringKey("serviceName"), serviceName)
                .build();

        this.meter.gaugeBuilder("observability_throughput")
                .setUnit("bytes")
                .buildWithCallback(measurement -> measurement.record(throughput, attributes));
        memoryUsageCounter.add(memoryUsage, attributes);
    }


}
