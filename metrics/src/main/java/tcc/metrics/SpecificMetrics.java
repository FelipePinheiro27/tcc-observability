package tcc.metrics;

import lombok.Data;

@Data
public class SpecificMetrics {
    private double maxResponseTime;
    private double maxCpuUsage;
    private double maxMemoryUsage;
    private double minResponseTime;
    private double minCpuUsage;
    private double minMemoryUsage;
    private String spanMaxResponseTime;
    private String spanMaxCpuUsage;
    private String spanMaxMemoryUsage;
    private String spanMinResponseTime;
    private String spanMinCpuUsage;
    private String spanMinMemoryUsage;
    private int responseTimeOverflows;
    private int cpuUsageOverflows;
    private int memoryUsageOverflows;
    private int allOverflows;
    private double expectedCpuUsage;
    private double expectedMemoryUsage;
    private double expectedResponseTime;
    private double averageCpuUsage;
    private double averageMemoryUsage;
    private double averageResponseTime;


    public double getMaxResponseTime() { return maxResponseTime; }

    public void setMaxResponseTime(double maxResponseTime) { this.maxResponseTime = maxResponseTime; }

    public double getMaxCpuUsage() { return maxCpuUsage; }

    public void setMaxCpuUsage(double maxCpuUsage) { this.maxCpuUsage = maxCpuUsage; }

    public double getMaxMemoryUsage() { return maxMemoryUsage; }

    public void setMaxMemoryUsage(double maxMemoryUsage) { this.maxMemoryUsage = maxMemoryUsage; }

    public double getMinResponseTime() { return minResponseTime; }

    public void setMinResponseTime(double minResponseTime) { this.minResponseTime = minResponseTime; }

    public double getMinCpuUsage() { return minCpuUsage; }

    public void setMinCpuUsage(double minCpuUsage) { this.minCpuUsage = minCpuUsage; }

    public double getMinMemoryUsage() { return minMemoryUsage; }

    public void setMinMemoryUsage(double minMemoryUsage) { this.minMemoryUsage = minMemoryUsage; }

    public String getSpanMaxResponseTime() { return spanMaxResponseTime; }

    public void setSpanMaxResponseTime(String spanMaxResponseTime) { this.spanMaxResponseTime = spanMaxResponseTime; }

    public String getSpanMaxCpuUsage() { return spanMaxCpuUsage; }

    public void setSpanMaxCpuUsage(String spanMaxCpuUsage) { this.spanMaxCpuUsage = spanMaxCpuUsage; }

    public String getSpanMaxMemoryUsage() { return spanMaxMemoryUsage; }

    public void setSpanMaxMemoryUsage(String spanMaxMemoryUsage) { this.spanMaxMemoryUsage = spanMaxMemoryUsage; }

    public String getSpanMinResponseTime() { return spanMinResponseTime; }

    public void setSpanMinResponseTime(String spanMinResponseTime) { this.spanMinResponseTime = spanMinResponseTime; }

    public String getSpanMinCpuUsage() { return spanMinCpuUsage; }

    public void setSpanMinCpuUsage(String spanMinCpuUsage) { this.spanMinCpuUsage = spanMinCpuUsage; }

    public String getSpanMinMemoryUsage() { return spanMinMemoryUsage; }

    public void setSpanMinMemoryUsage(String spanMinMemoryUsage) { this.spanMinMemoryUsage = spanMinMemoryUsage; }

    public int getResponseTimeOverflows() { return responseTimeOverflows; }

    public void setResponseTimeOverflows(int responseTimeOverflows) { this.responseTimeOverflows = responseTimeOverflows; }

    public int getCpuUsageOverflows() { return cpuUsageOverflows; }

    public void setCpuUsageOverflows(int cpuUsageOverflows) { this.cpuUsageOverflows = cpuUsageOverflows; }

    public int getMemoryUsageOverflows() { return memoryUsageOverflows; }

    public void setMemoryUsageOverflows(int memoryUsageOverflows) { this.memoryUsageOverflows = memoryUsageOverflows; }

    public int getAllOverflows() { return allOverflows; }

    public void setAllOverflows(int allOverflows) { this.allOverflows = allOverflows; }

    public double getAverageCpuUsage() { return averageCpuUsage; }

    public void setAverageCpuUsage(double averageCpuUsage) { this.averageCpuUsage = averageCpuUsage; }

    public double getAverageMemoryUsage() { return averageMemoryUsage; }

    public void setAverageMemoryUsage(double averageMemoryUsage) { this.averageMemoryUsage = averageMemoryUsage; }

    public double getAverageResponseTime() { return averageResponseTime; }

    public void setAverageResponseTime(double averageResponseTime) { this.averageResponseTime = averageResponseTime; }

    public double getExpectedCpuUsage() { return expectedCpuUsage; }

    public void setExpectedCpuUsage(double expectedCpuUsage) { this.expectedCpuUsage = expectedCpuUsage; }

    public double getExpectedMemoryUsage() { return expectedMemoryUsage; }

    public void setExpectedMemoryUsage(double expectedMemoryUsage) { this.expectedMemoryUsage = expectedMemoryUsage; }

    public double getExpectedResponseTime() { return expectedResponseTime; }

    public void setExpectedResponseTime(double expectedResponseTime) { this.expectedResponseTime = expectedResponseTime; }

}
