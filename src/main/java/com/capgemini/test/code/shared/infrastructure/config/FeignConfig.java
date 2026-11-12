package com.capgemini.test.code.shared.infrastructure.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

/**
 * Activa los clientes Feign. Va en una clase propia y no en {@code CodeApplication} para que los tests
 * de slice ({@code @WebMvcTest}) no intenten registrar clientes HTTP que no necesitan.
 */
@Configuration
@EnableFeignClients(basePackages = "com.capgemini.test.code")
public class FeignConfig {
}
