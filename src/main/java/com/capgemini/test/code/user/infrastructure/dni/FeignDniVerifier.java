package com.capgemini.test.code.user.infrastructure.dni;

import com.capgemini.test.code.user.application.port.out.DniVerifier;
import com.capgemini.test.code.user.application.port.out.ExternalServiceException;
import feign.FeignException;
import org.springframework.stereotype.Component;

/**
 * Traduce el protocolo HTTP del servicio de DNI al lenguaje del dominio: un 409 es "DNI rechazado"
 * (respuesta de negocio); cualquier otro fallo es "servicio no disponible" (problema técnico).
 *
 * <p>El DNI se envía tal cual: el servicio es la fuente de verdad y distingue mayúsculas
 * ("99999999w" se rechaza, "99999999W" no), así que normalizarlo aquí cambiaría su respuesta.
 */
@Component
class FeignDniVerifier implements DniVerifier {

  private final DniClient client;

  FeignDniVerifier(DniClient client) {
    this.client = client;
  }

  @Override
  public boolean isValid(String dni) {
    try {
      client.check(new CheckDniRequest(dni));
      return true;
    } catch (FeignException.Conflict rejected) {
      return false;
    } catch (FeignException e) {
      throw new ExternalServiceException("dni", e);
    }
  }
}
