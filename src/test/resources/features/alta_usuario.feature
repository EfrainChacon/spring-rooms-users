# language: es
Característica: Alta de usuarios en la sala 1
  Como responsable de las salas
  Quiero dar de alta usuarios con sus datos validados
  Para que solo entren usuarios correctos y se les avise por el canal de su rol

  Escenario: Un admin se guarda en la sala 1 y recibe un email
    Cuando doy de alta al usuario:
      | name  | email           | phone     | rol   | dni       |
      | pablo | email@email.com | 677998899 | admin | 23454234W |
    Entonces la respuesta tiene el código 201
    Y la respuesta contiene el ID generado
    Y el usuario queda guardado en la sala 1
    Y se ha enviado un email a "email@email.com" con el mensaje "usuario guardado"
    Y no se ha enviado ningún SMS

  Escenario: Un superadmin se guarda y recibe un SMS
    Cuando doy de alta al usuario:
      | name | email         | phone     | rol        | dni       |
      | ana  | ana@email.com | 600111222 | superadmin | 11111111H |
    Entonces la respuesta tiene el código 201
    Y se ha enviado un SMS al "600111222" con el mensaje "usuario guardado"
    Y no se ha enviado ningún email

  Esquema del escenario: Un dato inválido se rechaza sin consultar el DNI ni guardar nada (<caso>)
    Cuando doy de alta al usuario:
      | name   | email   | phone     | rol   | dni       |
      | <name> | <email> | 677998899 | <rol> | 23454234W |
    Entonces la respuesta tiene el código 409
    Y el error es 409 con el mensaje "error validation <campo>"
    Y no se ha consultado el servicio de DNI
    Y no hay ningún usuario guardado
    Y no se ha enviado ningún email

    Ejemplos:
      | caso                    | name    | email           | rol   | campo    |
      | nombre de 7 caracteres  | pablito | email@email.com | admin | userName |
      | email sin arroba        | pablo   | emailemail.com  | admin | email    |
      | email sin punto         | pablo   | email@emailcom  | admin | email    |
      | rol no permitido        | pablo   | email@email.com | user  | rol      |

  Escenario: Un DNI rechazado por el servicio externo no se guarda
    Cuando doy de alta al usuario:
      | name  | email           | phone     | rol   | dni       |
      | pablo | email@email.com | 677998899 | admin | 99999999w |
    Entonces la respuesta tiene el código 409
    Y el error es 409 con el mensaje "error validation dni"
    Y no hay ningún usuario guardado
    Y no se ha enviado ningún email

  Escenario: No se puede dar de alta dos veces el mismo email
    Dado que existe el usuario:
      | name  | email           | phone     | rol   | dni       |
      | pablo | email@email.com | 677998899 | admin | 23454234W |
    Cuando doy de alta al usuario:
      | name | email           | phone     | rol        | dni       |
      | otro | EMAIL@email.com | 600111222 | superadmin | 11111111H |
    Entonces la respuesta tiene el código 409
    Y el error es 409 con el mensaje "error validation email"
    Y no se ha consultado el servicio de DNI
    Y no se ha enviado ningún SMS
