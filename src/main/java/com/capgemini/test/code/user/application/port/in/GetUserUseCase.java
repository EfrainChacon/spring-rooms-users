package com.capgemini.test.code.user.application.port.in;

import com.capgemini.test.code.user.domain.model.User;

public interface GetUserUseCase {

  /** Busca el usuario dentro de la sala por defecto; un usuario de otra sala no se encuentra. */
  User get(long userId);
}
