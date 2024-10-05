package tcc.metrics.service.impl;

import tcc.metrics.*;
import tcc.metrics.service.MetricDataService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import tcc.metrics.AllMetrics;
import tcc.metrics.GeneralMetrics;
import tcc.metrics.SpecificMetrics;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricDataServiceImpl implements MetricDataService {
    private static final String JAEGER_API_URL = "http://localhost:16686/api/traces?service=custom-annotation";
    private final Metric metric = new Metric();

    public JSONArray getTraces() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(JAEGER_API_URL);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int responseCode = response.getStatusLine().getStatusCode();
                if (responseCode != 200) {
                    System.out.println("Erro na conexão, código de resposta: " + responseCode);
                }

                String jsonResponse = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(jsonResponse);

                JSONArray traces = jsonObject.getJSONArray("data");

                return traces;
            }
        } catch (Exception e) {
            System.err.println("Error: " + e);
        }

        return new JSONArray();
    }

    private String generateSimpleID(String input) {
        input = input.trim().toLowerCase();

        input = input.replaceAll("[^a-zA-Z0-9]", "-");

        input = input.replaceAll("-+", "-");

        input = input.replaceAll("^-|-$", "");

        return input;
    }

    private double parseAndRound(String value) {
        try {
            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            return bd.doubleValue();
        } catch (NumberFormatException e) {
            // Handle the case where the value cannot be parsed
            System.err.println("Error parsing value: " + value);
            return 0.0;
        }
    }

    public List<AllMetrics> fillServiceMetric(JSONArray jaegerTraces) {
        Map<String, ServiceMetrics> serviceMetricsMap = new HashMap<>();
        long currentTimeMillis = System.currentTimeMillis();
        long startTimeMillis = currentTimeMillis - (5 * 60 * 1000); // 5 minutos atrás

        for (int i = 0; i < jaegerTraces.length(); i++) {
            JSONObject trace = jaegerTraces.getJSONObject(i);
            JSONArray spans = trace.getJSONArray("spans");
            for (int j = 0; j < spans.length(); j++) {
                JSONObject span = spans.getJSONObject(j);
                String spanID = span.getString("traceID");
                JSONArray tags = span.getJSONArray("tags");
                long startTime = span.getLong("startTime") / 1000;
                boolean isLastFiveMin = startTime >= startTimeMillis && startTime <= currentTimeMillis;
                boolean isObservabilitySpan = false;
                double responseTimeReceived = ((double) span.getLong("duration") / (1000 * 1000));
                double cpuUsageReceived = 0, cpuUsageExpected = 0;
                int statusCodeReceived = 0;
                long memoryUsageReceived = 0, memoryUsageExpected = 0, throughput = 0, throughputReceived = 0;
                double responseTimeExpected = 0;
                String currentServiceName = null, routeName = null;

                for (int k = 0; k < tags.length(); k++) {
                    JSONObject tag = tags.getJSONObject(k);
                    switch (tag.getString("key")) {
                        case "http.response.status_code":
                            statusCodeReceived = tag.getInt("value");
                            break;
                        case "responseTime":
                            responseTimeExpected = tag.getDouble("value");
                            break;
                        case "cpuUsage":
                            cpuUsageExpected = parseAndRound(tag.getString("value"));
                            break;
                        case "cpuUsageReceived":
                            cpuUsageReceived = tag.getDouble("value");
                            break;
                        case "memory":
                            memoryUsageExpected = tag.getLong("value");
                            break;
                        case "memoryUsageReceived":
                            memoryUsageReceived = tag.getLong("value");
                            break;
                        case "throughput":
                            throughput = tag.getLong("value");
                        case "throughputReceived":
                            throughputReceived = tag.getLong("value");
                            break;
                        case "isObservabilitySpan":
                            isObservabilitySpan = tag.getBoolean("value");
                            break;
                        case "serviceName":
                            currentServiceName = tag.getString("value");
                            break;
                        case "http.route":
                            routeName = tag.getString("value");
                            break;
                    }
                }
                if (isObservabilitySpan) {
                    String allServiceName = currentServiceName + ": " + routeName;
                    ServiceMetrics serviceMetrics = serviceMetricsMap.computeIfAbsent(allServiceName, k -> new ServiceMetrics());

                    if (isLastFiveMin) {
                        serviceMetrics.incrementRequestCountBySecond();
                    }
                    if (cpuUsageReceived > cpuUsageExpected) {
                        serviceMetrics.incrementAllOverflows();
                        serviceMetrics.incrementCpuUsageOverflows();
                    }
                    if (memoryUsageReceived > memoryUsageExpected) {
                        serviceMetrics.incrementAllOverflows();
                        serviceMetrics.incrementMemoryUsageOverflows();
                    }
                    if (throughputReceived > throughput) {
                        serviceMetrics.incrementAllOverflows();
                        serviceMetrics.incremenThroughputOberflows();
                    }
                    if (responseTimeReceived > responseTimeExpected) {
                        serviceMetrics.incrementAllOverflows();
                        serviceMetrics.incrementResponseTimeOverflows();
                    }
                    if (statusCodeReceived >= 400)
                        serviceMetrics.incrementQttErrors();

                    serviceMetrics.updateMaxCpuUsage(cpuUsageReceived, spanID);
                    serviceMetrics.updateMinCpuUsage(cpuUsageReceived, spanID);
                    serviceMetrics.updateMaxMemoryUsage(memoryUsageReceived, spanID);
                    serviceMetrics.updateMinMemoryUsage(memoryUsageReceived, spanID);
                    serviceMetrics.updateMaxThroughput(throughputReceived, spanID);
                    serviceMetrics.updateMinThroughput(throughputReceived, spanID);
                    serviceMetrics.addServiceTimeTotal(responseTimeReceived);
                    serviceMetrics.updateMaxServiceTime(responseTimeReceived, spanID);
                    serviceMetrics.updateMinServiceTime(responseTimeReceived, spanID);
                    serviceMetrics.incrementQttRequests();
                    serviceMetrics.addTotalCpuUsage(cpuUsageReceived);
                    serviceMetrics.addTotalMemoryUsage(memoryUsageReceived);
                    serviceMetrics.addTotalResponseTime(responseTimeReceived);
                    serviceMetrics.addTotalThroughput(throughputReceived);
                    serviceMetrics.addExpectedThroughput(throughput);
                    serviceMetrics.addExpectedCpuUsage(cpuUsageExpected);
                    serviceMetrics.addExpectedMemoryUsage(memoryUsageExpected);
                    serviceMetrics.addExpectedResponsTime(responseTimeExpected);
                }
            }
        }

        List<AllMetrics> allMetricsList = new ArrayList<>();
        for (Map.Entry<String, ServiceMetrics> entry : serviceMetricsMap.entrySet()) {
            String serviceName = entry.getKey();
            String id = generateSimpleID(serviceName);
            ServiceMetrics serviceMetrics = entry.getValue();

            GeneralMetrics generalMetrics = new GeneralMetrics();
            SpecificMetrics specificMetrics = new SpecificMetrics();

            generalMetrics.setErrorsQtt(serviceMetrics.getQttErrors());
            generalMetrics.setRequestsQtt(serviceMetrics.getQttRequests());
            generalMetrics.setRequestsBySecond((double) serviceMetrics.getRequestCountBySecond() / 60);

            specificMetrics.setMaxResponseTime(serviceMetrics.getMaxServiceTime());
            specificMetrics.setMinResponseTime(serviceMetrics.getMinServiceTime());
            specificMetrics.setSpanMaxResponseTime(serviceMetrics.getMaxServiceTimeSpanId());
            specificMetrics.setSpanMinResponseTime(serviceMetrics.getMinServiceTimeSpanId());

            specificMetrics.setMaxCpuUsage(serviceMetrics.getMaxCpuUsage());
            specificMetrics.setMinCpuUsage(serviceMetrics.getMinCpuUsage());
            specificMetrics.setSpanMaxCpuUsage(serviceMetrics.getMaxCpuUsageSpanId());
            specificMetrics.setSpanMinCpuUsage(serviceMetrics.getMinCpuUsageSpanId());

            specificMetrics.setMaxMemoryUsage(serviceMetrics.getMaxMemoryUsage());
            specificMetrics.setMinMemoryUsage(serviceMetrics.getMinMemoryUsage());
            specificMetrics.setSpanMaxMemoryUsage(serviceMetrics.getMaxMemoryUsageSpanId());
            specificMetrics.setSpanMinMemoryUsage(serviceMetrics.getMinMemoryUsageSpanId());

            specificMetrics.setMaxThroughput(serviceMetrics.getMaxThroughputUsage());
            specificMetrics.setMinMemoryUsage(serviceMetrics.getMinThroughputUsage());
            specificMetrics.setSpanMaxMemoryUsage(serviceMetrics.getMaxThroughputSpanId());
            specificMetrics.setSpanMinMemoryUsage(serviceMetrics.getMinThroughputSpanId());

            specificMetrics.setResponseTimeOverflows(serviceMetrics.getResponseTimeOverflows());
            specificMetrics.setCpuUsageOverflows(serviceMetrics.getCpuUsageOverflows());
            specificMetrics.setMemoryUsageOverflows(serviceMetrics.getMemoryUsageOverflows());
            specificMetrics.setThroughputOverflows(serviceMetrics.getThroughputOverFlows());
            specificMetrics.setAllOverflows(serviceMetrics.getAllOverflows());
            specificMetrics.setExpectedCpuUsage(serviceMetrics.getExpectedCpuUsage());
            specificMetrics.setExpectedMemoryUsage(serviceMetrics.getExpectedMemoryUsage());
            specificMetrics.setExpectedResponseTime(serviceMetrics.getExpectedResponseTime());
            specificMetrics.setExpectedThroughput(serviceMetrics.getExpectedThroughputUsage());
            specificMetrics.setAverageThroughput(serviceMetrics.getTotalThroughput() / serviceMetrics.getQttRequests());
            specificMetrics.setAverageCpuUsage(serviceMetrics.getTotalCpuUsage() / serviceMetrics.getQttRequests());
            specificMetrics.setAverageMemoryUsage(serviceMetrics.getTotalMemoryUsage() / serviceMetrics.getQttRequests());
            specificMetrics.setAverageResponseTime(serviceMetrics.getTotalResponseTime() / serviceMetrics.getQttRequests());

            allMetricsList.add(new AllMetrics(generalMetrics, specificMetrics, serviceName, id));
        }

        return allMetricsList;
    }

    public GeneralMetrics FillSystemMetrics(JSONArray jaegerTraces) {
        long currentTimeMillis = System.currentTimeMillis();
        long startTimeMillis = currentTimeMillis - (5 * 60 * 1000);
        int requestsFiveMinutes = 0, totalRequests = 0, totalErrors = 0;

        for (int i = 0; i < jaegerTraces.length(); i++) {
            JSONObject trace = jaegerTraces.getJSONObject(i);
            JSONArray spans = trace.getJSONArray("spans");
            for (int j = 0; j < spans.length(); j++) {
                JSONObject span = spans.getJSONObject(j);
                JSONArray tags = span.getJSONArray("tags");
                long startTime = span.getLong("startTime") / 1000;
                boolean isLastFiveMin = startTime >= startTimeMillis && startTime <= currentTimeMillis;
                int statusCode = 0;
                boolean isObservabilitySpan = false;

                for (int k = 0; k < tags.length(); k++) {
                    JSONObject tag = tags.getJSONObject(k);
                    switch (tag.getString("key")) {
                        case "http.response.status_code":
                            statusCode = tag.getInt("value");
                            break;
                        case "isObservabilitySpan":
                            isObservabilitySpan = tag.getBoolean("value");
                            break;
                    }
                }
                if(isObservabilitySpan) {
                    totalRequests++;
                    if(statusCode >= 400)
                        totalErrors++;
                    if(isLastFiveMin)
                        requestsFiveMinutes++;
                }
            }
        }

        return new GeneralMetrics(totalRequests, totalErrors, (double) requestsFiveMinutes / 60);
    }

    public List<AllMetrics> getAllMetrics() {
        JSONArray traces = getTraces();

        return fillServiceMetric(traces);
    }

    public GeneralMetrics getSystemMetrics() {
        JSONArray traces = getTraces();
        return FillSystemMetrics(traces);
    }

    public PrometheusMetrics getPrometheusMetric() {
        PrometheusMetrics prometheusMetrics = new PrometheusMetrics();
        prometheusMetrics.setCpuUsage(metric.getCpuUsage());
        prometheusMetrics.setMemory(metric.getMemoryUsage());
        prometheusMetrics.setThroughput(metric.getSumNetworkIo());

        return prometheusMetrics;
    }
}
