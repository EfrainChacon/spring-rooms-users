package com.capgemini.test.code.acceptance;

import com.capgemini.test.code.support.AbstractIntegrationTest;
import com.capgemini.test.code.support.MockServerVerifier;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/** App completa en un puerto aleatorio, con PostgreSQL y MockServer en contenedores. */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration extends AbstractIntegrationTest {

  static MockServerVerifier verifier() {
    return mockServer();
  }
}
