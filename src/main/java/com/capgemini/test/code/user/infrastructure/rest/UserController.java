package com.capgemini.test.code.user.infrastructure.rest;

import com.capgemini.test.code.user.application.port.in.CreateUserUseCase;
import com.capgemini.test.code.user.application.port.in.GetUserUseCase;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * El cliente no elige sala: el servidor asigna la sala 1 (ver README, «Ambigüedad del enunciado»).
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  private final CreateUserUseCase createUserUseCase;
  private final GetUserUseCase getUserUseCase;

  public UserController(CreateUserUseCase createUserUseCase, GetUserUseCase getUserUseCase) {
    this.createUserUseCase = createUserUseCase;
    this.getUserUseCase = getUserUseCase;
  }

  @PostMapping
  public ResponseEntity<CreateUserResponse> create(@RequestBody CreateUserRequest request) {
    long id = createUserUseCase.create(request.toCommand());
    URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    return ResponseEntity.created(location).body(new CreateUserResponse(String.valueOf(id)));
  }

  @GetMapping("/{userId}")
  public UserResponse get(@PathVariable long userId) {
    return UserResponse.from(getUserUseCase.get(userId));
  }
}
