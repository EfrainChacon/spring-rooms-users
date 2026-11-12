package com.capgemini.test.code.user.domain.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.capgemini.test.code.user.domain.exception.UserValidationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class RoleTest {

  @ParameterizedTest
  @CsvSource({"admin, ADMIN", "Admin, ADMIN", " superadmin , SUPERADMIN", "SUPERADMIN, SUPERADMIN"})
  void acepta_admin_y_superadmin_sin_distinguir_mayusculas(String raw, Role expected) {
    assertThat(Role.fromValue(raw)).isEqualTo(expected);
  }

  @ParameterizedTest
  @NullAndEmptySource
  @ValueSource(strings = {"user", "root", "super admin"})
  void rechaza_cualquier_otro_rol(String raw) {
    assertThatThrownBy(() -> Role.fromValue(raw))
        .isInstanceOf(UserValidationException.class)
        .hasMessage("error validation rol");
  }

  @ParameterizedTest
  @CsvSource({"ADMIN, EMAIL", "SUPERADMIN, SMS"})
  void cada_rol_se_notifica_por_su_canal(Role role, NotificationChannel channel) {
    assertThat(role.notificationChannel()).isEqualTo(channel);
  }
}
