# language: es
# author: @AbelGuAr
# encoding: utf-8

@booking
Característica: Gestión de reservas en Restful Booker
  Cada escenario crea su propia reserva, con apellido y fechas únicos, y trabaja sobre ella sin
  depender de datos existentes en la API. La reserva se elimina al terminar el escenario.

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
    Cuando el consulta las reservas con el nombre de la reserva creada
    Entonces el código de respuesta debería ser 200
    Y la respuesta debería cumplir el esquema "getBookingIds.json"
    Y la lista debería contener solo la reserva creada

  @negativo
  Escenario: Filtrar reservas por un nombre que no existe devuelve una lista vacía
    Cuando el consulta las reservas con nombre "NoExisteXyz123" y apellido "NoExisteXyz123"
    Entonces el código de respuesta debería ser 200
    Y la respuesta debería cumplir el esquema "getBookingIds.json"
    Y la lista debería estar vacía

  # Los filtros de fecha son estrictos: checkin = entra DESPUÉS de la fecha, checkout = sale ANTES
  Escenario: Filtrar reservas por un rango de fechas que contiene la reserva
    Dado el tiene una reserva creada
    Cuando el consulta las reservas con fechas que rodean la reserva creada
    Entonces el código de respuesta debería ser 200
    Y la respuesta debería cumplir el esquema "getBookingIds.json"
    Y la lista debería contener la reserva creada

  Escenario: Filtrar reservas por fechas excluye las que entran el mismo día del filtro
    Dado el tiene una reserva creada
    Cuando el consulta las reservas con entrada posterior a la de la reserva creada
    Entonces el código de respuesta debería ser 200
    Y la respuesta debería cumplir el esquema "getBookingIds.json"
    Y la lista no debería contener la reserva creada

  @smoke
  Escenario: Consultar una reserva por su id
    Dado el tiene una reserva creada
    Cuando el consulta la reserva creada
    Entonces el código de respuesta debería ser 200
    Y la respuesta debería cumplir el esquema "booking.json"
    Y la respuesta debería coincidir con la reserva creada
    Y la cabecera "Content-Type" de la respuesta debería contener "application/json"

  Escenario: Consultar una reserva en formato XML
    Dado el tiene una reserva creada
    Cuando el consulta la reserva creada en formato "application/xml"
    Entonces el código de respuesta debería ser 200
    Y el cuerpo de la respuesta debería contener el apellido de la reserva creada en XML

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
    Y la reserva de la respuesta debería coincidir con la enviada

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
    Y la respuesta debería coincidir con la reserva enviada
    Y al consultarla de nuevo debería coincidir con la reserva enviada

  Escenario: Actualizar parcialmente una reserva
    Dado el tiene una reserva creada
    Y el está autenticado
    Cuando el actualiza parcialmente la reserva creada
    Entonces el código de respuesta debería ser 200
    Y la respuesta debería cumplir el esquema "booking.json"
    # PATCH cambia nombre y apellido y debe conservar el resto de la reserva
    Y la respuesta debería tener los cambios enviados sobre la reserva creada

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

  # ---------- Ciclo de vida ----------

  Escenario: Ciclo de vida completo de una reserva
    Dado el está autenticado
    Cuando el crea una reserva
    Entonces el código de respuesta debería ser 200
    Y la reserva de la respuesta debería coincidir con la enviada
    Cuando el consulta la reserva creada
    Entonces el código de respuesta debería ser 200
    Y la respuesta debería coincidir con la reserva creada
    Cuando el actualiza la reserva creada
    Entonces el código de respuesta debería ser 200
    Y al consultarla de nuevo debería coincidir con la reserva enviada
    Cuando el elimina la reserva creada mediante cookie
    Entonces el código de respuesta debería ser 201
    Y la reserva creada ya no debería existir

  # ---------- Valores límite válidos ----------

  Esquema del escenario: Crear una reserva con valores límite válidos (<caso>)
    Cuando el envía la siguiente reserva:
      """
      {"firstname": "<nombre>", "lastname": "Limites", "totalprice": <precio>, "depositpaid": true,
       "bookingdates": {"checkin": "2031-01-10", "checkout": "2031-01-12"}, "additionalneeds": "Breakfast"}
      """
    Entonces el código de respuesta debería ser 200
    Y la respuesta debería cumplir el esquema "postBooking.json"
    Y la reserva de la respuesta debería coincidir con la enviada

    Ejemplos:
      | caso                      | nombre            | precio     |
      | precio cero               | Jim               | 0          |
      | precio máximo de un int   | Jim               | 2147483647 |
      | nombre Unicode y emoji    | José Ñúñez 日本 🚀 | 100        |

  Escenario: Crear una reserva sin el campo opcional additionalneeds
    Cuando el envía la siguiente reserva:
      """
      {"firstname": "Jim", "lastname": "Limites", "totalprice": 100, "depositpaid": true,
       "bookingdates": {"checkin": "2031-01-10", "checkout": "2031-01-12"}}
      """
    Entonces el código de respuesta debería ser 200
    Y la respuesta debería cumplir el esquema "postBooking.json"
    Y la reserva de la respuesta debería coincidir con la enviada

  # El esquema (additionalProperties: false) garantiza que el campo extra no se devuelve
  Escenario: Crear una reserva con un campo desconocido lo ignora
    Cuando el envía la siguiente reserva:
      """
      {"firstname": "Jim", "lastname": "Limites", "totalprice": 100, "depositpaid": true,
       "bookingdates": {"checkin": "2031-01-10", "checkout": "2031-01-12"}, "admin": true}
      """
    Entonces el código de respuesta debería ser 200
    Y la respuesta debería cumplir el esquema "postBooking.json"
    Y la reserva de la respuesta debería coincidir con la enviada

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

  @negativo @defectoConocido
  Esquema del escenario: Crear una reserva con datos inválidos debería devolver 400 (<caso>)
    Cuando el envía la siguiente reserva:
      """
      {"firstname": <nombre>, "lastname": "Limites", "totalprice": <precio>, "depositpaid": <deposito>,
       "bookingdates": {"checkin": <checkin>, "checkout": "2031-12-31"}}
      """
    Entonces el código de respuesta debería ser 400

    Ejemplos:
      | caso                                            | nombre | precio | deposito | checkin      |
      | precio negativo (hoy lo acepta)                 | "Jim"  | -100   | true     | "2031-01-10" |
      | precio con decimales (hoy lo trunca)            | "Jim"  | 99.99  | true     | "2031-01-10" |
      | nombre vacío (hoy lo acepta)                    | ""     | 100    | true     | "2031-01-10" |
      | nombre numérico (hoy devuelve 500)              | 123    | 100    | true     | "2031-01-10" |
      | depósito como texto (hoy lo convierte en true)  | "Jim"  | 100    | "yes"    | "2031-01-10" |
      | 30 de febrero (hoy lo cambia a 2 de marzo)      | "Jim"  | 100    | true     | "2031-02-30" |
      | mes 13 (hoy guarda "0NaN-aN-aN")                | "Jim"  | 100    | true     | "2031-13-01" |
      | fecha en texto (hoy guarda "0NaN-aN-aN")        | "Jim"  | 100    | true     | "mañana"     |
