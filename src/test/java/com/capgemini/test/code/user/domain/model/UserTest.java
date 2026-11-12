package com.capgemini.test.code.user.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.capgemini.test.code.user.domain.exception.UserValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class UserTest {

  private static User userWith(String name, String email) {
    return User.create(name, email, "677998899", Role.ADMIN, "23454234W", 1L);
  }

  @Test
  void crea_un_usuario_valido_sin_id() {
    User user = userWith("pablo", "email@email.com");

    assertThat(user.id()).isNull();
    assertThat(user.name()).isEqualTo("pablo");
    assertThat(user.withId(7L).id()).isEqualTo(7L);
  }

  @ParameterizedTest
  @ValueSource(strings = {"a", "pablo", "123456"})
  void acepta_nombres_de_hasta_6_caracteres(String name) {
    assertThat(userWith(name, "a@b.com").name()).isEqualTo(name);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"   ", "pablito", "1234567"})
  void rechaza_nombres_vacios_o_de_mas_de_6_caracteres(String name) {
    assertThatThrownBy(() -> userWith(name, "a@b.com"))
        .isInstanceOf(UserValidationException.class)
        .hasMessage("error validation userName");
  }

  @Test
  void el_nombre_se_mide_sin_espacios_alrededor() {
    assertThat(userWith("  pablo  ", "a@b.com").name()).isEqualTo("pablo");
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"emailemail.com", "email@emailcom", "email"})
  void rechaza_emails_sin_arroba_o_sin_punto(String email) {
    assertThatThrownBy(() -> userWith("pablo", email))
        .isInstanceOf(UserValidationException.class)
        .hasMessage("error validation email");
  }

  @Test
  void normaliza_el_email_a_minusculas() {
    assertThat(userWith("pablo", " Email@Email.COM ").email()).isEqualTo("email@email.com");
  }

  @Test
  void conserva_el_dni_tal_cual_porque_el_servicio_externo_distingue_mayusculas() {
    User user = User.create("pablo", "a@b.com", "600", Role.ADMIN, "99999999w", 1L);

    assertThat(user.dni()).isEqualTo("99999999w");
  }

  @Test
  void exige_dni_telefono_y_rol() {
    assertThatThrownBy(() -> User.create("pablo", "a@b.com", "600", Role.ADMIN, " ", 1L))
        .hasMessage("error validation dni");
    assertThatThrownBy(() -> User.create("pablo", "a@b.com", null, Role.ADMIN, "1X", 1L))
        .hasMessage("error validation phone");
    assertThatThrownBy(() -> User.create("pablo", "a@b.com", "600", null, "1X", 1L))
        .hasMessage("error validation rol");
  }
}
