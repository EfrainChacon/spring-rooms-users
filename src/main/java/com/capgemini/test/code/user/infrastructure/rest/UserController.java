package com.capgemini.test.code.user.infrastructure.rest;

import com.capgemini.test.code.shared.infrastructure.rest.ApiError;
import com.capgemini.test.code.user.application.port.in.CreateUserUseCase;
import com.capgemini.test.code.user.application.port.in.GetUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 *
 * <p>Las anotaciones OpenAPI documentan el contrato real (201/409/404/400/503) para Swagger y para generar
 * clientes; no afectan al comportamiento.
 */
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Usuarios", description = "Alta y consulta de usuarios de la sala 1")
public class UserController {

  private static final String JSON = "application/json";

  private final CreateUserUseCase createUserUseCase;
  private final GetUserUseCase getUserUseCase;

  public UserController(CreateUserUseCase createUserUseCase, GetUserUseCase getUserUseCase) {
    this.createUserUseCase = createUserUseCase;
    this.getUserUseCase = getUserUseCase;
  }

  @Operation(summary = "Da de alta un usuario en la sala 1",
      description = "Valida los datos y el DNI (contra el servicio externo), guarda el usuario y lo notifica "
          + "por email (admin) o SMS (superadmin).")
  @ApiResponse(responseCode = "201", description = "Usuario creado",
      headers = @Header(name = "Location", description = "URL del usuario creado"),
      content = @Content(mediaType = JSON, schema = @Schema(implementation = CreateUserResponse.class)))
  @ApiResponse(responseCode = "409", description = "Validación fallida: nombre, email, rol, DNI o teléfono "
      + "inválidos, o email/DNI/teléfono ya registrados",
      content = @Content(mediaType = JSON, schema = @Schema(implementation = ApiError.class), examples = {
          @ExampleObject(name = "Nombre de más de 6 caracteres",
              value = "{\"code\": 409, \"message\": \"error validation userName\"}"),
          @ExampleObject(name = "Email inválido o ya registrado",
              value = "{\"code\": 409, \"message\": \"error validation email\"}"),
          @ExampleObject(name = "DNI rechazado por el servicio externo",
              value = "{\"code\": 409, \"message\": \"error validation dni\"}")}))
  @ApiResponse(responseCode = "400", description = "JSON mal formado",
      content = @Content(mediaType = JSON, schema = @Schema(implementation = ApiError.class),
          examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"malformed request\"}")))
  @ApiResponse(responseCode = "503", description = "El servicio de validación de DNI no responde",
      content = @Content(mediaType = JSON, schema = @Schema(implementation = ApiError.class),
          examples = @ExampleObject(value = "{\"code\": 503, \"message\": \"dni service unavailable\"}")))
  @PostMapping
  public ResponseEntity<CreateUserResponse> create(@RequestBody CreateUserRequest request) {
    long id = createUserUseCase.create(request.toCommand());
    URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    return ResponseEntity.created(location).body(new CreateUserResponse(String.valueOf(id)));
  }

  @Operation(summary = "Obtiene un usuario de la sala 1 por su ID")
  @ApiResponse(responseCode = "200", description = "Usuario encontrado",
      content = @Content(mediaType = JSON, schema = @Schema(implementation = UserResponse.class)))
  @ApiResponse(responseCode = "404", description = "No existe ningún usuario con ese ID en la sala 1",
      content = @Content(mediaType = JSON, schema = @Schema(implementation = ApiError.class),
          examples = @ExampleObject(value = "{\"code\": 404, \"message\": \"user 999 not found\"}")))
  @ApiResponse(responseCode = "400", description = "ID no numérico",
      content = @Content(mediaType = JSON, schema = @Schema(implementation = ApiError.class),
          examples = @ExampleObject(value = "{\"code\": 400, \"message\": \"malformed request\"}")))
  @GetMapping("/{userId}")
  public UserResponse get(@PathVariable long userId) {
    return UserResponse.from(getUserUseCase.get(userId));
  }
}
