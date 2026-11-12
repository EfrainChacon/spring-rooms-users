package com.capgemini.test.code.shared.domain;

/** Raíz de las excepciones de negocio. La capa REST las traduce a códigos HTTP en un único sitio. */
public abstract class DomainException extends RuntimeException {

  protected DomainException(String message) {
    super(message);
  }
}
