package observability.otel.service.impl;

import observability.otel.service.SpanAttributesService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;

public class SpanAttributesServiceImpl implements SpanAttributesService {

    public Method getMethod(ProceedingJoinPoint joinPoint) {
        Method[] methods = joinPoint.getTarget().getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(joinPoint.getSignature().getName())) {
                return method;
            }
        }
        return null;
    }
}