# Changelog

Los cambios relevantes del proyecto se documentan aquí. El formato sigue
[Keep a Changelog](https://keepachangelog.com/es-ES/1.1.0/) y el proyecto usa
[Semantic Versioning](https://semver.org/lang/es/):

- **MAJOR**: cambios incompatibles para quien ejecuta o extiende la suite (pasos de Cucumber, tags,
  tareas o propiedades de Gradle, claves de `serenity.conf`).
- **MINOR**: escenarios, entornos o funcionalidades nuevas que mantienen la compatibilidad.
- **PATCH**: correcciones, actualizaciones de dependencias y cambios internos.

Las actualizaciones de Dependabot no se listan una a una; ver el historial de commits de cada release.

## [Sin publicar]

## [1.0.0] - 2026-11-19

Primera versión publicada: la suite pasa de prueba de concepto a automatización mantenible y verificada en CI.

### Añadido

- Ejecución contra un contenedor Docker local (`docker compose up -d`, `-Penv=docker`) además de la API pública.
- Escenarios: casos negativos (403, 404, 400, 405, 418), ciclo de vida completo, filtros exactos y por fechas,
  formato XML y valores límite. Cada escenario crea datos únicos y los elimina al terminar.
- 13 escenarios `@defectoConocido` que documentan errores de Restful Booker, vigilados a diario en CI.
- Tags (`@smoke`, `@negativo`, `@defectoConocido`, `@rendimiento`) con un runner único y ejecución en paralelo.
- Pruebas unitarias (esquemas JSON, fábrica de datos, reintentos, configuración) y `validarFeatures` (dry-run).
- CI en GitHub Actions: verificación sin red, pruebas de API, resumen por ejecución, reporte de Serenity en
  GitHub Pages, cobertura con JaCoCo y análisis en SonarQube Cloud.
- Dependabot para Gradle, GitHub Actions y la imagen Docker; actions fijadas por SHA e imagen por digest.
- Workflow de release, `CHANGELOG.md` y licencia MIT.

### Cambiado

- Java 27, Gradle 9.9.1, Serenity 5.4.2, Cucumber 8.1.0, RestAssured 6.3.0 y JUnit 6.1.3; versiones en
  `gradle/libs.versions.toml`.
- JSON Schema reales (draft-04) en lugar de ejemplos de respuesta; aserciones con Serenity Ensure.
- Configuración por entornos y credenciales en `serenity.conf` (con prioridad para variables de entorno).

### Corregido

- `GuardaToken` fallaba con `NullPointerException`; el login "válido" usaba una contraseña incorrecta.
- Las validaciones de esquema aceptaban cualquier respuesta y había IDs de reserva fijos.

[Sin publicar]: https://github.com/Dyplox/RESTFUL_BOOKERING/compare/v1.0.0...HEAD
[1.0.0]: https://github.com/Dyplox/RESTFUL_BOOKERING/releases/tag/v1.0.0
