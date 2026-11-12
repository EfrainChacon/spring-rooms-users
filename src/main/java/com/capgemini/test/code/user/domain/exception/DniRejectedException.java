package com.capgemini.test.code.user.domain.exception;

/** El servicio externo de validación ha rechazado el DNI. */
public class DniRejectedException extends UserValidationException {

  public DniRejectedException() {
    super("dni");
  }
}
