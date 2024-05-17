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
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;

@Aspect
@Component
public class ObservabilityAspect {
    private final LongHistogram doWorkHistogram;
    private final OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);
    private long startTime;
    private double cpuStorageBegan;
    private Runtime runtime;

    @Autowired
    public ObservabilityAspect(OpenTelemetry openTelemetry) {
        Meter meter = openTelemetry.getMeter(OtelApplication.class.getName());
        doWorkHistogram = meter.histogramBuilder("do-work").ofLongs().build();
    }

    @Around("@annotation(observabilityParam)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint, ObservabilityParam observabilityParam) throws Throwable {
//        MetricDataService metricDataService = new MetricDataService();
        runtime = Runtime.getRuntime();
        runtime.gc();
        Param[] params = observabilityParam.params();
//        String endpoint = metricDataService.getMethodName(joinPoint);

        for (Param param : params) {
            String key = param.key();
            int value = param.value();
            Span.current().setAttribute(key, value);
        }

//        Span.current().setAttribute("serviceEndpoint", endpoint);

//        http://localhost:16686/api/traces?service=custom-annotation
        Object proceed = joinPoint.proceed();

        return proceed;
    }

    @Before("@annotation(observability.otel.annotation.ObservabilityParam)")
    public void logBefore(JoinPoint joinPoint) {
        startTime = System.currentTimeMillis();
        cpuStorageBegan = osBean.getProcessCpuLoad();
        System.out.println("Starting execution of method: " + joinPoint.getSignature().getName());
    }

    @After("@annotation(observability.otel.annotation.ObservabilityParam)")
    public void logAfter(JoinPoint joinPoint) {
        SpanContext spanContext = Span.current().getSpanContext();
        long endTime = System.currentTimeMillis();
        double cpuStorage = osBean.getProcessCpuLoad() - cpuStorageBegan;
        long serviceTime = endTime - startTime;
        long memory = runtime != null ? runtime.totalMemory() - runtime.freeMemory() : 0;

        Attributes attributes = Attributes.builder()
                .put("span", spanContext.getSpanId())
                .put("trace", spanContext.getTraceId())
                .put("cpuStorage", cpuStorage)
                .put("serviceTime", serviceTime)
                .put("memoryUsage", memory).build();

        doWorkHistogram.record(5584, attributes);
    }
}
