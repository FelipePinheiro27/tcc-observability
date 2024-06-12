package tcc.metrics.service;

import tcc.metrics.AllMetrics;
import tcc.metrics.GeneralMetrics;
import tcc.metrics.PrometheusMetrics;

import java.util.List;

public interface MetricDataService {
    public List<AllMetrics> getAllMetrics();

    public GeneralMetrics getSystemMetrics();

    public PrometheusMetrics getPrometheusMetric();

}
