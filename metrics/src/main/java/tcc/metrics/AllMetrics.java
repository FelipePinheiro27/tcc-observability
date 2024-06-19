package tcc.metrics;

import lombok.Data;

@Data
public class AllMetrics {
    private GeneralMetrics generalMetrics;
    private SpecificMetrics specificMetrics;
    private String serviceName;
    private String id;
    public AllMetrics(GeneralMetrics generalMetrics, SpecificMetrics specificMetrics, String serviceName, String id) {
        this.generalMetrics = generalMetrics;
        this.specificMetrics = specificMetrics;
        this.serviceName = serviceName;
        this.id = id;
    }
}
