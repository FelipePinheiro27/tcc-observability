package observability.otel;

import com.sun.management.OperatingSystemMXBean;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongHistogram;
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

@Aspect
@Component
public class ObservabilityAspect {
    private final LongHistogram observabilityHistogram;
    private final OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
    MemoryUsage heapMemoryUsage = memoryMXBean.getHeapMemoryUsage();

    private long startTime;
    private double cpuStorageBegan;
    private Runtime runtime;
    @Autowired
    private SpanAttributesService spanAttributesService;

    @Autowired
    public ObservabilityAspect(OpenTelemetry openTelemetry) {
        Meter meter = openTelemetry.getMeter(OtelApplication.class.getName());
        observabilityHistogram = meter.histogramBuilder("do-observability").ofLongs().build();
    }

    @Around("@annotation(observabilityParam)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, ObservabilityParam observabilityParam) throws Throwable {
        runtime = Runtime.getRuntime();
        runtime.gc();
        Param[] params = observabilityParam.params();
        Method methodName = spanAttributesService.getMethod(joinPoint);

        for (Param param : params) {
            String key = param.key();
            String value = String.valueOf(param.value());
            Span.current().setAttribute(key, value);
        }

        Span.current().setAttribute("serviceName", methodName.getName());

        Object proceed = joinPoint.proceed();

        return proceed;
    }

    @Before("@annotation(observability.otel.annotation.ObservabilityParam)")
    public void logBefore(JoinPoint joinPoint) {
        System.out.println("Heap Memory Usage:");
        System.out.println("Init: " + heapMemoryUsage.getInit() / 1048576.0 + " MB");
        startTime = System.currentTimeMillis();
        cpuStorageBegan = osBean.getProcessCpuLoad();
    }

    @After("@annotation(observability.otel.annotation.ObservabilityParam)")
    public void logAfter(JoinPoint joinPoint) {
        SpanContext spanContext = Span.current().getSpanContext();
        long endTime = System.currentTimeMillis();
        double cpuStorage = osBean.getProcessCpuLoad() - cpuStorageBegan;
        long serviceTime = endTime - startTime;
        long memory = runtime != null ? runtime.totalMemory() - runtime.freeMemory() : 0;
        System.out.println("Used: " + heapMemoryUsage.getUsed() / 1048576.0 + "MB");

        Attributes attributes = Attributes.builder()
                .put("span", spanContext.getSpanId())
                .put("trace", spanContext.getTraceId())
                .put("cpuStorage", cpuStorage)
                .put("serviceTime", serviceTime)
                .put("memoryUsage", memory / 1048576.0).build();

        observabilityHistogram.record(5584, attributes);
    }
}
