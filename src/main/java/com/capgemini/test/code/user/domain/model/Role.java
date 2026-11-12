package com.capgemini.test.code.user.domain.model;

import com.capgemini.test.code.user.domain.exception.UserValidationException;
import java.util.Arrays;
import java.util.Locale;

/**
 * Roles admitidos. Cada rol declara por qué canal se le notifica: añadir un rol obliga a elegir su
 * canal al compilar, en vez de tener que acordarse de tocar un if/else en el servicio.
 */
public enum Role {
  ADMIN("admin", NotificationChannel.EMAIL),
  SUPERADMIN("superadmin", NotificationChannel.SMS);

  public static final String FIELD = "rol";

  private final String value;
  private final NotificationChannel notificationChannel;

  Role(String value, NotificationChannel notificationChannel) {
    this.value = value;
    this.notificationChannel = notificationChannel;
  }

  /** Acepta el valor sin distinguir mayúsculas ("admin", "Admin"...). */
  public static Role fromValue(String raw) {
    if (raw == null) {
      throw new UserValidationException(FIELD);
    }
    String normalized = raw.trim().toLowerCase(Locale.ROOT);
    return Arrays.stream(values())
        .filter(role -> role.value.equals(normalized))
        .findFirst()
        .orElseThrow(() -> new UserValidationException(FIELD));
  }

  public String value() {
    return value;
  }

  public NotificationChannel notificationChannel() {
    return notificationChannel;
  }
}
