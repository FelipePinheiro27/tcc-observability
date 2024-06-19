package tcc.metrics;

public class ServiceMetrics {
    private double serviceTimeTotal = 0;
    private double maxServiceTime = 0;
    private double minServiceTime = Double.MAX_VALUE;
    private double maxCpuUsage = 0;
    private double minCpuUsage = Double.MAX_VALUE;
    private double maxMemoryUsage = 0;
    private double minMemoryUsage = Double.MAX_VALUE;
    private int qttRequests = 0;
    private int qttErrors = 0;
    private int requestCountBySecond = 0;
    private int allOverflows = 0;
    private int responseTimeOverflows = 0;
    private int cpuUsageOverflows = 0;
    private int memoryUsageOverflows = 0;
    private String maxServiceTimeSpanId = "";
    private String minServiceTimeSpanId = "";
    private String maxCpuUsageSpanId = "";
    private String minCpuUsageSpanId = "";
    private String maxMemoryUsageSpanId = "";
    private String minMemoryUsageSpanId = "";
    private double expectedCpuUsage = -1;
    private long expectedMemoryUsage = -1;
    private double expectedResponseTime = -1;
    private double totalCpuUsage = 0;
    private double totalMemoryUsage = 0;
    private double totalResponseTime = 0;

    public void incrementQttRequests() {
        this.qttRequests++;
    }

    public void incrementQttErrors() {
        this.qttErrors++;
    }

    public void incrementRequestCountBySecond() {
        this.requestCountBySecond++;
    }

    public void incrementAllOverflows() {
        this.allOverflows++;
    }

    public void incrementResponseTimeOverflows() {
        this.responseTimeOverflows++;
    }

    public void incrementCpuUsageOverflows() {
        this.cpuUsageOverflows++;
    }

    public void incrementMemoryUsageOverflows() {
        this.memoryUsageOverflows++;
    }

    public void addServiceTimeTotal(double serviceTime) {
        this.serviceTimeTotal += serviceTime;
    }

    public void updateMaxServiceTime(double serviceTime, String spanId) {
        if (serviceTime > this.maxServiceTime) {
            this.maxServiceTime = serviceTime;
            this.maxServiceTimeSpanId = spanId;
        }
    }

    public void updateMinServiceTime(double serviceTime, String spanId) {
        if (serviceTime < this.minServiceTime) {
            this.minServiceTime = serviceTime;
            this.minServiceTimeSpanId = spanId;
        }
    }

    public void updateMaxCpuUsage(double cpuUsage, String spanId) {
        if (cpuUsage > this.maxCpuUsage) {
            this.maxCpuUsage = cpuUsage;
            this.maxCpuUsageSpanId = spanId;
        }
    }

    public void updateMinCpuUsage (double cpuUsage, String spanId) {
        if (cpuUsage < this.minCpuUsage) {
            this.minCpuUsage = cpuUsage;
            this.minCpuUsageSpanId = spanId;
        }
    }

    public void updateMaxMemoryUsage(double memoryUsage, String spanId) {
        if (memoryUsage > this.maxMemoryUsage) {
            this.maxMemoryUsage = memoryUsage;
            this.maxMemoryUsageSpanId = spanId;
        }
    }

    public void updateMinMemoryUsage(double memoryUsage, String spanId) {
        if (memoryUsage < this.minMemoryUsage) {
            this.minMemoryUsage = memoryUsage;
            this.minMemoryUsageSpanId = spanId;
        }
    }

    public double getServiceTimeTotal() {
        return serviceTimeTotal;
    }

    public double getMaxServiceTime() {
        return maxServiceTime;
    }

    public double getMinServiceTime() {
        return minServiceTime;
    }

    public String getMaxServiceTimeSpanId() {
        return maxServiceTimeSpanId;
    }

    public String getMinServiceTimeSpanId() {
        return minServiceTimeSpanId;
    }

    public double getMaxCpuUsage() {
        return maxCpuUsage;
    }

    public double getMinCpuUsage() {
        return minCpuUsage;
    }

    public String getMaxCpuUsageSpanId() {
        return maxCpuUsageSpanId;
    }

    public String getMinCpuUsageSpanId() {
        return minCpuUsageSpanId;
    }

    public double getMaxMemoryUsage() {
        return maxMemoryUsage;
    }

    public double getMinMemoryUsage() {
        return minMemoryUsage;
    }

    public String getMaxMemoryUsageSpanId() {
        return maxMemoryUsageSpanId;
    }

    public String getMinMemoryUsageSpanId() {
        return minMemoryUsageSpanId;
    }

    public int getQttRequests() {
        return qttRequests;
    }

    public int getQttErrors() {
        return qttErrors;
    }

    public int getRequestCountBySecond() {
        return requestCountBySecond;
    }

    public int getAllOverflows() {
        return allOverflows;
    }

    public int getResponseTimeOverflows() {
        return responseTimeOverflows;
    }

    public int getCpuUsageOverflows() {
        return cpuUsageOverflows;
    }

    public int getMemoryUsageOverflows() {
        return memoryUsageOverflows;
    }

    public void addTotalCpuUsage(double cpuUsage) { totalCpuUsage += cpuUsage; }

    public void addTotalMemoryUsage(long memoryUsage) { totalMemoryUsage += memoryUsage; }

    public void addTotalResponseTime(double responseTime) { totalResponseTime += responseTime; }

    public double getTotalCpuUsage() { return totalCpuUsage; }

    public double getTotalMemoryUsage() { return totalMemoryUsage; }

    public double getTotalResponseTime() { return totalResponseTime; }

    public void addExpectedCpuUsage(double expectedCpuUsage) {
        if(this.expectedCpuUsage == -1)
            this.expectedCpuUsage = expectedCpuUsage;
    }

    public void addExpectedMemoryUsage(long expectedMemoryUsage) {
        if(this.expectedMemoryUsage == -1)
            this.expectedMemoryUsage = expectedMemoryUsage;
    }

    public void addExpectedResponsTime (double expectedResponseTime) {
        if(this.expectedResponseTime == -1)
            this.expectedResponseTime = expectedResponseTime;
    }

    public double getExpectedCpuUsage() { return expectedCpuUsage; }

    public double getExpectedMemoryUsage() { return expectedMemoryUsage; }

    public double getExpectedResponseTime() { return expectedResponseTime; }
}
