package one.goranson.logboot.service;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import one.goranson.logboot.client.LogClient;
import one.goranson.logboot.dto.LogItem;
import one.goranson.logboot.dto.LogUpdateRequest;
import one.goranson.logboot.dto.Loggers;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {

  private static final String HOSTNAME = "localhost";
  private static final String FILTER = "micrometer";

  @Mock
  private LogClient logClient;

  private LogService logService;

  @BeforeEach
  void setup() {
    this.logService = new LogService(logClient);
  }

  @Test
  public void can_fetch_logs() throws Exception {

    // Given
    var url = "http://" + HOSTNAME;
    var expectedLogList = List.of(LogItem.builder()
        .logger("one.goranson.logboot")
        .level("INFO")
        .build());
    given(logClient.getLogs(url, false))
        .willReturn(Loggers.builder().logItems(expectedLogList).build());

    // When
    var actual = logService.fetchLogs(HOSTNAME, null, false);

    // Then
    verify(logClient, times(1)).getLogs(url, false);
    then(actual).isEqualTo(expectedLogList);
  }

  @Test
  public void can_update_log() throws Exception {

    // Given
    var url = "http://" + HOSTNAME;
    var expectedLogItem = LogItem.builder()
        .logger("one.goranson.logboot")
        .level("INFO")
        .build();

    // When
    logService.updateLog(HOSTNAME, expectedLogItem);

    // Then
    ArgumentCaptor<LogUpdateRequest> logUpdateRequestArgumentCaptor =
        ArgumentCaptor.forClass(LogUpdateRequest.class);
    verify(logClient, times(1))
        .updateLog(eq(url), logUpdateRequestArgumentCaptor.capture());
    var logUpdateRequest = logUpdateRequestArgumentCaptor.getValue();
    then(logUpdateRequest.getLogger()).isEqualTo("one.goranson.logboot");
    then(logUpdateRequest.getBody().getLevel()).isEqualTo("INFO");
  }
}
