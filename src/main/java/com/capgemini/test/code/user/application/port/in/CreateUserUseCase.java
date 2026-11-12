package com.capgemini.test.code.user.application.port.in;

public interface CreateUserUseCase {

  /** Valida, guarda y notifica al usuario. Devuelve el ID generado. */
  long create(CreateUserCommand command);
}
