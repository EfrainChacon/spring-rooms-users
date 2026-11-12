package com.capgemini.test.code.user.infrastructure.rest;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * El ID viaja como string, como pide el contrato ({@code "id": "<id>"}). Además, en un cliente
 * JavaScript un long por encima de 2^53 perdería precisión al parsearse como number.
 */
public record CreateUserResponse(@Schema(description = "ID generado (long como string)", example = "1") String id) {
}
