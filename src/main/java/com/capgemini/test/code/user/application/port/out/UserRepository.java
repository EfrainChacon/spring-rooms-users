package com.capgemini.test.code.user.application.port.out;

import com.capgemini.test.code.user.domain.model.User;
import java.util.Optional;

public interface UserRepository {

  boolean existsByEmail(String email);

  /**
   * Persiste el usuario y lo devuelve con su ID.
   *
   * @throws com.capgemini.test.code.user.domain.exception.UserAlreadyExistsException si choca con un
   *     campo único (cubre la carrera entre la comprobación previa y el INSERT)
   */
  User save(User user);

  Optional<User> findByIdAndRoomId(long userId, long roomId);
}
