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

    public Map<String, Object> parseMetricDataToHashMap(JSONArray jsonArray) {
        Map<String, Object> hashMap = new HashMap<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject metricObject = jsonArray.getJSONObject(i).getJSONObject("metric");
            String spanId = metricObject.getString("span");
            System.out.println("parseMetricDataToHashMap: " + spanId);
            Map<String, Object> valueMap = new HashMap<>();
            valueMap.put("trace", metricObject.getString("trace"));
            valueMap.put("memoryUsage", metricObject.getString("memoryUsage"));
            valueMap.put("cpuStorage", metricObject.getString("cpuStorage"));
            valueMap.put("serviceTime", metricObject.getString("serviceTime"));

            hashMap.put(spanId, valueMap);
        }

        return hashMap;
    }

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
                String spanID = span.getString("spanID");
                JSONArray tags = span.getJSONArray("tags");
                long startTime = span.getLong("startTime") / 1000;
                boolean isLastFiveMin = startTime >= startTimeMillis && startTime <= currentTimeMillis;
                boolean isObservabilitySpan = false;
                long responseTimeReceived = span.getLong("duration");
                double cpuStorageReceived = 0, cpuStorageExpected = 0;
                int statusCodeReceived = 0;
                long memoryUsageReceived = 0, memoryUsageExpected = 0, responseTimeExpected = 0;
                String currentServiceName = null;

                for (int k = 0; k < tags.length(); k++) {
                    JSONObject tag = tags.getJSONObject(k);
                    switch (tag.getString("key")) {
                        case "http.response.status_code":
                            statusCodeReceived = tag.getInt("value");
                            break;
                        case "responseTime":
                            responseTimeExpected = tag.getLong("value");
                            break;
                        case "cpuStorage":
                            cpuStorageExpected = parseAndRound(tag.getString("value"));
                            break;
                        case "cpuUsageReceived":
                            cpuStorageReceived = tag.getDouble("value");
                            break;
                        case "memory":
                            memoryUsageExpected = tag.getLong("value");
                            break;
                        case "memoryUsageReceived":
                            memoryUsageReceived = tag.getLong("value");
                            break;
                        case "isObservabilitySpan":
                            isObservabilitySpan = tag.getBoolean("value");
                            break;
                        case "http.route":
                            currentServiceName = tag.getString("value");
                            break;
                    }
                }
            if (isObservabilitySpan) {
                ServiceMetrics serviceMetrics = serviceMetricsMap.computeIfAbsent(currentServiceName, k -> new ServiceMetrics());

                    if (isLastFiveMin) {
                        serviceMetrics.incrementRequestCountBySecond();
                    }
                    if (cpuStorageReceived > cpuStorageExpected) {
                        serviceMetrics.incrementAllOverflows();
                        serviceMetrics.incrementCpuStorageOverflows();
                    }
                    if (memoryUsageReceived > memoryUsageExpected) {
                        serviceMetrics.incrementAllOverflows();
                        serviceMetrics.incrementMemoryUsageOverflows();
                    }
                    if (responseTimeReceived > responseTimeExpected) {
                        serviceMetrics.incrementAllOverflows();
                        serviceMetrics.incrementResponseTimeOverflows();
                    }
                    if (statusCodeReceived >= 400)
                        serviceMetrics.incrementQttErrors();

                    serviceMetrics.updateMaxCpuStorage(cpuStorageReceived, spanID);
                    serviceMetrics.updateMinCpuStorage(cpuStorageReceived, spanID);
                    serviceMetrics.updateMaxMemoryUsage(memoryUsageReceived, spanID);
                    serviceMetrics.updateMinMemoryUsage(memoryUsageReceived, spanID);
                    serviceMetrics.addServiceTimeTotal(responseTimeReceived);
                    serviceMetrics.updateMaxServiceTime(responseTimeReceived, spanID);
                    serviceMetrics.updateMinServiceTime(responseTimeReceived, spanID);
                    serviceMetrics.incrementQttRequests();
                }
            }
        }

        List<AllMetrics> allMetricsList = new ArrayList<>();
        for (Map.Entry<String, ServiceMetrics> entry : serviceMetricsMap.entrySet()) {
            String serviceName = entry.getKey();
            ServiceMetrics serviceMetrics = entry.getValue();

            GeneralMetrics generalMetrics = new GeneralMetrics();
            SpecificMetrics specificMetrics = new SpecificMetrics();

            generalMetrics.setErrorsQtt(serviceMetrics.getQttErrors());
            generalMetrics.setRequestsQtt(serviceMetrics.getQttRequests());
            generalMetrics.setRequestsBySecond((double) serviceMetrics.getRequestCountBySecond() / 60);

            specificMetrics.setMedianReponseTime(serviceMetrics.getServiceTimeTotal() / serviceMetrics.getQttRequests());
            specificMetrics.setMaxResponseTime(serviceMetrics.getMaxServiceTime());
            specificMetrics.setMinResponseTime(serviceMetrics.getMinServiceTime());
            specificMetrics.setSpanMaxResponseTime(serviceMetrics.getMaxServiceTimeSpanId());
            specificMetrics.setSpanMinResponseTime(serviceMetrics.getMinServiceTimeSpanId());

            specificMetrics.setMaxCpuStorage(serviceMetrics.getMaxCpuStorage());
            specificMetrics.setMinCpuStorage(serviceMetrics.getMinCpuStorage());
            specificMetrics.setSpanMaxCpuStorage(serviceMetrics.getMaxCpuStorageSpanId());
            specificMetrics.setSpanMinCpuStorage(serviceMetrics.getMinCpuStorageSpanId());

            specificMetrics.setMaxMemoryUsage(serviceMetrics.getMaxMemoryUsage());
            specificMetrics.setMinMemoryUsage(serviceMetrics.getMinMemoryUsage());
            specificMetrics.setSpanMaxMemoryUsage(serviceMetrics.getMaxMemoryUsageSpanId());
            specificMetrics.setSpanMinMemoryUsage(serviceMetrics.getMinMemoryUsageSpanId());

            specificMetrics.setResponseTimeOverflows(serviceMetrics.getResponseTimeOverflows());
            specificMetrics.setCpuStorageOverflows(serviceMetrics.getCpuStorageOverflows());
            specificMetrics.setMemoryUsageOverflows(serviceMetrics.getMemoryUsageOverflows());
            specificMetrics.setAllOverflows(serviceMetrics.getAllOverflows());

            allMetricsList.add(new AllMetrics(generalMetrics, specificMetrics, serviceName));
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

                for (int k = 0; k < tags.length(); k++) {
                    JSONObject tag = tags.getJSONObject(k);
                    if (tag.getString("key").equals("http.response.status_code")) {
                        statusCode = tag.getInt("value");
                    }
                }

                totalRequests++;
                if(statusCode >= 400)
                    totalErrors++;
                if(isLastFiveMin)
                    requestsFiveMinutes++;
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
}
