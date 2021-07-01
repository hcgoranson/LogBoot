package one.goranson.logboot.client;

import static org.assertj.core.api.BDDAssertions.then;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

class LogClientTest {

  private MockWebServer mockWebServer;
  private LogClient logClient;

  @BeforeEach
  void init() {
    this.mockWebServer = new MockWebServer();
    this.logClient = new LogClient();
  }

  @AfterEach
  void end() throws IOException {
    this.mockWebServer.shutdown();
  }

  @Test
  public void can_get_logs() throws ExecutionException {

    // Given
    mockWebServer.enqueue(new MockResponse()
        .addHeader("Content-Type", "application/vnd.spring-boot.actuator.v3+json")
        .setResponseCode(200).setBody("{\n"
                                      + "    \"levels\": [\n"
                                      + "        \"OFF\",\n"
                                      + "        \"ERROR\",\n"
                                      + "        \"WARN\",\n"
                                      + "        \"INFO\",\n"
                                      + "        \"DEBUG\",\n"
                                      + "        \"TRACE\"\n"
                                      + "    ],\n"
                                      + "    \"loggers\": {\n"
                                      + "        \"ROOT\": {\n"
                                      + "            \"configuredLevel\": \"INFO\",\n"
                                      + "            \"effectiveLevel\": \"INFO\"\n"
                                      + "        },\n"
                                      + "        \"_org\": {\n"
                                      + "            \"configuredLevel\": null,\n"
                                      + "            \"effectiveLevel\": \"INFO\"\n"
                                      + "        }\n"
                                      + "      }\n"
                                      + "}"));

    // When
    var logItems = logClient.getLogs(mockWebServer.url("/").toString(), false);

    // Then
    then(logItems).isNotNull();
    then(logItems.size()).isEqualTo(2);
    then(logItems.get(0).getLogger()).isEqualTo("ROOT");
    then(logItems.get(0).getLevel()).isEqualTo("INFO");
  }

}
