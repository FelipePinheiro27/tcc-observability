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
    private final Meter meter;
    private double networkFirstTransferData;
    private final Metric metric = new Metric();
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

        networkFirstTransferData = metric.getSumNetworkIo();

        double cpuUsage = metric.getCpuUsage();
        long memoryUsage = metric.getMemoryUsage();

        requestCounter.add(1, attributes);
        memoryUsageCounter.add(memoryUsage, attributes);
        this.meter.gaugeBuilder("observability_cpu_usage")
                .setDescription("Current JVM memory usage")
                .setUnit("percentage")
                .buildWithCallback(measurement -> measurement.record(cpuUsage, attributes));

        return proceed;
    }

    @After("@annotation(observability.otel.annotation.ObservabilityParam)")
    public void logAfter(JoinPoint joinPoint) {
        double secondTransferData = metric.getSumNetworkIo();
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


}
