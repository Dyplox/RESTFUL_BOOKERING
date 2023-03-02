# language: es
# author: @AbelGuAr
# encoding: utf-8

Característica: Realizar las pruebas automatizadas al modulo auth del servicios a RESTFUL - BOOKER

  #P Verify if a user will be able to login with a valid username and valid password.                     OK
  Escenario: Verificar si un usuario podra iniciar sesion con un username y una password validos
    Dado que Abel quiere logueaser en la Api
    Cuando el ingresa "admin" and "password"
    Y el Guarda el token
    Entonces el verifica que el codigo HTTP sea 200
    Entonces el deberia recibir el token y validar el esquema "token.json"
    # Verify the time taken to log in with a valid username and password.
    Entonces el deberia ver un tiempo menor a 1500


  #N Verify if a user cannot login with a valid username and an invalid password.                         OK
  #N Verify if a user cannot login with an invalid username and a valid password.                         OK
  #N Verify that the validation message gets displayed in case the user leaves the username field blank.  OK
  #N Verify that the validation message gets displayed in case the user leaves the password field blank.  OK
  Esquema del escenario: Verificar si un usuario podra iniciar sesion con un username y una password invalidos
    Dado que Abel quiere logueaser en la Api
    Cuando el ingresa "<username>" and "<password>"
    Entonces el verifica que el codigo HTTP sea 200
    Entonces el deberia recibir el token y validar el esquema "token.json"
    Ejemplos:
      | username | password    |
      | admin    | BadPassword |
      | BadAdmin | password123 |
      |          | password123 |
      | admin    |             |

      | admin    | password    |
      | 1995     | password    |
      | admin    | 12345667    |
      | $&*@#    | $&*@#_()    |