package observability.otel.service;

import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

public interface SpanAttributesService {
    public String getEndpointName(ProceedingJoinPoint joinPoint);

    public Method getMethod(ProceedingJoinPoint joinPoint);
}
