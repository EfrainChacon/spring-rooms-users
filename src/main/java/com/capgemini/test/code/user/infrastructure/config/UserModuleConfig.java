package com.capgemini.test.code.user.infrastructure.config;

import com.capgemini.test.code.user.application.port.out.DniVerifier;
import com.capgemini.test.code.user.application.port.out.NotificationSender;
import com.capgemini.test.code.user.application.port.out.RoomRepository;
import com.capgemini.test.code.user.application.port.out.UserRepository;
import com.capgemini.test.code.user.application.service.UserNotifier;
import com.capgemini.test.code.user.application.service.UserService;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Único punto donde Spring conoce la capa de aplicación: aquí se ensamblan puertos y adaptadores. */
@Configuration
public class UserModuleConfig {

  @Bean
  UserNotifier userNotifier(List<NotificationSender> senders) {
    return new UserNotifier(senders);
  }

  @Bean
  UserService userService(UserRepository userRepository, RoomRepository roomRepository,
      DniVerifier dniVerifier, UserNotifier userNotifier) {
    return new UserService(userRepository, roomRepository, dniVerifier, userNotifier);
  }
}
