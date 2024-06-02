package observability.otel;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongUpDownCounter;
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
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@Aspect
@Component
public class ObservabilityAspect {
    private final LongCounter requestCounter;
    private final LongUpDownCounter dataVolumeCounter;
    private final AtomicLong lastMemoryUsage = new AtomicLong();
    private final AtomicLong serviceStartTime = new AtomicLong();
    private final Meter meter;


    @Autowired
    private SpanAttributesService spanAttributesService;

    @Autowired
    public ObservabilityAspect(OpenTelemetry openTelemetry) {
            this.meter = openTelemetry.getMeter(OtelApplication.class.getName());

            this.requestCounter = meter.counterBuilder("observability_requests_total")
                    .setDescription("Total number of requests")
                    .setUnit("requests")
                    .build();

            this.dataVolumeCounter = meter.upDownCounterBuilder("observability_data_volume")
                    .setDescription("Data volume processed by the service")
                    .setUnit("bytes")
                    .build();

            this.meter.gaugeBuilder("observability_memory_usage")
                    .ofLongs()
                    .setDescription("Current JVM memory usage")
                    .setUnit("bytes")
                    .buildWithCallback(measurement -> {
                        long memoryUsage = lastMemoryUsage.get();
                        Attributes attributes = Attributes.builder()
                                .put(AttributeKey.stringKey("method"), "last_method")
                                .put(AttributeKey.stringKey("spanId"), "last_span_id")
                                .put(AttributeKey.stringKey("type"), "heap")
                                .build();
                        measurement.record(memoryUsage, attributes);
                    });
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

        long startTime = Instant.now().toEpochMilli();
        serviceStartTime.set(startTime);

        Object[] args = joinPoint.getArgs();
        long inputDataSize = getDataSize(args);

        Object proceed = joinPoint.proceed();

        long outputDataSize = getDataSize(proceed);
        long totalDataVolume = inputDataSize + outputDataSize;

        requestCounter.add(1, Attributes.builder().put(AttributeKey.stringKey("method"), methodName.getName()).build());
        SpanContext spanContext = Span.current().getSpanContext();

        long memoryUsage = getMemoryUsage();
        lastMemoryUsage.set(memoryUsage);

        dataVolumeCounter.add(totalDataVolume, Attributes.builder()
                .put(AttributeKey.stringKey("method"), methodName.getName())
                .put(AttributeKey.stringKey("spanId"), spanContext.getSpanId())
                .build());

        Attributes attributes = Attributes.builder()
                .put(AttributeKey.stringKey("method"), methodName.getName())
                .put(AttributeKey.stringKey("spanId"), spanContext.getSpanId())
                .put(AttributeKey.stringKey("type"), "heap")
                .build();
        this.meter.gaugeBuilder("observability_memory_usage")
                .ofLongs()
                .setDescription("Current JVM memory usage")
                .setUnit("bytes")
                .buildWithCallback(measurement -> measurement.record(memoryUsage, attributes));

        return proceed;
    }

    @Before("@annotation(observability.otel.annotation.ObservabilityParam)")
    public void logBefore(JoinPoint joinPoint) {
    }

    @After("@annotation(observability.otel.annotation.ObservabilityParam)")
    public void logAfter(JoinPoint joinPoint) {
        lastMemoryUsage.set(0);
        serviceStartTime.set(0);
    }

    private long getDataSize(Object data) {
        if (data instanceof byte[]) {
            return ((byte[]) data).length;
        } else if (data instanceof String) {
            return ((String) data).getBytes().length;
        } else if (data instanceof Serializable) {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(data);
                oos.flush();
                return bos.toByteArray().length;
            } catch (IOException e) {
                e.printStackTrace();
                return 0;
            }
        }
        return 0;
    }

    private static long getMemoryUsage() {
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }
}
