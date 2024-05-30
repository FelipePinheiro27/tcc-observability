package observability.otel.service;

import observability.otel.AllMetrics;
import observability.otel.ErrorStatistics;
import observability.otel.GeneralMetrics;
import observability.otel.Metric;
import org.aspectj.lang.ProceedingJoinPoint;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public interface MetricDataService {
    public ErrorStatistics getErrorCount();

    public List<AllMetrics> getAllMetrics();

    public double getRequestCountBySecond();

}
