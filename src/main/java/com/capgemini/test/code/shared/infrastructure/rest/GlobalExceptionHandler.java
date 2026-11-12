package com.capgemini.test.code.shared.infrastructure.rest;

import com.capgemini.test.code.user.application.port.out.ExternalServiceException;
import com.capgemini.test.code.user.domain.exception.RoomNotFoundException;
import com.capgemini.test.code.user.domain.exception.UserNotFoundException;
import com.capgemini.test.code.user.domain.exception.UserValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/** Traducción centralizada de excepciones a respuestas HTTP, con el mismo formato en todos los casos. */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /** Incluye email duplicado y DNI rechazado: el contrato fija 409 para cualquier fallo de validación. */
  @ExceptionHandler(UserValidationException.class)
  ResponseEntity<ApiError> validation(UserValidationException e) {
    return error(HttpStatus.CONFLICT, e.getMessage());
  }

  @ExceptionHandler({UserNotFoundException.class, RoomNotFoundException.class})
  ResponseEntity<ApiError> notFound(RuntimeException e) {
    return error(HttpStatus.NOT_FOUND, e.getMessage());
  }

  @ExceptionHandler(ExternalServiceException.class)
  ResponseEntity<ApiError> externalService(ExternalServiceException e) {
    LOG.error("Fallo del servicio externo {}", e.service(), e);
    return error(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
  }

  @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
  ResponseEntity<ApiError> badRequest(Exception e) {
    return error(HttpStatus.BAD_REQUEST, "malformed request");
  }

  /** Errores de Spring MVC (405, 415, ruta inexistente...) conservan su código; el resto es un 500. */
  @ExceptionHandler(Exception.class)
  ResponseEntity<ApiError> unexpected(Exception e) {
    if (e instanceof ErrorResponse frameworkError) {
      return error(frameworkError.getStatusCode(), frameworkError.getBody().getDetail());
    }
    LOG.error("Error no controlado", e);
    return error(HttpStatus.INTERNAL_SERVER_ERROR, "internal error");
  }

  private static ResponseEntity<ApiError> error(HttpStatusCode status, String message) {
    return ResponseEntity.status(status).body(new ApiError(status.value(), message));
  }
}
