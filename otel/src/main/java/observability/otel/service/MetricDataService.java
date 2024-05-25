package observability.otel.service;

import observability.otel.ErrorStatistics;
import observability.otel.GeneralMetrics;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;
import java.util.Map;

public interface MetricDataService {
    public ErrorStatistics getErrorCount();

    public ErrorStatistics getErrorCountByServiceName(String serviceName);

    public Map<String, GeneralMetrics[]> getConnectWithPrometheus();

    public double getRequestCountBySecond();

}
