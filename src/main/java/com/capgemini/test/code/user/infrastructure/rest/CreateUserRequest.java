package com.capgemini.test.code.user.infrastructure.rest;

import com.capgemini.test.code.user.application.port.in.CreateUserCommand;

public record CreateUserRequest(String name, String email, String phone, String rol, String dni) {

  CreateUserCommand toCommand(long roomId) {
    return new CreateUserCommand(roomId, name, email, phone, rol, dni);
  }
}
