package tcc.metrics;

import lombok.Data;

@Data
public class AllMetrics {
    private GeneralMetrics generalMetrics;
    private SpecificMetrics specificMetrics;
    private String serviceName;

    public AllMetrics(GeneralMetrics generalMetrics, SpecificMetrics specificMetrics, String serviceName) {
        this.generalMetrics = generalMetrics;
        this.specificMetrics = specificMetrics;
        this.serviceName = serviceName;
    }
}
