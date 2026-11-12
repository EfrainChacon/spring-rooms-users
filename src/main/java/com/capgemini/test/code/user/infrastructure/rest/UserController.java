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
 * Usuarios como sub-recurso de la sala: el enunciado trabaja sobre la sala 1 ({@code /rooms/1/users}),
 * pero la ruta ya admite cualquier sala sin cambiar el contrato.
 */
@RestController
@RequestMapping("/api/v1/rooms/{roomId}/users")
public class UserController {

  private final CreateUserUseCase createUserUseCase;
  private final GetUserUseCase getUserUseCase;

  public UserController(CreateUserUseCase createUserUseCase, GetUserUseCase getUserUseCase) {
    this.createUserUseCase = createUserUseCase;
    this.getUserUseCase = getUserUseCase;
  }

  @PostMapping
  public ResponseEntity<CreateUserResponse> create(@PathVariable long roomId, @RequestBody CreateUserRequest request) {
    long id = createUserUseCase.create(request.toCommand(roomId));
    URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    return ResponseEntity.created(location).body(new CreateUserResponse(String.valueOf(id)));
  }

  @GetMapping("/{userId}")
  public UserResponse get(@PathVariable long roomId, @PathVariable long userId) {
    return UserResponse.from(getUserUseCase.get(roomId, userId));
  }
}
