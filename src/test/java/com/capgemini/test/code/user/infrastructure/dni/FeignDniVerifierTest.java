package com.capgemini.test.code.user.infrastructure.dni;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.capgemini.test.code.user.application.port.out.ExternalServiceException;
import feign.FeignException;
import feign.Request;
import feign.RetryableException;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.junit.jupiter.api.Test;

class FeignDniVerifierTest {

  private static final Request REQUEST = Request.create(Request.HttpMethod.PATCH, "http://mock/check-dni",
      Map.of(), null, StandardCharsets.UTF_8, null);

  private final DniClient client = mock(DniClient.class);
  private final FeignDniVerifier verifier = new FeignDniVerifier(client);

  @Test
  void un_200_es_un_dni_valido() {
    when(client.check(any())).thenReturn(new CheckDniResponse("Valid DNI"));

    assertThat(verifier.isValid("23454234W")).isTrue();
  }

  @Test
  void un_409_es_un_dni_rechazado() {
    when(client.check(any())).thenThrow(new FeignException.Conflict("Invalid DNI", REQUEST, null, Map.of()));

    assertThat(verifier.isValid("99999999w")).isFalse();
  }

  @Test
  void un_5xx_es_un_fallo_del_servicio_no_del_usuario() {
    when(client.check(any())).thenThrow(new FeignException.InternalServerError("boom", REQUEST, null, Map.of()));

    assertThatThrownBy(() -> verifier.isValid("23454234W")).isInstanceOf(ExternalServiceException.class);
  }

  @Test
  void un_timeout_es_un_fallo_del_servicio() {
    when(client.check(any())).thenThrow(new RetryableException(-1, "timeout", Request.HttpMethod.PATCH,
        new SocketTimeoutException(), (Long) null, REQUEST));

    assertThatThrownBy(() -> verifier.isValid("23454234W")).isInstanceOf(ExternalServiceException.class);
  }
}
