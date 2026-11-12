package com.capgemini.test.code.acceptance;

import static org.assertj.core.api.Assertions.assertThat;

import com.capgemini.test.code.support.MockServerVerifier;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

/** Pasos de los .feature. Cucumber crea una instancia por escenario, así que el estado no se comparte. */
public class UserSteps {

  @Autowired
  private TestRestTemplate http;
  @Autowired
  private JdbcTemplate jdbc;
  @Autowired
  private ObjectMapper json;

  private final MockServerVerifier mockServer = CucumberSpringConfiguration.verifier();

  private ResponseEntity<String> response;
  private String lastCreatedId;

  @Before
  public void cleanState() {
    jdbc.update("DELETE FROM users");
    mockServer.clearRequestLog();
  }

  @Dado("que existe en la sala {long} el usuario:")
  public void existeElUsuario(long roomId, DataTable user) throws Exception {
    doyDeAlta(roomId, user);
    assertThat(response.getStatusCode().value()).isEqualTo(201);
    mockServer.clearRequestLog();
  }

  @Cuando("doy de alta en la sala {long} al usuario:")
  public void doyDeAlta(long roomId, DataTable user) throws Exception {
    Map<String, String> body = user.asMaps().get(0);
    response = http.postForEntity("/api/v1/rooms/{roomId}/users", body, String.class, roomId);
    if (response.getStatusCode().value() == 201) {
      lastCreatedId = json.readTree(response.getBody()).get("id").asText();
    }
  }

  @Cuando("consulto ese usuario en la sala {long}")
  public void consultoEseUsuario(long roomId) {
    response = http.getForEntity("/api/v1/rooms/{roomId}/users/{id}", String.class, roomId, lastCreatedId);
  }

  @Cuando("consulto el usuario {long} en la sala {long}")
  public void consultoElUsuario(long userId, long roomId) {
    response = http.getForEntity("/api/v1/rooms/{roomId}/users/{id}", String.class, roomId, userId);
  }

  @Entonces("la respuesta tiene el código {int}")
  public void laRespuestaTieneElCodigo(int status) {
    assertThat(response.getStatusCode().value()).as(response.getBody()).isEqualTo(status);
  }

  @Entonces("la respuesta contiene el ID generado")
  public void contieneElId() throws Exception {
    assertThat(json.readTree(response.getBody()).get("id").asText()).matches("\\d+");
  }

  @Entonces("el error es {int} con el mensaje {string}")
  public void elErrorEs(int code, String message) throws Exception {
    JsonNode error = json.readTree(response.getBody());
    assertThat(error.get("code").asInt()).isEqualTo(code);
    assertThat(error.get("message").asText()).isEqualTo(message);
  }

  @Entonces("la respuesta contiene el usuario:")
  public void contieneElUsuario(DataTable expected) throws Exception {
    JsonNode user = json.readTree(response.getBody());
    expected.asMaps().get(0).forEach((field, value) -> assertThat(user.get(field).asText()).as(field).isEqualTo(value));
    assertThat(user.get("id").asText()).isEqualTo(lastCreatedId);
  }

  @Entonces("se ha enviado un email a {string} con el mensaje {string}")
  public void seHaEnviadoUnEmail(String email, String message) {
    mockServer.verifyPostedOnce("/email", Map.of("email", email, "message", message));
  }

  @Entonces("se ha enviado un SMS al {string} con el mensaje {string}")
  public void seHaEnviadoUnSms(String phone, String message) {
    mockServer.verifyPostedOnce("/sms", Map.of("phone", phone, "message", message));
  }

  @Entonces("no se ha enviado ningún email")
  public void ningunEmail() {
    mockServer.verifyNeverCalled("/email");
  }

  @Entonces("no se ha enviado ningún SMS")
  public void ningunSms() {
    mockServer.verifyNeverCalled("/sms");
  }

  @Entonces("no se ha consultado el servicio de DNI")
  public void noSeHaConsultadoElDni() {
    mockServer.verifyNeverCalled("/check-dni");
  }

  @Entonces("no hay ningún usuario guardado")
  public void noHayUsuarios() {
    assertThat(jdbc.queryForObject("SELECT count(*) FROM users", Integer.class)).isZero();
  }
}
