package observability.otel;

import com.sun.management.OperatingSystemMXBean;
import io.opentelemetry.api.OpenTelemetry;
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

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.lang.reflect.Method;
import java.util.function.ToDoubleFunction;

@Aspect
@Component
public class ObservabilityAspect {
    private final LongCounter requestCounter;
    private final LongCounter dataTransferCounter;
    private final LongCounter memoryUsageCounter;

    @Autowired
    private SpanAttributesService spanAttributesService;

    @Autowired
    public ObservabilityAspect(OpenTelemetry openTelemetry) {
        Meter meter = openTelemetry.getMeter(OtelApplication.class.getName());

        requestCounter = meter.counterBuilder("app_requests_total")
                .setDescription("Total number of requests")
                .setUnit("requests")
                .build();

        dataTransferCounter = meter.counterBuilder("app_data_transferred_bytes_total")
                .setDescription("Total bytes transferred")
                .setUnit("bytes")
                .build();

        memoryUsageCounter = meter.counterBuilder("app_memory_usage_bytes")
                .setDescription("Memory usage of the application")
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
            Span.current().setAttribute(key, value);
        }

        Span.current().setAttribute("serviceName", methodName.getName());

        Object proceed = joinPoint.proceed();

        // Increment the request counter
        requestCounter.add(1, Attributes.builder().put("method", methodName.getName()).build());

        return proceed;
    }

    @Before("@annotation(observability.otel.annotation.ObservabilityParam)")
    public void logBefore(JoinPoint joinPoint) {
        // Código para ser executado antes da execução do método anotado
    }

    @After("@annotation(observability.otel.annotation.ObservabilityParam)")
    public void logAfter(JoinPoint joinPoint) {
        SpanContext spanContext = Span.current().getSpanContext();
        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();
        long usedMemory = heapMemoryUsage.getUsed();

        memoryUsageCounter.add(usedMemory);
    }
}
