package com.capgemini.test.code.shared.infrastructure.rest;

/** Cuerpo de error común a toda la API: {@code {"code": 409, "message": "error validation email"}}. */
public record ApiError(int code, String message) {
}
