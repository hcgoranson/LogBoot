package one.goranson.logboot.dto;

import java.util.Optional;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LogResponseItem {
  @JsonProperty("effective_level")
  private String effective_level;

  @JsonProperty("effectiveLevel")
  private String effectiveLevel;

  public String getLevel() {
    return Optional.ofNullable(effective_level)
        .orElse(effectiveLevel);
  }
}
