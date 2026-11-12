package com.capgemini.test.code.user.infrastructure.rest;

import com.capgemini.test.code.user.application.port.in.CreateUserCommand;

public record CreateUserRequest(String name, String email, String phone, String rol, String dni) {

  CreateUserCommand toCommand() {
    return new CreateUserCommand(name, email, phone, rol, dni);
  }
}
