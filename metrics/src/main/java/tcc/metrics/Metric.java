package tcc.metrics;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Metric {
    private static final String PROMETHEUS_API_URL_QUERY = "http://localhost:9090/api/v1/query?query=";

    public JSONArray getPrometheusMetric(String prometheusUrl) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(prometheusUrl);
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

    public long getSumNetworkIo() {
        JSONArray networkArray = getPrometheusMetric(PROMETHEUS_API_URL_QUERY + "system_network_io_bytes_total");
        long receive = 0, transmit = 0;

        for (int i = 0; i < networkArray.length(); i++) {
            JSONObject result = networkArray.getJSONObject(i);
            String resultDevice = result.getJSONObject("metric").getString("device");
            String resultDirection = result.getJSONObject("metric").getString("direction");

            if (resultDevice.equals("eth0") && resultDirection.equals("receive")) {
                receive = result.getJSONArray("value").getLong(1);
            }
            if (resultDevice.equals("eth0") && resultDirection.equals("transmit")) {
                transmit = result.getJSONArray("value").getLong(1);
            }
        }

        return receive + transmit;
    }

    public long getMemoryUsage() {
        JSONArray memoryArray = getPrometheusMetric(PROMETHEUS_API_URL_QUERY + "system_memory_usage_bytes");
        long usedValue = -1;
        for (int i = 0; i < memoryArray.length(); i++) {
            JSONObject jsonObject = memoryArray.getJSONObject(i);
            JSONObject metric = jsonObject.getJSONObject("metric");
            String state = metric.getString("state");

            if ("used".equals(state)) {
                JSONArray value = jsonObject.getJSONArray("value");
                usedValue = value.getLong(1);
                break;
            }
        }

        return usedValue;
    }

    public double getCpuUsage() {
        JSONArray cpusArray = getPrometheusMetric(PROMETHEUS_API_URL_QUERY + "system_cpu_time_seconds_total");
        System.out.println("cpusArray: " + cpusArray);
        double cpuUsage = -1;

        Map<String, Double> cpuIdleTimes = new HashMap<>();
        Map<String, Double> cpuTotalTimes = new HashMap<>();

        for (int i = 0; i < cpusArray.length(); i++) {
            JSONObject result = cpusArray.getJSONObject(i);
            String cpu = result.getJSONObject("metric").getString("cpu");
            String state = result.getJSONObject("metric").getString("state");
            double value = result.getJSONArray("value").getDouble(1);

            if (state.equals("idle")) {
                cpuIdleTimes.put(cpu, value);
            }

            cpuTotalTimes.put(cpu, cpuTotalTimes.getOrDefault(cpu, 0.0) + value);
        }

        double totalIdleTime = cpuIdleTimes.values().stream().mapToDouble(Double::doubleValue).sum();
        double totalTime = cpuTotalTimes.values().stream().mapToDouble(Double::doubleValue).sum();
        cpuUsage = 100 * (1 - (totalIdleTime / totalTime));

        return Math.round(cpuUsage * 100.0) / 100.0;
    }
}
