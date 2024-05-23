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

    public String getEndpointName(ProceedingJoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        if (method != null) {
            GetMapping getMappingAnnotation = method.getAnnotation(GetMapping.class);
            RequestMapping requestMappingAnnotation = method.getAnnotation(RequestMapping.class);
            if (getMappingAnnotation != null) {
                String[] paths = getMappingAnnotation.value();
                if (paths.length > 0) {
                    return paths[0];
                }
            } else if (requestMappingAnnotation != null) {
                String[] paths = requestMappingAnnotation.value();
                RequestMethod[] methods = requestMappingAnnotation.method();
                if (paths.length > 0 && methods.length > 0 && methods[0] == RequestMethod.GET) {
                    return paths[0];
                }
            }
        }
        return "";
    }
}
