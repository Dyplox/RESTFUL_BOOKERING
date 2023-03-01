# language: es
# author: @AbelGuAr
# encoding: utf-8

Característica: Realizar las pruebas automatizadas al modulo ping del servicios a RESTFUL - BOOKER

  Escenario: Verificar si el tiempo de respuesta del servio esta en los tiempos deseados
    Dado que Abel quiere logueaser en la Api
    Cuando el realiza el llamado al endpoint de HealthCheck
    Entonces el deberia ver un tiempo menor a 500