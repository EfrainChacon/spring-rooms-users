package com.capgemini.test.code.shared.infrastructure.notification;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Cliente de la API de notificaciones. Está en {@code shared} porque no pertenece al contexto de
 * usuarios: pagos o pedidos lo reutilizarían con sus propios adaptadores.
 */
@FeignClient(name = "notification-client", url = "${external.service.url}")
public interface NotificationClient {

  @PostMapping("/email")
  void sendEmail(@RequestBody EmailNotification request);

  @PostMapping("/sms")
  void sendSms(@RequestBody SmsNotification request);

  record EmailNotification(String email, String message) {
  }

  record SmsNotification(String phone, String message) {
  }
}
