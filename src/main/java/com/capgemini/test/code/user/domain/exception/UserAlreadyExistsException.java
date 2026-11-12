package com.capgemini.test.code.user.domain.exception;

/** Ya hay un usuario con ese valor en un campo único (email; por esquema, también dni y phone). */
public class UserAlreadyExistsException extends UserValidationException {

  public UserAlreadyExistsException(String field) {
    super(field);
  }
}
