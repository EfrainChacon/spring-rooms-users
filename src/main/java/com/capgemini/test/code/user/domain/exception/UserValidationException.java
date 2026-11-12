package com.capgemini.test.code.user.domain.exception;

import com.capgemini.test.code.shared.domain.DomainException;

/** Un dato del usuario no cumple las reglas de negocio. Se responde 409 "error validation {campo}". */
public class UserValidationException extends DomainException {

  private final String field;

  public UserValidationException(String field) {
    super("error validation " + field);
    this.field = field;
  }

  public String field() {
    return field;
  }
}
