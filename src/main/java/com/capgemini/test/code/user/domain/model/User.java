package com.capgemini.test.code.user.domain.model;

import com.capgemini.test.code.user.domain.exception.UserValidationException;
import java.util.Locale;

/**
 * Usuario de una sala. Las reglas de negocio se comprueban al construirlo, así que no puede existir
 * un {@code User} inválido en memoria y el resto del código no necesita volver a validar.
 *
 * @param id     {@code null} mientras no se ha persistido
 * @param roomId sala a la que pertenece (un usuario está en una única sala)
 */
public record User(Long id, String name, String email, String phone, Role role, String dni, Long roomId) {

  public static final int MAX_NAME_LENGTH = 6;

  public User {
    name = trimmed(name);
    email = trimmed(email);
    phone = trimmed(phone);
    dni = trimmed(dni);

    if (isBlank(name) || name.length() > MAX_NAME_LENGTH) {
      throw new UserValidationException("userName");
    }
    // Regla literal del enunciado: debe contener una @ y un punto.
    if (isBlank(email) || !email.contains("@") || !email.contains(".")) {
      throw new UserValidationException("email");
    }
    // Se normaliza para que "Pablo@x.com" y "pablo@x.com" cuenten como el mismo usuario.
    email = email.toLowerCase(Locale.ROOT);
    if (isBlank(phone)) {
      throw new UserValidationException("phone");
    }
    if (isBlank(dni)) {
      throw new UserValidationException("dni");
    }
    if (role == null) {
      throw new UserValidationException(Role.FIELD);
    }
    if (roomId == null) {
      throw new IllegalArgumentException("roomId es obligatorio");
    }
  }

  /** Usuario nuevo, aún sin ID. */
  public static User create(String name, String email, String phone, Role role, String dni, Long roomId) {
    return new User(null, name, email, phone, role, dni, roomId);
  }

  public User withId(Long newId) {
    return new User(newId, name, email, phone, role, dni, roomId);
  }

  private static String trimmed(String value) {
    return value == null ? null : value.trim();
  }

  private static boolean isBlank(String value) {
    return value == null || value.isEmpty();
  }
}
