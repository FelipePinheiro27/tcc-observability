package observability.otel;

import lombok.Data;

@Data
public class AllMetrics {
    private GeneralMetrics generalMetrics;
    private SpecificMetrics specificMetrics;

    public AllMetrics(GeneralMetrics generalMetrics, SpecificMetrics specificMetrics) {
        this.generalMetrics = generalMetrics;
        this.specificMetrics = specificMetrics;
    }
}
