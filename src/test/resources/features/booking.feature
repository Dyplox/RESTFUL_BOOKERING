# language: es
# author: @AbelGuAr
# encoding: utf-8

@booking
Característica: Gestión de reservas en Restful Booker
  Cada escenario crea su propia reserva y trabaja sobre ella, sin depender de datos existentes
  en la API pública. La reserva se elimina al terminar el escenario.

  Antecedentes:
    Dado que Abel quiere usar la API

  # ---------- Consultas ----------

  Escenario: Consultar la lista de reservas
    Dado el tiene una reserva creada
    Cuando el consulta la lista de reservas
    Entonces el código de respuesta debería ser 200
    Y la respuesta debería cumplir el esquema "getBookingIds.json"
    Y la lista debería contener la reserva creada

  Escenario: Filtrar reservas por nombre y apellido
    Dado el tiene una reserva creada
    Cuando el consulta las reservas con nombre "Jim" y apellido "Brown"
    Entonces el código de respuesta debería ser 200
    Y la respuesta debería cumplir el esquema "getBookingIds.json"
    Y la lista debería contener la reserva creada

  @negativo
  Escenario: Filtrar reservas por un nombre que no existe devuelve una lista vacía
    Cuando el consulta las reservas con nombre "NoExisteXyz123" y apellido "NoExisteXyz123"
    Entonces el código de respuesta debería ser 200
    Y la respuesta debería cumplir el esquema "getBookingIds.json"
    Y la lista debería estar vacía

  Escenario: Filtrar reservas por fechas de check-in y check-out
    Cuando el consulta las reservas con check-in "2018-01-01" y check-out "2019-01-01"
    Entonces el código de respuesta debería ser 200
    Y la respuesta debería cumplir el esquema "getBookingIds.json"

  @smoke
  Escenario: Consultar una reserva por su id
    Dado el tiene una reserva creada
    Cuando el consulta la reserva creada
    Entonces el código de respuesta debería ser 200
    Y la respuesta debería cumplir el esquema "booking.json"
    Y el campo "firstname" de la respuesta debería ser "Jim"
    Y el campo "lastname" de la respuesta debería ser "Brown"
    Y el campo "bookingdates.checkin" de la respuesta debería ser "2018-01-01"

  Escenario: Consultar una reserva en formato XML
    Dado el tiene una reserva creada
    Cuando el consulta la reserva creada en formato "application/xml"
    Entonces el código de respuesta debería ser 200
    Y el cuerpo de la respuesta debería contener "<firstname>Jim</firstname>"

  @negativo
  Esquema del escenario: Consultar una reserva que no existe devuelve 404 (<caso>)
    Cuando el consulta la reserva con id "<id>"
    Entonces el código de respuesta debería ser 404
    Y el cuerpo de la respuesta debería ser "Not Found"

    Ejemplos:
      | caso           | id        |
      | id inexistente | 999999999 |
      | id no numérico | abc       |

  @negativo
  Escenario: Pedir la respuesta en un formato no soportado devuelve 418
    Cuando el envía la siguiente reserva aceptando "text/plain":
      """
      {"firstname": "Jim", "lastname": "Brown", "totalprice": 111, "depositpaid": true,
       "bookingdates": {"checkin": "2018-01-01", "checkout": "2019-01-01"}}
      """
    Entonces el código de respuesta debería ser 418

  # ---------- Creación ----------

  @smoke
  Escenario: Crear una reserva
    Cuando el crea una reserva
    Entonces el código de respuesta debería ser 200
    Y la respuesta debería cumplir el esquema "postBooking.json"
    Y el campo "booking.firstname" de la respuesta debería ser "Jim"
    Y el campo "booking.totalprice" de la respuesta debería ser "111"

  @negativo
  Escenario: Crear una reserva con JSON malformado devuelve 400
    Cuando el envía la siguiente reserva:
      """
      {firstname
      """
    Entonces el código de respuesta debería ser 400

  # ---------- Actualización ----------

  @smoke
  Escenario: Actualizar una reserva completa
    Dado el tiene una reserva creada
    Y el está autenticado
    Cuando el actualiza la reserva creada
    Entonces el código de respuesta debería ser 200
    Y la respuesta debería cumplir el esquema "booking.json"
    Y el campo "firstname" de la respuesta debería ser "Juan"
    Y el campo "totalprice" de la respuesta debería ser "9898"
    Y el campo "additionalneeds" de la respuesta debería ser "lunch"

  Escenario: Actualizar parcialmente una reserva
    Dado el tiene una reserva creada
    Y el está autenticado
    Cuando el actualiza parcialmente la reserva creada
    Entonces el código de respuesta debería ser 200
    Y la respuesta debería cumplir el esquema "booking.json"
    Y el campo "firstname" de la respuesta debería ser "Jose"
    Y el campo "lastname" de la respuesta debería ser "Gutierrez Rendon"
    # PATCH no debe tocar los campos que no se envían
    Y el campo "totalprice" de la respuesta debería ser "111"

  @negativo
  Escenario: Actualizar una reserva sin token devuelve 403
    Dado el tiene una reserva creada
    Cuando el actualiza la reserva creada
    Entonces el código de respuesta debería ser 403
    Y el cuerpo de la respuesta debería ser "Forbidden"

  @negativo
  Escenario: Actualizar una reserva con un token inválido devuelve 403
    Dado el tiene una reserva creada
    Y el tiene un token inválido
    Cuando el actualiza la reserva creada
    Entonces el código de respuesta debería ser 403

  @negativo
  Escenario: Actualizar parcialmente una reserva sin token devuelve 403
    Dado el tiene una reserva creada
    Cuando el actualiza parcialmente la reserva creada
    Entonces el código de respuesta debería ser 403

  # ---------- Eliminación ----------

  @smoke
  Escenario: Eliminar una reserva autenticándose por cookie
    Dado el tiene una reserva creada
    Y el está autenticado
    Cuando el elimina la reserva creada mediante cookie
    Entonces el código de respuesta debería ser 201
    Y la reserva creada ya no debería existir

  Escenario: Eliminar una reserva autenticándose con Basic Auth
    Dado el tiene una reserva creada
    Cuando el elimina la reserva creada mediante Basic Auth
    Entonces el código de respuesta debería ser 201
    Y la reserva creada ya no debería existir

  @negativo
  Escenario: Eliminar una reserva sin token devuelve 403 y no la borra
    Dado el tiene una reserva creada
    Cuando el elimina la reserva creada mediante cookie
    Entonces el código de respuesta debería ser 403
    Y la reserva creada debería seguir existiendo

  @negativo
  Escenario: Eliminar una reserva con un token inválido devuelve 403 y no la borra
    Dado el tiene una reserva creada
    Y el tiene un token inválido
    Cuando el elimina la reserva creada mediante cookie
    Entonces el código de respuesta debería ser 403
    Y la reserva creada debería seguir existiendo

  @negativo
  Escenario: Eliminar una reserva con Basic Auth incorrecto devuelve 403 y no la borra
    Dado el tiene una reserva creada
    Cuando el elimina la reserva creada mediante Basic Auth con usuario "admin" y contraseña "mala"
    Entonces el código de respuesta debería ser 403
    Y la reserva creada debería seguir existiendo

  @negativo
  Escenario: Eliminar una reserva que ya fue eliminada devuelve 405
    Dado el tiene una reserva creada
    Y el elimina la reserva creada mediante Basic Auth
    Cuando el elimina la reserva creada mediante Basic Auth
    Entonces el código de respuesta debería ser 405

  # ---------- Defectos conocidos de la API ----------
  # Restful Booker tiene errores intencionados para practicar testing. Estos escenarios describen el
  # comportamiento CORRECTO, así que hoy fallan. Están excluidos de la ejecución por defecto
  # (tag @defectoConocido); se ejecutan con: ./gradlew test -Ptags="@defectoConocido"

  @negativo @defectoConocido
  Escenario: Crear una reserva sin campos obligatorios debería devolver 400 (hoy devuelve 500)
    Cuando el envía la siguiente reserva:
      """
      {"lastname": "Brown", "totalprice": 111, "depositpaid": true,
       "bookingdates": {"checkin": "2018-01-01", "checkout": "2019-01-01"}}
      """
    Entonces el código de respuesta debería ser 400

  @negativo @defectoConocido
  Escenario: Crear una reserva con checkout anterior al checkin debería rechazarse (hoy la acepta)
    Cuando el envía la siguiente reserva:
      """
      {"firstname": "Jim", "lastname": "Brown", "totalprice": 111, "depositpaid": true,
       "bookingdates": {"checkin": "2019-01-01", "checkout": "2018-01-01"}}
      """
    Entonces el código de respuesta debería ser 400

  @negativo @defectoConocido
  Escenario: Crear una reserva con un precio no numérico debería rechazarse (hoy guarda null)
    Cuando el envía la siguiente reserva:
      """
      {"firstname": "Jim", "lastname": "Brown", "totalprice": "abc", "depositpaid": true,
       "bookingdates": {"checkin": "2018-01-01", "checkout": "2019-01-01"}}
      """
    Entonces el código de respuesta debería ser 400

  @negativo @defectoConocido
  Escenario: Crear una reserva con Content-Type incorrecto debería devolver 415 (hoy devuelve 500)
    Cuando el envía la siguiente reserva con Content-Type "text/plain":
      """
      {"firstname": "Jim", "lastname": "Brown", "totalprice": 111, "depositpaid": true,
       "bookingdates": {"checkin": "2018-01-01", "checkout": "2019-01-01"}}
      """
    Entonces el código de respuesta debería ser 415

  @defectoConocido
  Escenario: La respuesta XML debería declarar Content-Type XML (hoy declara text/html)
    Dado el tiene una reserva creada
    Cuando el consulta la reserva creada en formato "application/xml"
    Entonces el código de respuesta debería ser 200
    Y la cabecera "Content-Type" de la respuesta debería contener "xml"
