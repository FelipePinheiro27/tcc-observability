package tcc.metrics;

public class Metric {
    private String serviceName;
    private String endpointName;
    private String risk;

    public String getServiceName() { return serviceName; }

    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getEndpointName() { return endpointName; }

    public void setEndpointName(String endpointName) { this.endpointName = endpointName; }

    public String getRisk() { return risk; }

    public void setRisk(String risk) { this.risk = risk; }

    public void combine(Metric currentMetrics) {
        if (this.endpointName == null)
            this.endpointName = currentMetrics.endpointName;
    }
}
