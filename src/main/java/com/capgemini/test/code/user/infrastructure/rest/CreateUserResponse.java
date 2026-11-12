package com.capgemini.test.code.user.infrastructure.rest;

/**
 * El ID viaja como string, como pide el contrato ({@code "id": "<id>"}). Además, en un cliente
 * JavaScript un long por encima de 2^53 perdería precisión al parsearse como number.
 */
public record CreateUserResponse(String id) {
}
