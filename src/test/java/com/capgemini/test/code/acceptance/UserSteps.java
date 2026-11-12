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

  private static final String USERS = "/api/v1/users";

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

  @Dado("que existe el usuario:")
  public void existeElUsuario(DataTable user) throws Exception {
    doyDeAlta(user);
    assertThat(response.getStatusCode().value()).isEqualTo(201);
    mockServer.clearRequestLog();
  }

  @Dado("que en la sala {long} hay un usuario guardado")
  public void hayUnUsuarioEnOtraSala(long roomId) {
    Long id = jdbc.queryForObject(
        "INSERT INTO users (name, email, dni, phone, role, room_id) "
            + "VALUES ('otro', 'otro@email.com', '11111111H', '600000000', 'ADMIN', ?) RETURNING id",
        Long.class, roomId);
    lastCreatedId = String.valueOf(id);
  }

  @Cuando("doy de alta al usuario:")
  public void doyDeAlta(DataTable user) throws Exception {
    Map<String, String> body = user.asMaps().get(0);
    response = http.postForEntity(USERS, body, String.class);
    if (response.getStatusCode().value() == 201) {
      lastCreatedId = json.readTree(response.getBody()).get("id").asText();
    }
  }

  @Cuando("consulto ese usuario")
  public void consultoEseUsuario() {
    response = http.getForEntity(USERS + "/{id}", String.class, lastCreatedId);
  }

  @Cuando("consulto el usuario {long}")
  public void consultoElUsuario(long userId) {
    response = http.getForEntity(USERS + "/{id}", String.class, userId);
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

  @Entonces("el usuario queda guardado en la sala {long}")
  public void quedaEnLaSala(long roomId) {
    Long room = jdbc.queryForObject("SELECT room_id FROM users WHERE id = ?", Long.class, Long.valueOf(lastCreatedId));
    assertThat(room).isEqualTo(roomId);
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
