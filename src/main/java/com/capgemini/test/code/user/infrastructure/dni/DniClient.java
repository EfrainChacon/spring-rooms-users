package com.capgemini.test.code.user.infrastructure.dni;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Cliente del servicio de validación de DNI. Va sobre OkHttp ({@code spring.cloud.openfeign.okhttp}),
 * porque el cliente por defecto de Feign no sabe hacer PATCH. Timeouts y logs en application.yml.
 */
@FeignClient(name = "dni-client", url = "${external.service.url}")
interface DniClient {

  @PatchMapping("/check-dni")
  CheckDniResponse check(@RequestBody CheckDniRequest request);
}
