package com.capgemini.test.code.user.domain.exception;

import com.capgemini.test.code.shared.domain.DomainException;

public class RoomNotFoundException extends DomainException {

  public RoomNotFoundException(long roomId) {
    super("room " + roomId + " not found");
  }
}
