package com.capgemini.test.code.user.infrastructure.persistence;

import com.capgemini.test.code.user.application.port.out.UserRepository;
import com.capgemini.test.code.user.domain.exception.UserAlreadyExistsException;
import com.capgemini.test.code.user.domain.model.User;
import java.util.Map;
import java.util.Optional;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Repository;

@Repository
class JpaUserRepositoryAdapter implements UserRepository {

  /** Restricciones UNIQUE de V1__init.sql y el campo que se devuelve en "error validation {campo}". */
  private static final Map<String, String> UNIQUE_CONSTRAINT_FIELDS = Map.of(
      "uk_users_email", "email",
      "uk_users_dni", "dni",
      "uk_users_phone", "phone");

  private final UserJpaRepository jpa;

  JpaUserRepositoryAdapter(UserJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public boolean existsByEmail(String email) {
    return jpa.existsByEmail(email);
  }

  /**
   * La comprobación previa de email no basta con peticiones concurrentes: la BD es la que garantiza la
   * unicidad, y aquí su error se traduce a excepción de dominio.
   */
  @Override
  public User save(User user) {
    try {
      return jpa.saveAndFlush(UserEntity.fromDomain(user)).toDomain();
    } catch (DataIntegrityViolationException e) {
      throw duplicatedField(e).<RuntimeException>map(UserAlreadyExistsException::new).orElse(e);
    }
  }

  @Override
  public Optional<User> findByIdAndRoomId(long userId, long roomId) {
    return jpa.findByIdAndRoomId(userId, roomId).map(UserEntity::toDomain);
  }

  private static Optional<String> duplicatedField(DataIntegrityViolationException e) {
    if (e.getCause() instanceof ConstraintViolationException violation && violation.getConstraintName() != null) {
      return Optional.ofNullable(UNIQUE_CONSTRAINT_FIELDS.get(violation.getConstraintName()));
    }
    return Optional.empty();
  }
}
