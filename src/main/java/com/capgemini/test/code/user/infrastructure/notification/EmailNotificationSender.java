package com.capgemini.test.code.user.infrastructure.notification;

import com.capgemini.test.code.shared.infrastructure.notification.NotificationClient;
import com.capgemini.test.code.shared.infrastructure.notification.NotificationClient.EmailNotification;
import com.capgemini.test.code.user.application.port.out.NotificationSender;
import com.capgemini.test.code.user.domain.model.NotificationChannel;
import com.capgemini.test.code.user.domain.model.User;
import org.springframework.stereotype.Component;

@Component
class EmailNotificationSender implements NotificationSender {

  private final NotificationClient client;

  EmailNotificationSender(NotificationClient client) {
    this.client = client;
  }

  @Override
  public NotificationChannel channel() {
    return NotificationChannel.EMAIL;
  }

  @Override
  public void send(User recipient, String message) {
    client.sendEmail(new EmailNotification(recipient.email(), message));
  }
}
