package one.goranson.logboot.service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import com.google.common.base.Strings;
import one.goranson.logboot.client.LogClient;
import one.goranson.logboot.dto.LogItem;
import one.goranson.logboot.dto.LogUpdateRequest;

public class LogService {

  private final LogClient logClient;

  public LogService() {
    this.logClient = new LogClient();
  }

  public List<LogItem> fetchLogs(String host, String filter, boolean userCache) throws Exception {
    var logItems = logClient.getLogs(rewriteHostname(host), userCache);

    return logItems.stream()
        .filter(filterLogItems(filter))
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

  public void updateLog(String host, LogItem logItem) throws Exception {
    logClient.updateLog(rewriteHostname(host), LogUpdateRequest.toLogRequest(logItem));
  }

  private String rewriteHostname(String host) {
    if (Strings.isNullOrEmpty(host)) {
      throw new IllegalArgumentException("Please enter a valid host name");
    } else if (host.startsWith("http://") || host.startsWith("https://")) {
      return host;
    }
    return "http://" + host;
  }

  private Predicate<LogItem> filterLogItems(String filter) {
    if (Strings.isNullOrEmpty(filter)) {
      return item -> true;
    }
    return logItem -> logItem.getLogger()
                          .toLowerCase()
                          .contains(filter)
                      || logItem.getLevel()
                          .toLowerCase()
                          .contains(filter);
  }
}