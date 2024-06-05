package tcc.metrics;

public class ServiceMetrics {
    private double serviceTimeTotal = 0;
    private double maxServiceTime = 0;
    private double minServiceTime = Double.MAX_VALUE;
    private double maxCpuStorage = 0;
    private double minCpuStorage = Double.MAX_VALUE;
    private double maxMemoryUsage = 0;
    private double minMemoryUsage = Double.MAX_VALUE;
    private int qttRequests = 0;
    private int qttErrors = 0;
    private int requestCountBySecond = 0;
    private int allOverflows = 0;
    private int responseTimeOverflows = 0;
    private int cpuStorageOverflows = 0;
    private int memoryUsageOverflows = 0;
    private String maxServiceTimeSpanId = "";
    private String minServiceTimeSpanId = "";
    private String maxCpuStorageSpanId = "";
    private String minCpuStorageSpanId = "";
    private String maxMemoryUsageSpanId = "";
    private String minMemoryUsageSpanId = "";

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

    public void incrementCpuStorageOverflows() {
        this.cpuStorageOverflows++;
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

    public void updateMaxCpuStorage(double cpuStorage, String spanId) {
        if (cpuStorage > this.maxCpuStorage) {
            this.maxCpuStorage = cpuStorage;
            this.maxCpuStorageSpanId = spanId;
        }
    }

    public void updateMinCpuStorage(double cpuStorage, String spanId) {
        if (cpuStorage < this.minCpuStorage) {
            this.minCpuStorage = cpuStorage;
            this.minCpuStorageSpanId = spanId;
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

    public double getMaxCpuStorage() {
        return maxCpuStorage;
    }

    public double getMinCpuStorage() {
        return minCpuStorage;
    }

    public String getMaxCpuStorageSpanId() {
        return maxCpuStorageSpanId;
    }

    public String getMinCpuStorageSpanId() {
        return minCpuStorageSpanId;
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

    public int getCpuStorageOverflows() {
        return cpuStorageOverflows;
    }

    public int getMemoryUsageOverflows() {
        return memoryUsageOverflows;
    }
}
