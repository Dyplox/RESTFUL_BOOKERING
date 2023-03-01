# language: es
# author: @AbelGuAr
# encoding: utf-8

Característica: Realizar las pruebas automatizadas al modulo booking del servicios a RESTFUL - BOOKER


  # GETS
  Escenario: Verificar si la lista de kookings es valida
    Dado que Abel quiere logueaser en la Api
    Cuando el llama el servicio para ver la lista de bookings
    Entonces el verifica que el codigo HTTP sea 200
    Entonces el deberia recibir el token y validar el esquema "getBookingIds.json"

  Escenario: Verificar si la lista de kookings es valida filtrando por QueryParams Names
    Dado que Abel quiere logueaser en la Api
    Cuando el llama el servicio para ver la lista de bookings con nombre "Jane" y "Doe"
    Entonces el verifica que el codigo HTTP sea 200
    Entonces el deberia recibir el token y validar el esquema "getBookingIds.json"

  Escenario: Verificar si la lista de kookings es valida filtrando por QueryParams Check In/Out
    Dado que Abel quiere logueaser en la Api
    Cuando el llama el servicio para ver la lista de bookings con Check "2018-01-01" y "2019-01-01"
    Entonces el verifica que el codigo HTTP sea 200
    Entonces el deberia recibir el token y validar el esquema "getBookingIds.json"

  Escenario: Verificar si la lista de kookings es valida filtrando por ID
    Dado que Abel quiere logueaser en la Api
    Cuando el llama el servicio para ver el bookin con id 11
    Entonces el verifica que el codigo HTTP sea 200
    Entonces el deberia recibir el token y validar el esquema "getBookingID.json"

 ##GETS
   # % sign in search keyword should not redirect to 404 ERROR
   # Pagination should be tested for searches returning high number of records
   # Validate search rules defined for “Exact Match” with the search key word
   # Validate search rules defined for “Similar Match” with the search key word

  # CREATE
  Escenario: Verificar si la creacion de kookings es valida
    Dado que Abel quiere logueaser en la Api
    Cuando el llama el servicio para crear el bookin
    Entonces el verifica que el codigo HTTP sea 200
    Entonces el deberia recibir el token y validar el esquema "postBooking.json"

  # UPDATE
  Escenario: Verificar si la actualizacion de kookings es valida
    Dado que Abel quiere logueaser en la Api
    Cuando el ingresa "admin" and "password123"
    Entonces el verifica que el codigo HTTP sea 200
    Y el Guarda el token
    Cuando el llama el servicio para actualizar el bookin 1212
    Entonces el verifica que el codigo HTTP sea 200
    Entonces el deberia recibir el token y validar el esquema "postBooking.json"

  Escenario: Verificar si la actualizacion parcial del kookings es valida
    Dado que Abel quiere logueaser en la Api
    Cuando el ingresa "admin" and "password123"
    Entonces el verifica que el codigo HTTP sea 200
    Y el Guarda el token
    Cuando el llama el servicio para actualizar parcialmente el bookin 1214
    Entonces el verifica que el codigo HTTP sea 200
    Entonces el deberia recibir el token y validar el esquema "postBooking.json"


   # Validate Location Field
   # Enter valid “From” Date in the date picker field
   # Enter invalid “From” Date in the date picker field – Eg from date is greater than to date.
   # Enter valid data “Room Type” field
   # Enter invalid data “Room Type” field
   # Enter valid “number of guests”
   # Enter invalid “number of guests”