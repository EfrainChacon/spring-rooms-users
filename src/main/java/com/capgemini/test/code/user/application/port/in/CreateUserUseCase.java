package com.capgemini.test.code.user.application.port.in;

public interface CreateUserUseCase {

  /** Valida, guarda en la sala por defecto y notifica al usuario. Devuelve el ID generado. */
  long create(CreateUserCommand command);
}
