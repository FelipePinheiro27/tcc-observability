package tcc.metrics;

public class PrometheusMetrics {
    private long throughput;
    private long memory;
    private double cpuUsage;

    public long getThroughput() { return throughput; }

    public void setThroughput(long throughput) { this.throughput = throughput; }

    public long getMemory() { return memory; }

    public void setMemory(long memory) { this.memory = memory; }

    public double getCpuUsage() { return cpuUsage; }

    public void setCpuUsage(double cpuUsage) { this.cpuUsage = cpuUsage; }
}
