package observability.otel.service;

import observability.otel.ErrorStatistics;
import org.aspectj.lang.ProceedingJoinPoint;

public interface MetricDataService {
    public String getMethodName(ProceedingJoinPoint joinPoint);

    public ErrorStatistics getErrorCount();
}
