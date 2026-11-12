package com.capgemini.test.code.user.infrastructure.rest;

import com.capgemini.test.code.user.application.port.in.CreateUserCommand;
import io.swagger.v3.oas.annotations.media.Schema;

/** Los ejemplos son los del enunciado: Swagger los usa para rellenar la petición de prueba. */
public record CreateUserRequest(
    @Schema(description = "Nombre, de 1 a 6 caracteres", example = "pablo") String name,
    @Schema(description = "Debe contener @ y un punto; se guarda en minúsculas", example = "email@email.com")
    String email,
    @Schema(example = "677998899") String phone,
    @Schema(description = "Rol del usuario", allowableValues = {"admin", "superadmin"}, example = "admin")
    String rol,
    @Schema(description = "Se valida contra el servicio externo", example = "23454234W") String dni) {

  CreateUserCommand toCommand() {
    return new CreateUserCommand(name, email, phone, rol, dni);
  }
}
