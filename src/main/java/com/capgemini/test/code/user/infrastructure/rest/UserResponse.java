package com.capgemini.test.code.user.infrastructure.rest;

import com.capgemini.test.code.user.domain.model.User;
import io.swagger.v3.oas.annotations.media.Schema;

/** Representación pública del usuario: mismos nombres de campo que en la petición de alta. */
public record UserResponse(
    @Schema(example = "1") String id,
    @Schema(example = "pablo") String name,
    @Schema(example = "email@email.com") String email,
    @Schema(example = "677998899") String phone,
    @Schema(example = "admin") String rol,
    @Schema(example = "23454234W") String dni,
    @Schema(example = "1") String roomId) {

  static UserResponse from(User user) {
    return new UserResponse(String.valueOf(user.id()), user.name(), user.email(), user.phone(),
        user.role().value(), user.dni(), String.valueOf(user.roomId()));
  }
}
