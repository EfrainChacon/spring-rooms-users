package com.capgemini.test.code.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.capgemini.test.code.user.application.port.in.CreateUserCommand;
import com.capgemini.test.code.user.application.port.out.DniVerifier;
import com.capgemini.test.code.user.application.port.out.ExternalServiceException;
import com.capgemini.test.code.user.application.port.out.RoomRepository;
import com.capgemini.test.code.user.application.port.out.UserRepository;
import com.capgemini.test.code.user.domain.exception.DniRejectedException;
import com.capgemini.test.code.user.domain.exception.RoomNotFoundException;
import com.capgemini.test.code.user.domain.exception.UserAlreadyExistsException;
import com.capgemini.test.code.user.domain.exception.UserNotFoundException;
import com.capgemini.test.code.user.domain.exception.UserValidationException;
import com.capgemini.test.code.user.domain.model.Role;
import com.capgemini.test.code.user.domain.model.User;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  private static final long ROOM_ID = 1L;

  @Mock
  private UserRepository userRepository;
  @Mock
  private RoomRepository roomRepository;
  @Mock
  private DniVerifier dniVerifier;
  @Mock
  private UserNotifier userNotifier;

  private UserService service;

  @BeforeEach
  void setUp() {
    service = new UserService(userRepository, roomRepository, dniVerifier, userNotifier);
  }

  private static CreateUserCommand command(String name, String rol) {
    return new CreateUserCommand(ROOM_ID, name, "email@email.com", "677998899", rol, "23454234W");
  }

  @Test
  void guarda_notifica_y_devuelve_el_id() {
    when(roomRepository.existsById(ROOM_ID)).thenReturn(true);
    when(userRepository.existsByEmail("email@email.com")).thenReturn(false);
    when(dniVerifier.isValid("23454234W")).thenReturn(true);
    when(userRepository.save(any())).thenAnswer(invocation -> invocation.<User>getArgument(0).withId(42L));

    long id = service.create(command("pablo", "admin"));

    assertThat(id).isEqualTo(42L);
    InOrder order = inOrder(dniVerifier, userRepository, userNotifier);
    order.verify(dniVerifier).isValid("23454234W");
    order.verify(userRepository).save(any());
    order.verify(userNotifier).notifyUserSaved(argThat(u -> u.id() == 42L));
  }

  @Test
  void un_dato_invalido_se_rechaza_sin_tocar_bd_ni_servicios_externos() {
    assertThatThrownBy(() -> service.create(command("pablito", "admin")))
        .isInstanceOf(UserValidationException.class)
        .hasMessage("error validation userName");

    verifyNoInteractions(roomRepository, userRepository, dniVerifier, userNotifier);
  }

  @Test
  void un_rol_invalido_se_rechaza_antes_de_nada() {
    assertThatThrownBy(() -> service.create(command("pablo", "user")))
        .hasMessage("error validation rol");

    verifyNoInteractions(roomRepository, userRepository, dniVerifier, userNotifier);
  }

  @Test
  void falla_si_la_sala_no_existe() {
    when(roomRepository.existsById(ROOM_ID)).thenReturn(false);

    assertThatThrownBy(() -> service.create(command("pablo", "admin")))
        .isInstanceOf(RoomNotFoundException.class);

    verifyNoInteractions(userRepository, dniVerifier, userNotifier);
  }

  @Test
  void un_email_existente_se_rechaza_sin_llamar_al_servicio_de_dni() {
    when(roomRepository.existsById(ROOM_ID)).thenReturn(true);
    when(userRepository.existsByEmail("email@email.com")).thenReturn(true);

    assertThatThrownBy(() -> service.create(command("pablo", "admin")))
        .isInstanceOf(UserAlreadyExistsException.class)
        .hasMessage("error validation email");

    verifyNoInteractions(dniVerifier, userNotifier);
    verify(userRepository, never()).save(any());
  }

  @Test
  void un_dni_rechazado_no_se_guarda_ni_se_notifica() {
    when(roomRepository.existsById(ROOM_ID)).thenReturn(true);
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(dniVerifier.isValid("23454234W")).thenReturn(false);

    assertThatThrownBy(() -> service.create(command("pablo", "admin")))
        .isInstanceOf(DniRejectedException.class)
        .hasMessage("error validation dni");

    verify(userRepository, never()).save(any());
    verifyNoInteractions(userNotifier);
  }

  @Test
  void si_el_servicio_de_dni_cae_no_se_guarda_nada() {
    when(roomRepository.existsById(ROOM_ID)).thenReturn(true);
    when(userRepository.existsByEmail(anyString())).thenReturn(false);
    when(dniVerifier.isValid(anyString())).thenThrow(new ExternalServiceException("dni", new RuntimeException()));

    assertThatThrownBy(() -> service.create(command("pablo", "admin")))
        .isInstanceOf(ExternalServiceException.class);

    verify(userRepository, never()).save(any());
  }

  @Test
  void obtiene_un_usuario_de_la_sala() {
    User stored = new User(5L, "pablo", "a@b.com", "600", Role.SUPERADMIN, "1X", ROOM_ID);
    when(userRepository.findByIdAndRoomId(5L, ROOM_ID)).thenReturn(Optional.of(stored));

    assertThat(service.get(ROOM_ID, 5L)).isEqualTo(stored);
  }

  @Test
  void falla_si_el_usuario_no_esta_en_la_sala() {
    when(userRepository.findByIdAndRoomId(anyLong(), anyLong())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.get(ROOM_ID, 5L)).isInstanceOf(UserNotFoundException.class);
  }
}
