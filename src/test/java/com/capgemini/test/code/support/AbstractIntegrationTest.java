package com.capgemini.test.code.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MockServerContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

/**
 * Infraestructura real para las pruebas de integración y aceptación: la misma imagen de PostgreSQL y
 * el mismo {@code init.json} de MockServer que usa docker-compose, así que se prueba contra el
 * contrato real de las APIs externas y no contra una copia que pueda divergir.
 *
 * <p>Contenedores singleton: se arrancan una vez por JVM y los comparten todas las clases de test
 * (y el contexto de Spring, que también se cachea).
 */
public abstract class AbstractIntegrationTest {

  private static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:15-alpine");

  private static final MockServerContainer MOCK_SERVER =
      new MockServerContainer(DockerImageName.parse("mockserver/mockserver:5.15.0"))
          .withCopyFileToContainer(MountableFile.forHostPath("docker/mockserver-config/init.json"), "/config/init.json")
          .withEnv("MOCKSERVER_INITIALIZATION_JSON_PATH", "/config/init.json");

  static {
    Startables.deepStart(POSTGRES, MOCK_SERVER).join();
  }

  @DynamicPropertySource
  static void infrastructureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRES::getUsername);
    registry.add("spring.datasource.password", POSTGRES::getPassword);
    registry.add("external.service.url", MOCK_SERVER::getEndpoint);
  }

  protected static MockServerVerifier mockServer() {
    return new MockServerVerifier(MOCK_SERVER.getEndpoint());
  }
}
