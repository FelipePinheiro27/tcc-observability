package observability.otel.annotation;

public @interface Param {
    String key();
    int value();
}