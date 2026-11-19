# language: es
# author: @AbelGuAr
# encoding: utf-8

@auth
Característica: Autenticación en Restful Booker
  Como consumidor de la API
  Quiero obtener un token con mis credenciales
  Para poder modificar y eliminar reservas

  Antecedentes:
    Dado que Abel quiere usar la API

  @smoke
  Escenario: Iniciar sesión con usuario y contraseña válidos
    Cuando el se autentica con las credenciales configuradas
    Entonces el código de respuesta debería ser 200
    Y la respuesta debería cumplir el esquema "token.json"
    Y el tiempo de respuesta debería ser menor a 1500 ms

  # La API responde 200 también con credenciales inválidas, pero con {"reason": "Bad credentials"} en vez de token
  @negativo
  Esquema del escenario: No se puede iniciar sesión con credenciales inválidas (<caso>)
    Cuando el se autentica con usuario "<usuario>" y contraseña "<contraseña>"
    Entonces el código de respuesta debería ser 200
    Y la respuesta debería cumplir el esquema "authError.json"
    Y el campo "reason" de la respuesta debería ser "Bad credentials"

    Ejemplos:
      | caso                            | usuario  | contraseña  |
      | contraseña incorrecta           | admin    | BadPassword |
      | usuario incorrecto              | BadAdmin | password123 |
      | usuario vacío                   |          | password123 |
      | contraseña vacía                | admin    |             |
      | ambos vacíos                    |          |             |
      | contraseña parecida a la válida | admin    | password    |
      | usuario numérico                | 1995     | password    |
      | caracteres especiales           | $&*@#    | $&*@#_()    |
