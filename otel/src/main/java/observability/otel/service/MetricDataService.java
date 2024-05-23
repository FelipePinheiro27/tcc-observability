package observability.otel.service;

import observability.otel.ErrorStatistics;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;

public interface MetricDataService {
    public ErrorStatistics getErrorCount();

    public ErrorStatistics getErrorCountByServiceName(String serviceName);

    public double getRequestCountBySecond();

}
