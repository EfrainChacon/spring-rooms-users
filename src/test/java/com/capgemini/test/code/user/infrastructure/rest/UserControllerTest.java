package com.capgemini.test.code.user.infrastructure.rest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.capgemini.test.code.user.application.port.in.CreateUserCommand;
import com.capgemini.test.code.user.application.port.in.CreateUserUseCase;
import com.capgemini.test.code.user.application.port.in.GetUserUseCase;
import com.capgemini.test.code.user.application.port.out.ExternalServiceException;
import com.capgemini.test.code.user.domain.exception.DniRejectedException;
import com.capgemini.test.code.user.domain.exception.UserNotFoundException;
import com.capgemini.test.code.user.domain.exception.UserValidationException;
import com.capgemini.test.code.user.domain.model.Role;
import com.capgemini.test.code.user.domain.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/** Slice web: contrato HTTP (rutas, códigos, formato JSON de respuestas y errores) sin BD ni red. */
@WebMvcTest(UserController.class)
class UserControllerTest {

  private static final String BODY = """
      {"name":"pablo","email":"email@email.com","phone":"677998899","rol":"admin","dni":"23454234W"}""";

  @Autowired
  private MockMvc mvc;
  @MockBean
  private CreateUserUseCase createUserUseCase;
  @MockBean
  private GetUserUseCase getUserUseCase;

  @Test
  void post_devuelve_201_con_el_id_como_string_y_location() throws Exception {
    when(createUserUseCase.create(any())).thenReturn(1L);

    mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(BODY))
        .andExpect(status().isCreated())
        .andExpect(header().string("Location", "http://localhost/api/v1/users/1"))
        .andExpect(jsonPath("$.id").value("1"));

    verify(createUserUseCase).create(
        new CreateUserCommand("pablo", "email@email.com", "677998899", "admin", "23454234W"));
  }

  @Test
  void una_validacion_fallida_devuelve_409_con_el_formato_del_contrato() throws Exception {
    when(createUserUseCase.create(any())).thenThrow(new UserValidationException("userName"));

    mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(BODY))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.code").value(409))
        .andExpect(jsonPath("$.message").value("error validation userName"));
  }

  @Test
  void un_dni_rechazado_devuelve_409() throws Exception {
    when(createUserUseCase.create(any())).thenThrow(new DniRejectedException());

    mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(BODY))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.message").value("error validation dni"));
  }

  @Test
  void si_cae_un_servicio_externo_devuelve_503() throws Exception {
    when(createUserUseCase.create(any())).thenThrow(new ExternalServiceException("dni", new RuntimeException()));

    mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content(BODY))
        .andExpect(status().isServiceUnavailable())
        .andExpect(jsonPath("$.code").value(503));
  }

  @Test
  void un_json_mal_formado_devuelve_400() throws Exception {
    mvc.perform(post("/api/v1/users").contentType(MediaType.APPLICATION_JSON).content("{\"name\":"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.code").value(400));
  }

  @Test
  void get_devuelve_el_usuario() throws Exception {
    when(getUserUseCase.get(7L))
        .thenReturn(new User(7L, "pablo", "email@email.com", "677998899", Role.SUPERADMIN, "23454234W", 1L));

    mvc.perform(get("/api/v1/users/7"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("7"))
        .andExpect(jsonPath("$.name").value("pablo"))
        .andExpect(jsonPath("$.rol").value("superadmin"))
        .andExpect(jsonPath("$.roomId").value("1"));
  }

  @Test
  void get_de_un_usuario_inexistente_devuelve_404() throws Exception {
    when(getUserUseCase.get(7L)).thenThrow(new UserNotFoundException(7L));

    mvc.perform(get("/api/v1/users/7"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.code").value(404));
  }

  @Test
  void un_metodo_no_soportado_conserva_su_codigo() throws Exception {
    mvc.perform(delete("/api/v1/users/7"))
        .andExpect(status().isMethodNotAllowed())
        .andExpect(jsonPath("$.code").value(405));
  }
}
