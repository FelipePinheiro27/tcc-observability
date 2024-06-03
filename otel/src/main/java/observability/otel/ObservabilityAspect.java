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
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

@Aspect
@Component
public class ObservabilityAspect {
    private final LongCounter requestCounter;
    private final LongCounter dataVolumeCounter;
    private final LongCounter memoryUsageCounter;
    private final LongCounter cpuUsageCounter;
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

        this.dataVolumeCounter = meter.counterBuilder("observability_data_volume")
                .setDescription("Data volume processed by the service")
                .setUnit("bytes")
                .build();

        this.memoryUsageCounter = meter.counterBuilder("observability_memory_usage")
                .setDescription("Current JVM memory usage")
                .setUnit("bytes")
                .build();

        this.cpuUsageCounter = meter.counterBuilder("observability_cpu_usage")
                .setDescription("Current JVM CPU usage")
                .setUnit("percentage")
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

        long startTime = Instant.now().toEpochMilli();
        serviceStartTime.set(startTime);

        Object[] args = joinPoint.getArgs();
        long inputDataSize = getDataSize(args);

        Object proceed = joinPoint.proceed();

        long outputDataSize = getDataSize(proceed);
        long totalDataVolume = inputDataSize + outputDataSize;

        long memoryUsage = getMemoryUsage();
        lastMemoryUsage.set(memoryUsage);
        SpanContext spanContext = Span.current().getSpanContext();

        double cpuUsage = getCpuUsage();
        Attributes attributes = Attributes.builder()
                .put(AttributeKey.stringKey("method"), methodName.getName())
                .put(AttributeKey.stringKey("spanId"), spanContext.getSpanId())
                .build();

        requestCounter.add(1, attributes);

        dataVolumeCounter.add(totalDataVolume, attributes);

        memoryUsageCounter.add(memoryUsage, attributes);

        cpuUsageCounter.add((long) cpuUsage, attributes);

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

    private static double getCpuUsage() {
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
            return sunOsBean.getProcessCpuLoad() * 100;
        }
        return 0;
    }
}
