package com.capgemini.test.code.user.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.capgemini.test.code.support.AbstractIntegrationTest;
import com.capgemini.test.code.user.application.port.out.RoomRepository;
import com.capgemini.test.code.user.application.port.out.UserRepository;
import com.capgemini.test.code.user.domain.exception.UserAlreadyExistsException;
import com.capgemini.test.code.user.domain.model.Role;
import com.capgemini.test.code.user.domain.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

/** Adaptador de persistencia contra PostgreSQL real con el esquema de Flyway. */
@SpringBootTest
class JpaUserRepositoryAdapterIT extends AbstractIntegrationTest {

  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoomRepository roomRepository;
  @Autowired
  private JdbcTemplate jdbc;

  @BeforeEach
  void cleanUsers() {
    jdbc.update("DELETE FROM users");
  }

  private static User pablo() {
    return User.create("pablo", "email@email.com", "677998899", Role.ADMIN, "23454234W", 1L);
  }

  @Test
  void la_migracion_crea_las_salas_iniciales() {
    assertThat(roomRepository.existsById(1L)).isTrue();
    assertThat(roomRepository.existsById(2L)).isTrue();
    assertThat(roomRepository.existsById(99L)).isFalse();
  }

  @Test
  void guarda_y_recupera_un_usuario_en_su_sala() {
    User saved = userRepository.save(pablo());

    assertThat(saved.id()).isPositive();
    assertThat(userRepository.findByIdAndRoomId(saved.id(), 1L)).contains(saved);
    assertThat(userRepository.findByIdAndRoomId(saved.id(), 2L)).isEmpty();
    assertThat(userRepository.existsByEmail("email@email.com")).isTrue();
  }

  @Test
  void un_email_duplicado_se_traduce_a_excepcion_de_dominio() {
    userRepository.save(pablo());

    assertThatThrownBy(() -> userRepository.save(
        User.create("otro", "email@email.com", "600000000", Role.ADMIN, "11111111H", 1L)))
        .isInstanceOf(UserAlreadyExistsException.class)
        .hasMessage("error validation email");
  }

  @Test
  void un_dni_o_telefono_duplicado_tambien() {
    userRepository.save(pablo());

    assertThatThrownBy(() -> userRepository.save(
        User.create("otro", "otro@email.com", "600000000", Role.ADMIN, "23454234W", 1L)))
        .hasMessage("error validation dni");
    assertThatThrownBy(() -> userRepository.save(
        User.create("otro", "otro@email.com", "677998899", Role.ADMIN, "11111111H", 1L)))
        .hasMessage("error validation phone");
  }
}
