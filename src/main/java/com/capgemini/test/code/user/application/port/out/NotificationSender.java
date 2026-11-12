package com.capgemini.test.code.user.application.port.out;

import com.capgemini.test.code.user.domain.model.NotificationChannel;
import com.capgemini.test.code.user.domain.model.User;

/** Estrategia de envío para un canal. Un canal nuevo es una implementación nueva, sin tocar el servicio. */
public interface NotificationSender {

  NotificationChannel channel();

  void send(User recipient, String message);
}
