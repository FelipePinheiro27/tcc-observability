package observability.otel;

import lombok.Data;

@Data
public class SpecificMetrics {
    private double maxResponseTime;
    private double maxCpuStorage;
    private double maxMemoryUsage;
    private double minResponseTime;
    private double minCpuStorage;
    private double minMemoryUsage;
    private double medianReponseTime;
    private String spanMaxResponseTime;
    private String spanMaxCpuStorage;
    private String spanMaxMemoryUsage;
    private String spanMinResponseTime;
    private String spanMinCpuStorage;
    private String spanMinMemoryUsage;
    private int responseTimeOverflows;
    private int cpuStorageOverflows;
    private int memoryUsageOverflows;
    private int allOverflows;

    public double getMaxResponseTime() { return maxResponseTime; }

    public void setMaxResponseTime(double maxResponseTime) { this.maxResponseTime = maxResponseTime; }

    public double getMaxCpuStorage() { return maxCpuStorage; }

    public void setMaxCpuStorage(double maxCpuStorage) { this.maxCpuStorage = maxCpuStorage; }

    public double getMaxMemoryUsage() { return maxMemoryUsage; }

    public void setMaxMemoryUsage(double maxMemoryUsage) { this.maxMemoryUsage = maxMemoryUsage; }

    public double getMinResponseTime() { return minResponseTime; }

    public void setMinResponseTime(double minResponseTime) { this.minResponseTime = minResponseTime; }

    public double getMinCpuStorage() { return minCpuStorage; }

    public void setMinCpuStorage(double minCpuStorage) { this.minCpuStorage = minCpuStorage; }

    public double getMinMemoryUsage() { return minMemoryUsage; }

    public void setMinMemoryUsage(double minMemoryUsage) { this.minMemoryUsage = minMemoryUsage; }

    public String getSpanMaxResponseTime() { return spanMaxResponseTime; }

    public void setSpanMaxResponseTime(String spanMaxResponseTime) { this.spanMaxResponseTime = spanMaxResponseTime; }

    public String getSpanMaxCpuStorage() { return spanMaxCpuStorage; }

    public void setSpanMaxCpuStorage(String spanMaxCpuStorage) { this.spanMaxCpuStorage = spanMaxCpuStorage; }

    public String getSpanMaxMemoryUsage() { return spanMaxMemoryUsage; }

    public void setSpanMaxMemoryUsage(String spanMaxMemoryUsage) { this.spanMaxMemoryUsage = spanMaxMemoryUsage; }

    public String getSpanMinResponseTime() { return spanMinResponseTime; }

    public void setSpanMinResponseTime(String spanMinResponseTime) { this.spanMinResponseTime = spanMinResponseTime; }

    public String getSpanMinCpuStorage() { return spanMinCpuStorage; }

    public void setSpanMinCpuStorage(String spanMinCpuStorage) { this.spanMinCpuStorage = spanMinCpuStorage; }

    public String getSpanMinMemoryUsage() { return spanMinMemoryUsage; }

    public void setSpanMinMemoryUsage(String spanMinMemoryUsage) { this.spanMinMemoryUsage = spanMinMemoryUsage; }

    public double getMedianReponseTime() { return medianReponseTime; }

    public void setMedianReponseTime(double medianReponseTime) { this.medianReponseTime = medianReponseTime; }

    public int getResponseTimeOverflows() { return responseTimeOverflows; }

    public void setResponseTimeOverflows(int responseTimeOverflows) { this.responseTimeOverflows = responseTimeOverflows; }

    public int getCpuStorageOverflows() { return cpuStorageOverflows; }

    public void setCpuStorageOverflows(int cpuStorageOverflows) { this.cpuStorageOverflows = cpuStorageOverflows; }

    public int getMemoryUsageOverflows() { return memoryUsageOverflows;}

    public void setMemoryUsageOverflows(int memoryUsageOverflows) { this.memoryUsageOverflows = memoryUsageOverflows; }
    public int getAllOverflows() { return allOverflows; }

    public void setAllOverflows(int allOverflows) { this.allOverflows = allOverflows; }

}
