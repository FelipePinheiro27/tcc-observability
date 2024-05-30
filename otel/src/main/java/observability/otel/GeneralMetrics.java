package observability.otel;

import lombok.Data;

@Data
public class GeneralMetrics {
    private int requestsQtt;
    private int errorsQtt;
    private double requestsBySecond;

    public GeneralMetrics(){ }

    public GeneralMetrics(int requestsQtt, int errorsQtt, double requestsBySecond) {
        this.requestsQtt = requestsQtt;
        this.errorsQtt = errorsQtt;
        this.requestsBySecond = requestsBySecond;
    }

    public int getRequestsQtt() { return requestsQtt; }

    public void setRequestsQtt(int requestsQtt) { this.requestsQtt = requestsQtt; }

    public int getErrorsQtt() { return errorsQtt; }

    public void setErrorsQtt(int errorsQtt) { this.errorsQtt = errorsQtt; }

    public double getRequestsBySecond() { return requestsBySecond; }

    public void setRequestsBySecond(double requestsBySecond) { this.requestsBySecond = requestsBySecond; }
}
