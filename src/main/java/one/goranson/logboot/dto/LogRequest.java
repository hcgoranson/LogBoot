package one.goranson.logboot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class LogRequest {
  private String logger;
  private Body body;

  @Builder
  @ToString
  public static class Body {
    @JsonProperty("configuredLevel")
    private String level;
  }

  public static LogRequest toLogRequest(LogItem logItem) {
    return LogRequest.builder()
        .logger(logItem.getLogger())
        .body(Body.builder()
            .level(logItem.getLevel())
            .build())
        .build();
  }
}
