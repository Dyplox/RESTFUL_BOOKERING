# language: es
# author: @AbelGuAr
# encoding: utf-8

@ping
Característica: Health check de Restful Booker

  Antecedentes:
    Dado que Abel quiere usar la API

  @smoke
  Escenario: El health check responde
    Cuando el consulta el health check
    Entonces el código de respuesta debería ser 201

  # Depende de la latencia de red: excluido por defecto, se ejecuta con -Ptags="@rendimiento"
  @rendimiento
  Escenario: El health check responde en el tiempo esperado
    Cuando el consulta el health check
    Entonces el tiempo de respuesta debería ser menor a 500 ms
