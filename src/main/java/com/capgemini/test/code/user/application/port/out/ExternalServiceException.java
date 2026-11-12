package com.capgemini.test.code.user.application.port.out;

/** Un servicio externo no responde o responde algo inesperado. No es culpa del cliente: se responde 503. */
public class ExternalServiceException extends RuntimeException {

  private final String service;

  public ExternalServiceException(String service, Throwable cause) {
    super(service + " service unavailable", cause);
    this.service = service;
  }

  public String service() {
    return service;
  }
}
