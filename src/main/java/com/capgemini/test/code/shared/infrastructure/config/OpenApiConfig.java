package com.capgemini.test.code.shared.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

/** Metadatos de la documentación OpenAPI (Swagger UI en /swagger-ui.html). */
@Configuration
@OpenAPIDefinition(info = @Info(
    title = "Rooms & Users API",
    version = "v1",
    description = "Alta y consulta de usuarios de la sala 1. Todos los errores comparten el mismo formato, "
        + "por ejemplo `{\"code\": 409, \"message\": \"error validation email\"}`."))
public class OpenApiConfig {
}
