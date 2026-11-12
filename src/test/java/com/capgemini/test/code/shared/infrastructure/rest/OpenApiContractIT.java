package com.capgemini.test.code.shared.infrastructure.rest;

import static org.assertj.core.api.Assertions.assertThat;

import com.capgemini.test.code.support.AbstractIntegrationTest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

/** La documentación OpenAPI (Swagger) declara los códigos que la API devuelve de verdad. */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenApiContractIT extends AbstractIntegrationTest {

  @Autowired
  private TestRestTemplate http;
  @Autowired
  private ObjectMapper json;

  private List<String> documentedCodes(String path, String method) throws Exception {
    JsonNode responses = json.readTree(http.getForObject("/v3/api-docs", String.class))
        .path("paths").path(path).path(method).path("responses");
    List<String> codes = new ArrayList<>();
    responses.fieldNames().forEachRemaining(codes::add);
    return codes;
  }

  @Test
  void el_alta_documenta_201_y_los_errores_del_contrato() throws Exception {
    assertThat(documentedCodes("/api/v1/users", "post")).containsExactlyInAnyOrder("201", "409", "400", "503");
  }

  @Test
  void la_consulta_documenta_200_y_404() throws Exception {
    assertThat(documentedCodes("/api/v1/users/{userId}", "get")).containsExactlyInAnyOrder("200", "404", "400");
  }
}
