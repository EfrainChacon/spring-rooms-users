package com.capgemini.test.code.user.infrastructure.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.capgemini.test.code.user.application.port.out.DniVerifier;
import com.capgemini.test.code.user.application.port.out.RoomRepository;
import com.capgemini.test.code.user.application.port.out.UserRepository;
import com.capgemini.test.code.user.application.service.UserNotifier;
import org.junit.jupiter.api.Test;

class UserModuleConfigTest {

  private final RoomRepository roomRepository = mock(RoomRepository.class);
  private final UserModuleConfig config = new UserModuleConfig();

  @Test
  void no_arranca_si_la_sala_por_defecto_no_existe() {
    when(roomRepository.existsById(99L)).thenReturn(false);

    assertThatThrownBy(() -> config.userService(mock(UserRepository.class), roomRepository,
        mock(DniVerifier.class), mock(UserNotifier.class), 99L))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("99");
  }

  @Test
  void arranca_si_la_sala_por_defecto_existe() {
    when(roomRepository.existsById(1L)).thenReturn(true);

    assertThat(config.userService(mock(UserRepository.class), roomRepository,
        mock(DniVerifier.class), mock(UserNotifier.class), 1L)).isNotNull();
  }
}
