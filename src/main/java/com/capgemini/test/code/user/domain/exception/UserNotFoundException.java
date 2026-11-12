package com.capgemini.test.code.user.domain.exception;

import com.capgemini.test.code.shared.domain.DomainException;

public class UserNotFoundException extends DomainException {

  public UserNotFoundException(long roomId, long userId) {
    super("user " + userId + " not found in room " + roomId);
  }
}
