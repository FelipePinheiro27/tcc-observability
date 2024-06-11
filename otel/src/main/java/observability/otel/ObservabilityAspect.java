package observability.otel;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
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

import java.lang.reflect.Method;

@Aspect
@Component
public class ObservabilityAspect {
    private long memoryUsageFirstValue;
    private final Meter meter;
    private double networkTransferDataFirstValue;
    private final Metric metric = new Metric();
    @Autowired
    private SpanAttributesService spanAttributesService;

    @Autowired
    public ObservabilityAspect(OpenTelemetry openTelemetry) {
        this.meter = openTelemetry.getMeter(OtelApplication.class.getName());
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

        Object proceed = joinPoint.proceed();
        double cpuUsage = metric.getCpuUsage();

        Span.current().setAttribute("serviceName", methodName.getName());
        Span.current().setAttribute("cpuUsageReceived", cpuUsage);
        Span.current().setAttribute("isObservabilitySpan", true);

        return proceed;
    }

    @Before("@annotation(observability.otel.annotation.ObservabilityParam)")
    public void logBefore(JoinPoint joinPoint) {
        networkTransferDataFirstValue = metric.getSumNetworkIo();
        memoryUsageFirstValue = metric.getMemoryUsage();
        System.out.println("networkTransferDataFirstValueBefore: " + networkTransferDataFirstValue);
    }

    @After("@annotation(observability.otel.annotation.ObservabilityParam)")
    public void logAfter(JoinPoint joinPoint) {
        double networkTransferDataSecondValue = metric.getSumNetworkIo();
        long memoryUsageSecondValue = metric.getMemoryUsage();
        double throughput = networkTransferDataSecondValue - networkTransferDataFirstValue;
        long memoryUsage = memoryUsageSecondValue - memoryUsageFirstValue;
        Span.current().setAttribute("memoryUsageReceived", memoryUsage);
        Span.current().setAttribute("throughputReceived", throughput);
    }
}
