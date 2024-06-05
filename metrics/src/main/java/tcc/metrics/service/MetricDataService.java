package tcc.metrics.service;

import tcc.metrics.AllMetrics;
import tcc.metrics.GeneralMetrics;

import java.util.List;

public interface MetricDataService {
    public List<AllMetrics> getAllMetrics();

    public GeneralMetrics getSystemMetrics();

}
