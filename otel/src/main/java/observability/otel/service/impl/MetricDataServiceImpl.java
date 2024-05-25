package observability.otel.service.impl;

import observability.otel.ErrorStatistics;
import observability.otel.GeneralMetrics;
import observability.otel.service.MetricDataService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

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
    public Map<String, GeneralMetrics[]> getConnectWithPrometheus() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(PROMETHEUS_API_URL);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                String jsonResponse = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = new JSONObject(jsonResponse);
                JSONArray resultArray = jsonObject.getJSONObject("data").getJSONArray("result");
                Map<String, Object> hashMap = parseMetricDataToHashMap(resultArray);
                JSONArray traces = getTraces();
                return fillServiceMetrics(hashMap, traces);
            }
        }
        catch (Exception e) {
            System.err.println("Error: " + e);
        }
        return new HashMap<>();
    }

    public Map<String, GeneralMetrics[]> fillServiceMetrics(Map<String, Object> customPrometheusMetricsMap, JSONArray jaegerTraces) {
        Map<String, List<GeneralMetrics>> generalMetrics = new HashMap<>(); // Initialize the map
        System.out.println("customPrometheusMetricsMap: " + customPrometheusMetricsMap);

        for (int i = 0; i < jaegerTraces.length(); i++) {
            JSONObject trace = jaegerTraces.getJSONObject(i);
            JSONArray spans = trace.getJSONArray("spans");
            for (int j = 0; j < spans.length(); j++) {
                JSONObject span = spans.getJSONObject(j);
                String spanID = span.getString("spanID");
                System.out.println("spanID: " + spanID);
                JSONArray tags = span.getJSONArray("tags");
                Map<String, String> prometheusMetrics;
                GeneralMetrics currentMetrics = new GeneralMetrics();
                if (customPrometheusMetricsMap.containsKey(spanID)) {
                    prometheusMetrics = (Map<String, String>) customPrometheusMetricsMap.get(spanID);
                    String serviceName = null;
                    currentMetrics.setSpanId(spanID);
                    for (int k = 0; k < tags.length(); k++) {
                        JSONObject tag = tags.getJSONObject(k);
                        switch (tag.getString("key")) {
                            case "serviceName":
                                serviceName = tag.getString("value");
                                currentMetrics.setServiceName(serviceName);
                                break;
                            case "http.route":
                                String serviceEndpoint = tag.getString("value");
                                currentMetrics.setServiceEndpoint(serviceEndpoint);
                                break;
                            case "http.response.status_code":
                                int statusCode = tag.getInt("value");
                                currentMetrics.setStatusCode(statusCode);
                                break;
                            case "cpuStorage":
                                String cpuStorage = tag.getString("value");
                                currentMetrics.setExpectedCpuStorage(cpuStorage);
                                String cpuStorageValue = prometheusMetrics.get("cpuStorage");
                                currentMetrics.setCpuStorageValue(cpuStorageValue);
                                break;
                            case "responseTime":
                                String serviceTime = tag.getString("value");
                                currentMetrics.setExpectedServiceTime(serviceTime);
                                String serviceTimeValue = prometheusMetrics.get("serviceTime");
                                currentMetrics.setServiceTimeValue(serviceTimeValue);
                                break;
                            case "memory":
                                String memory = tag.getString("value");
                                currentMetrics.setExpectedMemoryUsage(memory);
                                String memoryValue = prometheusMetrics.get("memoryUsage");
                                currentMetrics.setMemoryUsageValue(memoryValue);
                                break;
                        }
                    }
                    if (serviceName != null) {
                        generalMetrics.computeIfAbsent(serviceName, k -> new ArrayList<>()).add(currentMetrics);
                        System.out.println("MINHAS MÉTRICAS: " + generalMetrics);
                    }
                }
            }
        }

        // Convert List<GeneralMetrics> to GeneralMetrics[]
        Map<String, GeneralMetrics[]> generalMetricsArray = new HashMap<>();
        for (Map.Entry<String, List<GeneralMetrics>> entry : generalMetrics.entrySet()) {
            List<GeneralMetrics> metricsList = entry.getValue();
            GeneralMetrics[] metricsArray = new GeneralMetrics[metricsList.size()];
            generalMetricsArray.put(entry.getKey(), metricsList.toArray(metricsArray));
        }

        System.out.println("generalMetricsArray: " + generalMetricsArray);

        return generalMetricsArray;
    }
}
