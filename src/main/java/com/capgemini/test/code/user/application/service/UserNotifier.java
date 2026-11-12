package com.capgemini.test.code.user.application.service;

import com.capgemini.test.code.user.application.port.out.NotificationSender;
import com.capgemini.test.code.user.domain.model.NotificationChannel;
import com.capgemini.test.code.user.domain.model.Role;
import com.capgemini.test.code.user.domain.model.User;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Elige la estrategia de notificación según el canal del rol del usuario.
 *
 * <p>La notificación es best-effort: el usuario ya está guardado y su ID es la respuesta correcta, así
 * que un fallo al notificar se registra pero no convierte el alta en un error. Para garantizar la
 * entrega haría falta un outbox transaccional (ver README).
 */
public class UserNotifier {

  static final String USER_SAVED_MESSAGE = "usuario guardado";

  private static final Logger LOG = Logger.getLogger(UserNotifier.class.getName());

  private final Map<NotificationChannel, NotificationSender> senders = new EnumMap<>(NotificationChannel.class);

  public UserNotifier(Collection<NotificationSender> availableSenders) {
    availableSenders.forEach(sender -> senders.put(sender.channel(), sender));
    // Falla al arrancar, no con la primera petición de un superadmin.
    Arrays.stream(Role.values())
        .map(Role::notificationChannel)
        .filter(channel -> !senders.containsKey(channel))
        .findFirst()
        .ifPresent(channel -> {
          throw new IllegalStateException("No hay NotificationSender para el canal " + channel);
        });
  }

  public void notifyUserSaved(User user) {
    NotificationSender sender = senders.get(user.role().notificationChannel());
    try {
      sender.send(user, USER_SAVED_MESSAGE);
    } catch (RuntimeException e) {
      LOG.log(Level.WARNING, e, () -> "No se pudo notificar por " + sender.channel() + " al usuario " + user.id());
    }
  }
}
