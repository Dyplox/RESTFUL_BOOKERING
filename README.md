# RESTFUL_BOOKERING

[![CI](https://github.com/Dyplox/RESTFUL_BOOKERING/actions/workflows/ci.yml/badge.svg?branch=main)](https://github.com/Dyplox/RESTFUL_BOOKERING/actions/workflows/ci.yml)
[![Reporte Serenity](https://img.shields.io/badge/reporte-Serenity-2ea44f)](https://dyplox.github.io/RESTFUL_BOOKERING/)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Dyplox_RESTFUL_BOOKERING&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Dyplox_RESTFUL_BOOKERING)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Dyplox_RESTFUL_BOOKERING&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Dyplox_RESTFUL_BOOKERING)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=Dyplox_RESTFUL_BOOKERING&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=Dyplox_RESTFUL_BOOKERING)
[![Release](https://img.shields.io/github/v/release/Dyplox/RESTFUL_BOOKERING)](https://github.com/Dyplox/RESTFUL_BOOKERING/releases)
[![Java](https://img.shields.io/badge/Java-27-orange)](https://openjdk.org/projects/jdk/27/)
[![License: MIT](https://img.shields.io/badge/license-MIT-blue)](LICENSE)

Pruebas automatizadas de la API pública [Restful Booker](https://restful-booker.herokuapp.com/apidoc/index.html) con **Serenity BDD**, patrón **Screenplay**, **Cucumber** (escenarios en español) y **RestAssured**, más pruebas unitarias con **JUnit Jupiter** y un pipeline de **GitHub Actions**.

Cubre:

- **Auth**: token con credenciales válidas; 8 combinaciones de credenciales inválidas.
- **Booking**: CRUD completo, ciclo de vida de punta a punta, filtros por nombre y fechas, formato XML, valores límite (precio 0 y máximo, Unicode, campos opcionales y desconocidos) y casos negativos (403 sin token o con token inválido, 404, 400, 405, 418). Cada escenario crea su propia reserva con datos únicos y la elimina al terminar.
- **Ping**: health check y tiempo de respuesta.
- **Defectos conocidos de la API**: escenarios que documentan los errores intencionados de Restful Booker (excluidos por defecto).

Cada prueba valida el **código HTTP** y, según el caso, la respuesta contra un **JSON Schema**, **valores concretos** del body, **cabeceras** y **efectos** (p. ej. que una reserva borrada devuelva 404).

> 📘 Arquitectura, flujo de una prueba y pendientes: [`docs/CONTEXTO.md`](docs/CONTEXTO.md)

## Stack

| Tecnología | Versión | Uso en el proyecto |
|---|---|---|
| Java (JDK) | 27 | Lenguaje; configurado como *toolchain* de Gradle |
| Gradle (wrapper) | 9.9.1 | Compilación, dependencias y ejecución de pruebas |
| Serenity BDD (`serenity-core`) | 5.4.2 | Framework de pruebas y reportes HTML |
| Serenity Screenplay REST | 5.4.2 | Patrón Screenplay e interacciones HTTP (`Get`, `Post`, `Put`, `Patch`, `Delete`) |
| Serenity Cucumber | 5.4.2 | Integración de Cucumber con Serenity (`SerenityReporterParallel`) |
| Serenity Gradle Plugin | 5.4.2 | Reporte agregado (`aggregate`) |
| Cucumber (`cucumber-java`, `cucumber-junit-platform-engine`) | 8.1.0 | Escenarios BDD en Gherkin, en español, en paralelo |
| RestAssured | 6.3.0 | Motor HTTP bajo Serenity (`SerenityRest`) |
| RestAssured JSON Schema Validator | 6.3.0 | Validación del body contra JSON Schema |
| Serenity Ensure | 5.4.2 | Aserciones que aparecen en el reporte con lo esperado y lo obtenido |
| Hamcrest | 3.0 | Matchers (pruebas unitarias) |
| JUnit Platform (`junit-platform-suite`) | 6.1.3 | Runner de Cucumber como `@Suite` |
| JUnit Jupiter | 6.1.3 | Pruebas unitarias |
| Gson | 2.15.0 | Serialización JSON |
| SonarQube Gradle Plugin | 7.6.0 | Análisis estático de código (`./gradlew sonar`) |
| GitHub Actions | — | CI: unitarias, validación de features y pruebas de API |

## Requisitos

- **JDK 27**
- Para las pruebas de API, una de estas dos:
  - **Docker** (recomendado): `docker compose up -d` (imagen fijada por *digest* en `docker-compose.yml`)
  - Conexión a internet, para usar la API pública
- Las pruebas unitarias no necesitan nada de esto

Gradle no hace falta instalarlo: el proyecto incluye el wrapper.

## Instalación

```bash
git clone https://github.com/Dyplox/RESTFUL_BOOKERING.git
cd RESTFUL_BOOKERING
```

## Ejecución

```bash
# Pruebas de API (todo menos @defectoConocido y @rendimiento) + reporte Serenity
./gradlew test -Penv=docker   # contra el contenedor local (rápido, aislado)
./gradlew test                # contra la API pública (entorno por defecto)

# Filtrar por tags
./gradlew test -Ptags="@smoke"
./gradlew test -Ptags="@booking and @negativo"
./gradlew test -Ptags="@defectoConocido"   # documentan errores de la API: hoy fallan
./gradlew test -Ptags="@rendimiento"       # dependen de la latencia de red

# Pruebas unitarias (sin red, ~1 s)
./gradlew unitTest

# Comprobar sin red que todos los pasos de las features están implementados
./gradlew validarFeatures

# Todo: unitarias + validación de features + API
./gradlew check

# Cobertura combinada (unitarias + API): build/reports/jacoco/test/html/index.html
# y umbral mínimo (90 % de líneas, 70 % de ramas): el build falla si baja
./gradlew unitTest test jacocoTestReport jacocoTestCoverageVerification -Penv=docker
```

El reporte HTML se regenera en cada ejecución en `target/site/serenity/index.html`. Si una prueba falla, el build termina en `BUILD FAILED` y el reporte se genera igualmente.

### Tags

| Tag | Significado |
|---|---|
| `@auth`, `@booking`, `@ping` | Módulo de la API (nivel feature) |
| `@smoke` | Camino feliz mínimo: login, crear, consultar, actualizar, eliminar y health check |
| `@negativo` | Casos de error esperados (credenciales inválidas, sin token, ids inexistentes…) |
| `@defectoConocido` | Describen el comportamiento correcto donde la API tiene errores; hoy fallan. Excluidos por defecto |
| `@rendimiento` | Umbrales de tiempo de respuesta. Excluidos por defecto |

Aunque se pase `-Ptags`, `@defectoConocido` y `@rendimiento` se siguen excluyendo salvo que la expresión los mencione.

## CI

`.github/workflows/ci.yml` se ejecuta en cada push a `main`, en cada pull request, a diario (06:00 UTC, para detectar cambios en la API pública) y a mano con una expresión de tags opcional:

1. **verificacion** (sin red): `unitTest`, `validarFeatures` y `javadoc`.
2. **api** (si el anterior pasa):
   - En push y PR, contra un contenedor de Restful Booker levantado como *service*.
   - En la ejecución diaria, contra la API pública, para detectar si cambia. También corre `@rendimiento`, a título informativo.
   - En la ejecución manual se elige el entorno.

Cada ejecución deja un **resumen** (aprobadas, fallidas, excluidas y los fallos) en la página del run. El reporte de Serenity y los resultados JUnit se publican como artefactos aunque haya fallos.

Además:
- **Vigilancia diaria:** ejecuta los `@defectoConocido` y **falla si alguno empieza a pasar**, lo que indicaría que la API lo corrigió y hay que reclasificar el escenario. También corre `@rendimiento`, a título informativo.
- **Reporte en GitHub Pages:** en cada push a `main` se publica el reporte de Serenity. Requiere activarlo una vez en *Settings → Pages → Source: GitHub Actions*.
- **Dependencias al día:** Dependabot abre PRs semanales para Gradle (`gradle/libs.versions.toml`), las GitHub Actions (fijadas por SHA) y la imagen Docker (fijada por *digest*).

## Calidad y releases

- **Cobertura mínima**: el CI combina la cobertura de las pruebas unitarias y de API y falla si baja del 90 % de líneas o del 70 % de ramas (umbral en `build.gradle`).
- **`main` protegida**: los cambios entran por pull request, con los jobs de unitarias y de API en verde y la rama al día.
- **SonarQube Cloud**: el CI analiza el código en cada push y PR, con la cobertura combinada de JaCoCo, y espera al *quality gate*. Necesita el secreto `SONAR_TOKEN` en el repositorio; sin él, el paso se omite.
- **Releases**: la versión está en `gradle.properties` y los cambios en [`CHANGELOG.md`](CHANGELOG.md). Para publicar:
  1. En un PR, subir la versión y mover las notas de `[Sin publicar]` a `## [<versión>] - <fecha>`.
  2. Tras el merge: `git tag v<versión> origin/main && git push origin v<versión>`. El workflow `release` verifica el tag y publica la release con el Javadoc.

## Configuración

- **Entornos y credenciales**: `src/test/resources/serenity.conf`. Hay tres entornos: `default`/`publico` (API pública) y `docker` (`localhost:3001`); se elige con `-Penv`. Las credenciales se pueden sobrescribir con las variables de entorno `RESTFUL_BOOKER_USUARIO` y `RESTFUL_BOOKER_CLAVE`.
- **Cucumber** (glue, plugins, filtro de tags por defecto, paralelismo): `src/test/resources/junit-platform.properties`.
- **Cliente HTTP**: lo configura `ClienteApi` una sola vez:
  - Cabeceras JSON comunes.
  - Reintento ante 502, 503, 504 o errores de conexión, solo en métodos idempotentes.
  - Antes de empezar, la suite comprueba que la API responde y, si no, se detiene con un mensaje claro.

## Estructura

```
src/main/java/co/booker/
├── tasks/        Acciones del actor (auth, ping y CRUD de booking)
├── questions/    Lecturas de la respuesta (código, tiempo, campo JSON, cabecera, cuerpo)
├── models/       Booking y BookingDates (records) y ReservaFactory (datos únicos por escenario)
└── utils/        Configuración, cliente HTTP, reintentos, JSON, autenticación, endpoints y constantes

src/test/java/co/booker/
├── runners/          CucumberTestSuite: una sola suite, se filtra por tags
├── stepdefinitions/  Pasos Cucumber → tareas Screenplay (ComunDefinition tiene hooks y validaciones)
└── unitarias/        Pruebas unitarias (esquemas, fábrica de datos, reintentos, configuración…)

src/test/resources/
├── features/                  auth, booking, ping (.feature)
├── *.json                     JSON Schema de cada respuesta
├── serenity.conf              Entornos y credenciales
└── junit-platform.properties  Configuración de Cucumber

gradle/libs.versions.toml      Versiones de todas las dependencias
docker-compose.yml             Restful Booker local (imagen fijada por digest)
.github/
├── workflows/ci.yml           Pipeline
├── scripts/resumen_pruebas.py Resumen de resultados y vigilancia de defectos
└── dependabot.yml             Actualizaciones automáticas
```

## Ejemplo de escenario

```gherkin
@negativo
Escenario: Eliminar una reserva sin token devuelve 403 y no la borra
  Dado el tiene una reserva creada
  Cuando el elimina la reserva creada mediante cookie
  Entonces el código de respuesta debería ser 403
  Y la reserva creada debería seguir existiendo
```

## Estado

| Suite | Pruebas | Resultado |
|---|---|---|
| API (por defecto) | 40 | ✅ en Docker (~3 s) y en la API pública (~11 s), en paralelo y en ejecuciones repetidas |
| `@defectoConocido` | 13 | ❌ fallan a propósito: documentan errores de la API |
| `@rendimiento` | 1 | ⚠️ depende de la latencia |
| Unitarias | 58 | ✅ |
| Validación de features | 54 escenarios | ✅ todos los pasos implementados |

## Licencia

[MIT](LICENSE)

## Autor

**Abel Fernando Gutierrez Arias** — SR QA Engineer / SDET — [GitHub @Dyplox](https://github.com/Dyplox)
