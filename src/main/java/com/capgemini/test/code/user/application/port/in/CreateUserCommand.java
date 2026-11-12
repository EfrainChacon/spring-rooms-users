package com.capgemini.test.code.user.application.port.in;

/** Datos de entrada tal y como llegan; validarlos es responsabilidad del dominio. */
public record CreateUserCommand(long roomId, String name, String email, String phone, String rol, String dni) {
}
