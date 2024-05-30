package observability.otel.service.impl;

import observability.otel.*;
import observability.otel.service.MetricDataService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.xml.transform.Source;
import java.math.BigDecimal;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MetricDataServiceImpl implements MetricDataService {

    private static final String JAEGER_API_URL = "http://172.17.0.1:16686/api/traces?service=custom-annotation";
    private static final String PROMETHEUS_API_URL = "http://172.17.0.1:9090/api/v1/query?query=do_observability_count";

    public ErrorStatistics getErrorCount() {
        int errorCount = 0;
        int callCount = 0;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(JAEGER_API_URL);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int responseCode = response.getStatusLine().getStatusCode();
                if (responseCode != 200) {
                    System.out.println("Erro na conexão, código de resposta: " + responseCode);
                    return new ErrorStatistics(errorCount, callCount);
                }

                String jsonResponse = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(jsonResponse);

                JSONArray traces = jsonObject.getJSONArray("data");
                for (int i = 0; i < traces.length(); i++) {
                    JSONObject trace = traces.getJSONObject(i);
                    JSONArray spans = trace.getJSONArray("spans");
                    for (int j = 0; j < spans.length(); j++) {
                        JSONObject span = spans.getJSONObject(j);
                        String httpRoute = null;
                        JSONArray tags = span.getJSONArray("tags");
                        for (int k = 0; k < tags.length(); k++) {
                            JSONObject tag = tags.getJSONObject(k);
                            if ("http.route".equals(tag.getString("key"))) {
                                httpRoute = tag.getString("value");
                            }
                            if(httpRoute != null && !httpRoute.contains("/api/observability")){
                                if ("http.response.status_code".equals(tag.getString("key"))){
                                    callCount++;

                                    if(tag.getInt("value") >= 400)
                                        errorCount++;
                                }

                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ErrorStatistics(errorCount, callCount);
    }

    public ErrorStatistics getErrorCountByServiceName(String serviceName) {
        int errorCount = 0;
        int callCount = 0;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(JAEGER_API_URL);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int responseCode = response.getStatusLine().getStatusCode();
                if (responseCode != 200) {
                    System.out.println("Erro na conexão, código de resposta: " + responseCode);
                    return new ErrorStatistics(errorCount, callCount);
                }

                String jsonResponse = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(jsonResponse);

                JSONArray traces = jsonObject.getJSONArray("data");
                for (int i = 0; i < traces.length(); i++) {
                    JSONObject trace = traces.getJSONObject(i);
                    JSONArray spans = trace.getJSONArray("spans");
                    for (int j = 0; j < spans.length(); j++) {
                        JSONObject span = spans.getJSONObject(j);
                        String httpRoute = null;
                        JSONArray tags = span.getJSONArray("tags");
                        for (int k = 0; k < tags.length(); k++) {
                            JSONObject tag = tags.getJSONObject(k);
                            if ("serviceName".equals(tag.getString("key"))) {
                                httpRoute = tag.getString("value");
                            }
                            if(httpRoute != null && httpRoute.equals(serviceName)){
                                if ("http.response.status_code".equals(tag.getString("key"))){
                                    callCount++;

                                    if(tag.getInt("value") >= 400)
                                        errorCount++;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e);
        }
        return new ErrorStatistics(errorCount, callCount);
    }

    public double getRequestCountBySecond() {
        int requestCountBySecond = 0;
        long currentTimeMillis = System.currentTimeMillis();
        long startTimeMillis = currentTimeMillis - (60 * 1000); // 1 minutos atrás

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(JAEGER_API_URL);

            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int responseCode = response.getStatusLine().getStatusCode();
                if (responseCode != 200) {
                    System.out.println("Erro na conexão, código de resposta: " + responseCode);
                    return -1;
                }

                String jsonResponse = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(jsonResponse);

                JSONArray traces = jsonObject.getJSONArray("data");
                for (int i = 0; i < traces.length(); i++) {
                    JSONObject trace = traces.getJSONObject(i);
                    JSONArray spans = trace.getJSONArray("spans");
                    for (int j = 0; j < spans.length(); j++) {
                        JSONObject span = spans.getJSONObject(j);
                        long startTime = span.getLong("startTime") / 1000;
                        if (startTime >= startTimeMillis && startTime <= currentTimeMillis) {
                            String httpRoute = null;
                            JSONArray tags = span.getJSONArray("tags");
                            for (int k = 0; k < tags.length(); k++) {
                                JSONObject tag = tags.getJSONObject(k);
                                if ("http.route".equals(tag.getString("key"))) {
                                    httpRoute = tag.getString("value");
                                }
                            }
                            if (httpRoute != null && !httpRoute.equals("/api/metrics")) {
                                requestCountBySecond++;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e);
        }

        return (double) requestCountBySecond / 60;
    }

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

    public JSONArray getPrometheusMetric() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(PROMETHEUS_API_URL);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(jsonResponse);
                JSONArray resultArray = jsonObject.getJSONObject("data").getJSONArray("result");

                return resultArray;
            }
        }
        catch (Exception e) {
            System.err.println("Error: " + e);
        }

        return new JSONArray();
    }

    public AllMetrics fillServiceMetric(Map<String, Object> customPrometheusMetricsMap, JSONArray jaegerTraces, String serviceName) {
        double serviceTimeTotal = 0, maxServiceTime = 0, minServiceTime = 0,
                maxCpuStorage = 0, minCpuStorage = 0, maxMemoryUsage = 0, minMemoryUsage = 0;
        long currentTimeMillis = System.currentTimeMillis();
        long startTimeMillis = currentTimeMillis - (5 * 60 * 1000); // 5 minutos atrás
        int qttRequests = 0, qttErrors = 0, requestCountBySecond = 0, allOverflows = 0,
            responseTimeOverflows = 0, cpuStorageOverflows = 0, memoryUsageOverflows = 0;
        String maxServiceTimeSpanId = "", minServiceTimeSpanId = "", maxCpuStorageSpanId= "",
                minCpuStorageSpanId = "", maxMemoryUsageSpanId = "", minMemoryUsageSpanId = "";
        GeneralMetrics generalMetrics = new GeneralMetrics();
        SpecificMetrics specificMetrics = new SpecificMetrics();

        for (int i = 0; i < jaegerTraces.length(); i++) {
            JSONObject trace = jaegerTraces.getJSONObject(i);
            JSONArray spans = trace.getJSONArray("spans");
            for (int j = 0; j < spans.length(); j++) {
                JSONObject span = spans.getJSONObject(j);
                String spanID = span.getString("spanID");
                JSONArray tags = span.getJSONArray("tags");
                Map<String, String> prometheusMetrics;
                long startTime = span.getLong("startTime") / 1000;
                boolean isLastFiveMin = startTime >= startTimeMillis && startTime <= currentTimeMillis;

                String currentServiceName = null;

                for (int k = 0; k < tags.length(); k++) {
                    JSONObject tag = tags.getJSONObject(k);
                    if ("serviceName".equals(tag.getString("key"))) {
                        currentServiceName = tag.getString("value");
                        break;
                    }
                }

                if (serviceName.equals(currentServiceName) && customPrometheusMetricsMap.containsKey(spanID)) {
                    if(isLastFiveMin)
                        requestCountBySecond++;

                    prometheusMetrics = (Map<String, String>) customPrometheusMetricsMap.get(spanID);
                    if(prometheusMetrics != null){
                        for (int k = 0; k < tags.length(); k++) {
                            JSONObject tag = tags.getJSONObject(k);
                            switch (tag.getString("key")) {
                                case "http.response.status_code":
                                    int statusCode = tag.getInt("value");
                                    qttRequests++;
                                    if (statusCode >= 400) {
                                        qttErrors++;
                                    }
                                    break;
                                case "responseTime":
                                    String serviceTimeValue = prometheusMetrics.get("serviceTime");
                                    double serviceTimeParsed = Double.parseDouble(serviceTimeValue);
                                    double serviceTimeSpanParsed = Double.parseDouble(tag.getString("value"));
                                    if(serviceTimeParsed > serviceTimeSpanParsed){
                                        allOverflows++;
                                        responseTimeOverflows++;
                                    }
                                    serviceTimeTotal += serviceTimeParsed;
                                    if (serviceTimeParsed > maxServiceTime) {
                                        maxServiceTime = serviceTimeParsed;
                                        maxServiceTimeSpanId = spanID;
                                    }
                                    if (serviceTimeParsed < minServiceTime) {
                                        minServiceTime = serviceTimeParsed;
                                        minServiceTimeSpanId = spanID;
                                    }
                                    break;
                                case "cpuStorage":
                                    String cpuStorageValue = prometheusMetrics.get("cpuStorage");
                                    double cpuStorageParsed = Double.parseDouble(cpuStorageValue);
                                    double cpuStorageSpanParsed = Double.parseDouble(tag.getString("value"));
                                    if(cpuStorageParsed > cpuStorageSpanParsed){
                                        allOverflows++;
                                        cpuStorageOverflows++;
                                    }
                                    if (cpuStorageParsed > maxCpuStorage) {
                                        maxCpuStorage = cpuStorageParsed;
                                        maxCpuStorageSpanId = spanID;
                                    }
                                    if (cpuStorageParsed < minCpuStorage) {
                                        minCpuStorage = cpuStorageParsed;
                                        minCpuStorageSpanId = spanID;
                                    }
                                    break;
                                case "memory":
                                    String memoryUsageValue = prometheusMetrics.get("memoryUsage");
                                    System.out.println("MEMORY USAGE BEFORE: " + memoryUsageValue);
                                    double memoryUsageParsed = Double.parseDouble(memoryUsageValue);
                                    System.out.println("MEMORY USAGE AFTER: " + memoryUsageValue);
                                    double memoryUsageSpanParsed = Double.parseDouble(tag.getString("value"));
                                    System.out.println("MEMORY USAGE SPAN: " + memoryUsageValue);
                                    if(memoryUsageParsed > memoryUsageSpanParsed){
                                        allOverflows++;
                                        memoryUsageOverflows++;
                                    }
                                    if (memoryUsageParsed > maxMemoryUsage) {
                                        maxMemoryUsage = memoryUsageParsed;
                                        maxMemoryUsageSpanId = spanID;
                                    }
                                    if (memoryUsageParsed < minMemoryUsage) {
                                        minMemoryUsage = memoryUsageParsed;
                                        minMemoryUsageSpanId = spanID;
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        }

        System.out.println("requestCountBySecond: " + requestCountBySecond + " por 60: " + requestCountBySecond / 60);

        generalMetrics.setErrorsQtt(qttErrors);
        generalMetrics.setRequestsQtt(qttRequests);
        generalMetrics.setRequestsBySecond((double) requestCountBySecond / 60);

        specificMetrics.setMedianReponseTime(serviceTimeTotal / (qttRequests + qttErrors));
        specificMetrics.setMaxResponseTime(maxServiceTime);
        specificMetrics.setMinResponseTime(minServiceTime);
        specificMetrics.setSpanMaxResponseTime(maxServiceTimeSpanId);
        specificMetrics.setSpanMinResponseTime(minServiceTimeSpanId);

        specificMetrics.setMaxCpuStorage(maxCpuStorage);
        specificMetrics.setMinCpuStorage(minCpuStorage);
        specificMetrics.setSpanMaxCpuStorage(maxCpuStorageSpanId);
        specificMetrics.setSpanMinCpuStorage(minCpuStorageSpanId);

        specificMetrics.setMaxMemoryUsage(maxMemoryUsage);
        specificMetrics.setMinMemoryUsage(minMemoryUsage);
        specificMetrics.setSpanMaxMemoryUsage(maxMemoryUsageSpanId);
        specificMetrics.setSpanMinMemoryUsage(minMemoryUsageSpanId);

        specificMetrics.setResponseTimeOverflows(responseTimeOverflows);
        specificMetrics.setCpuStorageOverflows(cpuStorageOverflows);
        specificMetrics.setMemoryUsageOverflows(memoryUsageOverflows);
        specificMetrics.setAllOverflows(allOverflows);

        return new AllMetrics(generalMetrics, specificMetrics);
    }

    public AllMetrics getMetricsByServiceName(String serviceName) {
        JSONArray traces = getTraces();
        JSONArray prometheusMetric = getPrometheusMetric();
        Map<String, Object> prometheusMetrichashMap = parseMetricDataToHashMap(prometheusMetric);
        return fillServiceMetric(prometheusMetrichashMap, traces, serviceName);
    }

    public Map<String, Metric> getAllServices() {
        JSONArray traces = getTraces();
        Map<String, Metric> metrics = new HashMap<>();

        for (int i = 0; i < traces.length(); i++) {
            JSONObject trace = traces.getJSONObject(i);
            JSONArray spans = trace.getJSONArray("spans");
            for (int j = 0; j < spans.length(); j++) {
                JSONObject span = spans.getJSONObject(j);
                JSONArray tags = span.getJSONArray("tags");
                Metric currentMetrics = new Metric();
                String serviceName = null;
                for (int k = 0; k < tags.length(); k++) {
                    JSONObject tag = tags.getJSONObject(k);
                    switch (tag.getString("key")) {
                        case "serviceName":
                            serviceName = tag.getString("value");
                            currentMetrics.setServiceName(serviceName);
                            break;
                        case "http.route":
                            String serviceEndpoint = tag.getString("value");
                            currentMetrics.setEndpointName(serviceEndpoint);
                            break;
                    }
                }
                if (serviceName != null) {
                    if (metrics.containsKey(serviceName)) {
                        // Combinar métricas existentes com as novas
                        Metric existingMetric = metrics.get(serviceName);
                        existingMetric.combine(currentMetrics); // Método que combina métricas
                    } else {
                        metrics.put(serviceName, currentMetrics);
                    }
                }
            }
        }

        return metrics;
    }
}
