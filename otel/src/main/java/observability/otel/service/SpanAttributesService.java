package observability.otel.service;

import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

public interface SpanAttributesService {
    public Method getMethod(ProceedingJoinPoint joinPoint);
}
