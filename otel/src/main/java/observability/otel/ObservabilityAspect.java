package observability.otel;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.common.AttributeKey;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.LongGaugeBuilder;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.metrics.ObservableLongGauge;
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

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

@Aspect
@Component
public class ObservabilityAspect {
    private final LongCounter requestCounter;
    private final Meter meter;
    private final AtomicLong lastMemoryUsage = new AtomicLong();

    @Autowired
    private SpanAttributesService spanAttributesService;

    @Autowired
    public ObservabilityAspect(OpenTelemetry openTelemetry) {
        try {
            this.meter = openTelemetry.getMeter(OtelApplication.class.getName());

            this.requestCounter = meter.counterBuilder("observability_requests_total")
                    .setDescription("Total number of requests")
                    .setUnit("requests")
                    .build();

            this.meter.gaugeBuilder("observability_memory_usage")
                    .ofLongs()
                    .setDescription("Current JVM memory usage")
                    .setUnit("bytes")
                    .buildWithCallback(measurement -> {
                        // Usar o valor da última medição de uso de memória com seus atributos
                        long memoryUsage = lastMemoryUsage.get();
                        Attributes attributes = Attributes.builder()
                                .put(AttributeKey.stringKey("method"), "last_method")
                                .put(AttributeKey.stringKey("spanId"), "last_span_id")
                                .put(AttributeKey.stringKey("type"), "heap")
                                .build();
                        measurement.record(memoryUsage, attributes);
                    });
        } catch (Exception e) {
            System.err.println("Erro ao inicializar ObservabilityAspect: " + e.getMessage());
            throw e;
        }
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

        requestCounter.add(1, Attributes.builder().put(AttributeKey.stringKey("method"), methodName.getName()).build());
        SpanContext spanContext = Span.current().getSpanContext();

        long memoryUsage = getMemoryUsage();
        lastMemoryUsage.set(memoryUsage);

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
        // Código para ser executado antes da execução do método anotado
    }

    @After("@annotation(observability.otel.annotation.ObservabilityParam)")
    public void logAfter(JoinPoint joinPoint) {
        // Código para ser executado após a execução do método anotado
    }

    private static long getMemoryUsage() {
        // Implementação para obter o uso real de memória
        return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
    }
}
