package com.capgemini.test.code.support;

import static org.assertj.core.api.Assertions.fail;

import java.util.Map;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;

/**
 * Verificaciones sobre las peticiones que ha recibido MockServer, usando su API REST
 * (/mockserver/verify y /mockserver/clear). Evita añadir el cliente Java de MockServer y sus
 * dependencias transitivas solo para esto.
 */
public final class MockServerVerifier {

  private final RestClient client;

  MockServerVerifier(String endpoint) {
    this.client = RestClient.builder().baseUrl(endpoint).build();
  }

  /** Olvida las peticiones registradas, pero conserva las expectativas de init.json. */
  public void clearRequestLog() {
    client.put().uri("/mockserver/clear?type=LOG").body("{}").retrieve().toBodilessEntity();
  }

  public void verifyPostedOnce(String path, Map<String, String> jsonBody) {
    verify(Map.of(
        "httpRequest", Map.of(
            "method", "POST",
            "path", path,
            "body", Map.of("type", "JSON", "json", jsonBody, "matchType", "STRICT")),
        "times", Map.of("atLeast", 1, "atMost", 1)));
  }

  public void verifyNeverCalled(String path) {
    verify(Map.of(
        "httpRequest", Map.of("path", path),
        "times", Map.of("atLeast", 0, "atMost", 0)));
  }

  private void verify(Map<String, Object> verification) {
    client.put().uri("/mockserver/verify").body(verification)
        .exchange((request, response) -> {
          HttpStatusCode status = response.getStatusCode();
          if (status.value() != 202) {
            fail("Verificación de MockServer fallida (" + status.value() + "): "
                + new String(response.getBody().readAllBytes()));
          }
          return null;
        });
  }
}
