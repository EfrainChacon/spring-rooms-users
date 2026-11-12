# language: es
Característica: Consulta de un usuario de la sala 1

  Escenario: Se obtiene un usuario guardado
    Dado que existe el usuario:
      | name  | email           | phone     | rol   | dni       |
      | pablo | email@email.com | 677998899 | admin | 23454234W |
    Cuando consulto ese usuario
    Entonces la respuesta tiene el código 200
    Y la respuesta contiene el usuario:
      | name  | email           | phone     | rol   | dni       | roomId |
      | pablo | email@email.com | 677998899 | admin | 23454234W | 1      |

  Escenario: Un usuario de otra sala no se obtiene, porque la consulta es dentro de la sala 1
    Dado que en la sala 2 hay un usuario guardado
    Cuando consulto ese usuario
    Entonces la respuesta tiene el código 404

  Escenario: Un usuario inexistente devuelve 404
    Cuando consulto el usuario 123456
    Entonces la respuesta tiene el código 404
    Y el error es 404 con el mensaje "user 123456 not found"
