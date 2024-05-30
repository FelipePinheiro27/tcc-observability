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

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public List<AllMetrics> fillServiceMetric(Map<String, Object> customPrometheusMetricsMap, JSONArray jaegerTraces) {
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

                if (currentServiceName != null && customPrometheusMetricsMap.containsKey(spanID)) {
                    prometheusMetrics = (Map<String, String>) customPrometheusMetricsMap.get(spanID);

                    ServiceMetrics serviceMetrics = serviceMetricsMap.computeIfAbsent(currentServiceName, k -> new ServiceMetrics());

                    if (isLastFiveMin) {
                        serviceMetrics.incrementRequestCountBySecond();
                    }

                    if (prometheusMetrics != null) {
                        for (int k = 0; k < tags.length(); k++) {
                            JSONObject tag = tags.getJSONObject(k);
                            switch (tag.getString("key")) {
                                case "http.response.status_code":
                                    int statusCode = tag.getInt("value");
                                    serviceMetrics.incrementQttRequests();
                                    if (statusCode >= 400) {
                                        serviceMetrics.incrementQttErrors();
                                    }
                                    break;
                                case "responseTime":
                                    String serviceTimeValue = prometheusMetrics.get("serviceTime");
                                    double serviceTimeParsed = parseAndRound(serviceTimeValue);
                                    double serviceTimeSpanParsed = parseAndRound(tag.getString("value"));
                                    if (serviceTimeParsed > serviceTimeSpanParsed) {
                                        serviceMetrics.incrementAllOverflows();
                                        serviceMetrics.incrementResponseTimeOverflows();
                                    }
                                    serviceMetrics.addServiceTimeTotal(serviceTimeParsed);
                                    serviceMetrics.updateMaxServiceTime(serviceTimeParsed, spanID);
                                    serviceMetrics.updateMinServiceTime(serviceTimeParsed, spanID);
                                    break;
                                case "cpuStorage":
                                    String cpuStorageValue = prometheusMetrics.get("cpuStorage");
                                    double cpuStorageParsed = parseAndRound(cpuStorageValue);
                                    double cpuStorageSpanParsed = parseAndRound(tag.getString("value"));
                                    if (cpuStorageParsed > cpuStorageSpanParsed) {
                                        serviceMetrics.incrementAllOverflows();
                                        serviceMetrics.incrementCpuStorageOverflows();
                                    }
                                    serviceMetrics.updateMaxCpuStorage(cpuStorageParsed, spanID);
                                    serviceMetrics.updateMinCpuStorage(cpuStorageParsed, spanID);
                                    break;
                                case "memory":
                                    String memoryUsageValue = prometheusMetrics.get("memoryUsage");
                                    double memoryUsageParsed = parseAndRound(memoryUsageValue);
                                    double memoryUsageSpanParsed = parseAndRound(tag.getString("value"));
                                    if (memoryUsageParsed > memoryUsageSpanParsed) {
                                        serviceMetrics.incrementAllOverflows();
                                        serviceMetrics.incrementMemoryUsageOverflows();
                                    }
                                    serviceMetrics.updateMaxMemoryUsage(memoryUsageParsed, spanID);
                                    serviceMetrics.updateMinMemoryUsage(memoryUsageParsed, spanID);
                                    break;
                            }
                        }
                    }
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


    public List<AllMetrics> getAllMetrics() {
        JSONArray traces = getTraces();
        JSONArray prometheusMetric = getPrometheusMetric();
        Map<String, Object> prometheusMetrichashMap = parseMetricDataToHashMap(prometheusMetric);

        return fillServiceMetric(prometheusMetrichashMap, traces);
    }
}
