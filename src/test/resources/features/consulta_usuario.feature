# language: es
Característica: Consulta de un usuario de una sala

  Escenario: Se obtiene un usuario guardado en la sala 1
    Dado que existe en la sala 1 el usuario:
      | name  | email           | phone     | rol   | dni       |
      | pablo | email@email.com | 677998899 | admin | 23454234W |
    Cuando consulto ese usuario en la sala 1
    Entonces la respuesta tiene el código 200
    Y la respuesta contiene el usuario:
      | name  | email           | phone     | rol   | dni       | roomId |
      | pablo | email@email.com | 677998899 | admin | 23454234W | 1      |

  Escenario: Un usuario de la sala 1 no se ve desde la sala 2
    Dado que existe en la sala 1 el usuario:
      | name  | email           | phone     | rol   | dni       |
      | pablo | email@email.com | 677998899 | admin | 23454234W |
    Cuando consulto ese usuario en la sala 2
    Entonces la respuesta tiene el código 404

  Escenario: Un usuario inexistente devuelve 404
    Cuando consulto el usuario 123456 en la sala 1
    Entonces la respuesta tiene el código 404
    Y el error es 404 con el mensaje "user 123456 not found in room 1"
