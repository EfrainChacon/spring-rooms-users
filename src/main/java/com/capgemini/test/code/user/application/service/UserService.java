package com.capgemini.test.code.user.application.service;

import com.capgemini.test.code.user.application.port.in.CreateUserCommand;
import com.capgemini.test.code.user.application.port.in.CreateUserUseCase;
import com.capgemini.test.code.user.application.port.in.GetUserUseCase;
import com.capgemini.test.code.user.application.port.out.DniVerifier;
import com.capgemini.test.code.user.application.port.out.RoomRepository;
import com.capgemini.test.code.user.application.port.out.UserRepository;
import com.capgemini.test.code.user.domain.exception.DniRejectedException;
import com.capgemini.test.code.user.domain.exception.RoomNotFoundException;
import com.capgemini.test.code.user.domain.exception.UserAlreadyExistsException;
import com.capgemini.test.code.user.domain.exception.UserNotFoundException;
import com.capgemini.test.code.user.domain.model.Role;
import com.capgemini.test.code.user.domain.model.User;

/**
 * Casos de uso de usuarios. No lleva anotaciones de Spring: se instancia en {@code UserModuleConfig},
 * así que se prueba con mocks sencillos y no depende del framework.
 *
 * <p>Las comprobaciones van de la más barata a la más cara (memoria, BD, API externa) para no llamar
 * al servicio de DNI con peticiones que se iban a rechazar igualmente.
 */
public class UserService implements CreateUserUseCase, GetUserUseCase {

  private final UserRepository userRepository;
  private final RoomRepository roomRepository;
  private final DniVerifier dniVerifier;
  private final UserNotifier userNotifier;

  public UserService(UserRepository userRepository, RoomRepository roomRepository,
      DniVerifier dniVerifier, UserNotifier userNotifier) {
    this.userRepository = userRepository;
    this.roomRepository = roomRepository;
    this.dniVerifier = dniVerifier;
    this.userNotifier = userNotifier;
  }

  @Override
  public long create(CreateUserCommand command) {
    User user = User.create(command.name(), command.email(), command.phone(),
        Role.fromValue(command.rol()), command.dni(), command.roomId());

    if (!roomRepository.existsById(user.roomId())) {
      throw new RoomNotFoundException(user.roomId());
    }
    if (userRepository.existsByEmail(user.email())) {
      throw new UserAlreadyExistsException("email");
    }
    if (!dniVerifier.isValid(user.dni())) {
      throw new DniRejectedException();
    }

    User saved = userRepository.save(user);
    userNotifier.notifyUserSaved(saved);
    return saved.id();
  }

  @Override
  public User get(long roomId, long userId) {
    return userRepository.findByIdAndRoomId(userId, roomId)
        .orElseThrow(() -> new UserNotFoundException(roomId, userId));
  }
}
