package observability.otel;

public class ErrorStatistics {
    private int totalErrors;
    private int totalCalls;

    public ErrorStatistics(int totalErrors, int totalCalls) {
        this.totalErrors = totalErrors;
        this.totalCalls = totalCalls;
    }

    public int getTotalErrors() {
        return totalErrors;
    }

    public void setTotalErrors(int totalErrors) {
        this.totalErrors = totalErrors;
    }

    public int getTotalCalls() {
        return totalCalls;
    }

    public void setTotalCalls(int totalCalls) {
        this.totalCalls = totalCalls;
    }

    @Override
    public String toString() {
        return "ErrorStatistics{" +
                "totalErrors=" + totalErrors +
                ", totalCalls=" + totalCalls +
                '}';
    }
}