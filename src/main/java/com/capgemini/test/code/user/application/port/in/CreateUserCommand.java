package com.capgemini.test.code.user.application.port.in;

/**
 * Datos de entrada tal y como llegan; validarlos es responsabilidad del dominio. No incluye la sala: la asigna el
 * servidor.
 */
public record CreateUserCommand(String name, String email, String phone, String rol, String dni) {
}
