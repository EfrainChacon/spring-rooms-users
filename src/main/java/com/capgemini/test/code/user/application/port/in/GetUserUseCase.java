package com.capgemini.test.code.user.application.port.in;

import com.capgemini.test.code.user.domain.model.User;

public interface GetUserUseCase {

  User get(long roomId, long userId);
}
