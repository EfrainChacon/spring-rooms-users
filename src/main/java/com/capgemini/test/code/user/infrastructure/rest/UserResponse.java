package com.capgemini.test.code.user.infrastructure.rest;

import com.capgemini.test.code.user.domain.model.User;

/** Representación pública del usuario: mismos nombres de campo que en la petición de alta. */
public record UserResponse(String id, String name, String email, String phone, String rol, String dni, String roomId) {

  static UserResponse from(User user) {
    return new UserResponse(String.valueOf(user.id()), user.name(), user.email(), user.phone(),
        user.role().value(), user.dni(), String.valueOf(user.roomId()));
  }
}
