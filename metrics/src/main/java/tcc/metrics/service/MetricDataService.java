package observability.otel.service;

import observability.otel.AllMetrics;
import observability.otel.GeneralMetrics;

import java.util.List;

public interface MetricDataService {
    public List<AllMetrics> getAllMetrics();

    public GeneralMetrics getSystemMetrics();

}
