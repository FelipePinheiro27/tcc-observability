package observability.otel.service.impl;

import observability.otel.ErrorStatistics;
import observability.otel.service.MetricDataService;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.reflect.Method;

public class MetricDataServiceImpl implements MetricDataService {

    private static final String JAEGER_API_URL = "http://localhost:16686/api/traces?service=custom-annotation";

    public Method getMethod(ProceedingJoinPoint joinPoint) {
        Method[] methods = joinPoint.getTarget().getClass().getMethods();
        for (Method method : methods) {
            if (method.getName().equals(joinPoint.getSignature().getName())) {
                return method;
            }
        }
        return null;
    }

    public String getMethodName(ProceedingJoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        if (method != null) {
            GetMapping getMappingAnnotation = method.getAnnotation(GetMapping.class);
            RequestMapping requestMappingAnnotation = method.getAnnotation(RequestMapping.class);
            if (getMappingAnnotation != null) {
                String[] paths = getMappingAnnotation.value();
                if (paths.length > 0) {
                    return paths[0];
                }
            } else if (requestMappingAnnotation != null) {
                String[] paths = requestMappingAnnotation.value();
                RequestMethod[] methods = requestMappingAnnotation.method();
                if (paths.length > 0 && methods.length > 0 && methods[0] == RequestMethod.GET) {
                    return paths[0];
                }
            }
        }
        return "";
    }

    public ErrorStatistics getErrorCount() {
        int errorCount = 0;
        int callCount = 0;

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            String url = "http://172.17.0.1:16686/api/traces?service=custom-annotation";
            HttpGet request = new HttpGet(url);

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
                            if(httpRoute != null && !httpRoute.equals("/api/metrics")){
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
}
