package com.capgemini.test.code.user.application.port.out;

public interface DniVerifier {

  /**
   * @return {@code true} si el servicio acepta el DNI, {@code false} si lo rechaza
   * @throws ExternalServiceException si no se ha podido obtener una respuesta válida
   */
  boolean isValid(String dni);
}
