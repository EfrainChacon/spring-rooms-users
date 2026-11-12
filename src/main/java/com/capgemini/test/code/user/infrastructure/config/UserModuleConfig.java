package com.capgemini.test.code.user.infrastructure.config;

import com.capgemini.test.code.user.application.port.out.DniVerifier;
import com.capgemini.test.code.user.application.port.out.NotificationSender;
import com.capgemini.test.code.user.application.port.out.RoomRepository;
import com.capgemini.test.code.user.application.port.out.UserRepository;
import com.capgemini.test.code.user.application.service.UserNotifier;
import com.capgemini.test.code.user.application.service.UserService;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Único punto donde Spring conoce la capa de aplicación: aquí se ensamblan puertos y adaptadores. */
@Configuration
public class UserModuleConfig {

  @Bean
  UserNotifier userNotifier(List<NotificationSender> senders) {
    return new UserNotifier(senders);
  }

  /**
   * La sala por defecto es configuración del servidor. Si no existe en la BD, la aplicación no arranca: es un
   * error de despliegue y no debe llegar al cliente como un 404 en cada petición.
   */
  @Bean
  UserService userService(UserRepository userRepository, RoomRepository roomRepository, DniVerifier dniVerifier,
      UserNotifier userNotifier, @Value("${app.users.default-room-id}") long defaultRoomId) {
    if (!roomRepository.existsById(defaultRoomId)) {
      throw new IllegalStateException("La sala por defecto " + defaultRoomId + " no existe en la base de datos");
    }
    return new UserService(userRepository, dniVerifier, userNotifier, defaultRoomId);
  }
}
