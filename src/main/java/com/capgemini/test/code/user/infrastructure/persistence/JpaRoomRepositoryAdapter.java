package com.capgemini.test.code.user.infrastructure.persistence;

import com.capgemini.test.code.user.application.port.out.RoomRepository;
import org.springframework.stereotype.Repository;

@Repository
class JpaRoomRepositoryAdapter implements RoomRepository {

  private final RoomJpaRepository jpa;

  JpaRoomRepositoryAdapter(RoomJpaRepository jpa) {
    this.jpa = jpa;
  }

  @Override
  public boolean existsById(long roomId) {
    return jpa.existsById(roomId);
  }
}
