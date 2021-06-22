package one.goranson.logboot.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import one.goranson.logboot.dto.LogItem;
import one.goranson.logboot.dto.LogResponse;
import one.goranson.logboot.dto.LogUpdateRequest;

@Slf4j
public class LogClient {
  private final HttpClient httpClient;
  private final LoadingCache<String, List<LogItem>> logCache;
  private final ObjectMapper objectMapper;

  public LogClient() {
    this.httpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1)
        .connectTimeout(Duration.ofSeconds(10))
        .build();

    this.logCache = CacheBuilder.newBuilder()
        .maximumSize(1000)
        .expireAfterAccess(60, TimeUnit.SECONDS)
        .build(
            new CacheLoader<>() {
              public List<LogItem> load(String host) throws IOException, InterruptedException {
                return getLogs(host);
              }
            }
        );

    this.objectMapper = new ObjectMapper();
  }

  public List<LogItem> getLogs(String host, boolean useCache) throws ExecutionException {
    if (!useCache) {
      logCache.refresh(host);
    }
    return logCache.get(host);
  }

  public void updateLog(String host, LogUpdateRequest body) throws Exception {
    var url = String.format("%s/actuator/loggers/%s", host, body.getLogger());
    var request = HttpRequest.newBuilder()
        .uri(URI.create(url))
        .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body.getBody())))
        .header("Content-Type", "application/json")
        .build();

    httpClient.send(request, HttpResponse.BodyHandlers.ofString());
  }

  private List<LogItem> getLogs(String host) throws IOException, InterruptedException {
    var url = String.format("%s/actuator/loggers", host);
    var request = HttpRequest.newBuilder()
        .GET()
        .uri(URI.create(url))
        .build();
    var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    var logResponse = objectMapper.readValue(response.body(), LogResponse.class);
    return logResponse.getLoggers().entrySet().stream()
        .map(entry -> new LogItem(entry.getKey(), entry.getValue().getLevel()))
        .collect(Collectors.toList());
  }

}
