package observability.otel;

public class GeneralMetrics {
    private String expectedCpuStorage;
    private String expectedMemoryUsage;
    private String expectedServiceTime;
    private String cpuStorageValue;
    private String memoryUsageValue;
    private String serviceTimeValue;
    private String serviceName;
    private String serviceEndpoint;
    private int statusCode;
    private String spanId;

    public String getExpectedCpuStorage() { return expectedCpuStorage; }

    public void setExpectedCpuStorage(String expectedCpuStorage) { this.expectedCpuStorage = expectedCpuStorage; }

    public String getExpectedMemoryUsage() { return expectedMemoryUsage; }

    public void setExpectedMemoryUsage(String expectedMemoryUsage) { this.expectedMemoryUsage = expectedMemoryUsage; }

    public String getExpectedServiceTime() { return expectedServiceTime; }

    public void setExpectedServiceTime(String expectedServiceTime) { this.expectedServiceTime = expectedServiceTime; }

    public String getCpuStorageValue() { return cpuStorageValue; }

    public void setCpuStorageValue(String cpuStorageValue) { this.cpuStorageValue = cpuStorageValue; }

    public String getMemoryUsageValue() { return memoryUsageValue; }

    public void setMemoryUsageValue(String memoryUsageValue) { this.memoryUsageValue = memoryUsageValue; }

    public String getServiceTimeValue() { return serviceTimeValue; }

    public void setServiceTimeValue(String serviceTimeValue) { this.serviceTimeValue = serviceTimeValue; }

    public String getServiceName() { return serviceName; }

    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getServiceEndpoint() { return serviceEndpoint; }

    public void setServiceEndpoint(String serviceEndpoint) { this.serviceEndpoint = serviceEndpoint; }

    public int getStatusCode() { return statusCode; }

    public void setStatusCode(int statusCode) { this.statusCode = statusCode; }

    public String getSpanId() { return spanId; }

    public void setSpanId(String spanId) { this.spanId = spanId; }

    @Override
    public String toString() {
        return "GeneralMetrics{" +
                "expectedCpuStorage='" + expectedCpuStorage + '\'' +
                ", expectedMemoryUsage='" + expectedMemoryUsage + '\'' +
                ", expectedServiceTime='" + expectedServiceTime + '\'' +
                ", cpuStorageValue='" + cpuStorageValue + '\'' +
                ", memoryUsageValue='" + memoryUsageValue + '\'' +
                ", serviceTimeValue='" + serviceTimeValue + '\'' +
                ", serviceName='" + serviceName + '\'' +
                ", serviceEndpoint='" + serviceEndpoint + '\'' +
                ", statusCode=" + statusCode +
                ", spanID=" + spanId +
                '}';
    }
}
