package com.capgemini.test.code.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.capgemini.test.code.user.application.port.out.NotificationSender;
import com.capgemini.test.code.user.domain.model.NotificationChannel;
import com.capgemini.test.code.user.domain.model.Role;
import com.capgemini.test.code.user.domain.model.User;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class UserNotifierTest {

  /** Doble de prueba a mano: registra lo que envía y opcionalmente falla. */
  private static final class RecordingSender implements NotificationSender {
    private final NotificationChannel channel;
    private final boolean failing;
    private final List<String> sent = new ArrayList<>();

    RecordingSender(NotificationChannel channel, boolean failing) {
      this.channel = channel;
      this.failing = failing;
    }

    @Override
    public NotificationChannel channel() {
      return channel;
    }

    @Override
    public void send(User recipient, String message) {
      if (failing) {
        throw new IllegalStateException("proveedor caído");
      }
      sent.add(recipient.name() + ":" + message);
    }
  }

  private static User user(Role role) {
    return new User(1L, "pablo", "a@b.com", "600", role, "1X", 1L);
  }

  @Test
  void un_admin_recibe_email_y_un_superadmin_sms() {
    RecordingSender email = new RecordingSender(NotificationChannel.EMAIL, false);
    RecordingSender sms = new RecordingSender(NotificationChannel.SMS, false);
    UserNotifier notifier = new UserNotifier(List.of(email, sms));

    notifier.notifyUserSaved(user(Role.ADMIN));
    notifier.notifyUserSaved(user(Role.SUPERADMIN));

    assertThat(email.sent).containsExactly("pablo:usuario guardado");
    assertThat(sms.sent).containsExactly("pablo:usuario guardado");
  }

  @Test
  void un_fallo_al_notificar_no_se_propaga() {
    UserNotifier notifier = new UserNotifier(List.of(
        new RecordingSender(NotificationChannel.EMAIL, true),
        new RecordingSender(NotificationChannel.SMS, false)));

    assertThatCode(() -> notifier.notifyUserSaved(user(Role.ADMIN))).doesNotThrowAnyException();
  }

  @Test
  void no_arranca_si_falta_el_canal_de_algun_rol() {
    List<NotificationSender> onlyEmail = List.of(new RecordingSender(NotificationChannel.EMAIL, false));

    assertThatThrownBy(() -> new UserNotifier(onlyEmail))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("SMS");
  }
}
