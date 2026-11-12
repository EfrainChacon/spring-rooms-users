package com.capgemini.test.code.user.infrastructure.persistence;

import com.capgemini.test.code.user.domain.model.Role;
import com.capgemini.test.code.user.domain.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Modelo de persistencia, separado del de dominio: las anotaciones JPA no llegan al dominio. */
@Entity
@Table(name = "users")
class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false, unique = true)
  private String dni;

  @Column(nullable = false, unique = true)
  private String phone;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Role role;

  @Column(name = "room_id")
  private Long roomId;

  protected UserEntity() {
    // JPA
  }

  static UserEntity fromDomain(User user) {
    UserEntity entity = new UserEntity();
    entity.id = user.id();
    entity.name = user.name();
    entity.email = user.email();
    entity.dni = user.dni();
    entity.phone = user.phone();
    entity.role = user.role();
    entity.roomId = user.roomId();
    return entity;
  }

  User toDomain() {
    return new User(id, name, email, phone, role, dni, roomId);
  }
}
