# RESTFUL_BOOKERING

Pruebas automatizadas de la API pública [Restful Booker](https://restful-booker.herokuapp.com/apidoc/index.html) con **Serenity BDD**, patrón **Screenplay**, **Cucumber** (escenarios en español) y **RestAssured**, más pruebas unitarias con **JUnit Jupiter** y un pipeline de **GitHub Actions**.

Cubre:

- **Auth**: token con credenciales válidas; 8 combinaciones de credenciales inválidas.
- **Booking**: CRUD completo, filtros, formato XML y casos negativos (403 sin token o con token inválido, 404, 400, 405, 418). Cada escenario crea su propia reserva y la elimina al terminar.
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
| Hamcrest | 3.0 | Matchers de aserción |
| JUnit Platform (`junit-platform-suite`) | 6.1.3 | Runner de Cucumber como `@Suite` |
| JUnit Jupiter | 6.1.3 | Pruebas unitarias |
| Gson | 2.15.0 | Serialización JSON |
| SonarQube Gradle Plugin | 7.6.0 | Análisis estático de código (`./gradlew sonar`) |
| GitHub Actions | — | CI: unitarias, validación de features y pruebas de API |

## Requisitos

- **JDK 27**
- Conexión a internet para las pruebas de API (las unitarias no la necesitan)

Gradle no hace falta instalarlo: el proyecto incluye el wrapper.

## Instalación

```bash
git clone https://github.com/Dyplox/RESTFUL_BOOKERING.git
cd RESTFUL_BOOKERING
```

## Ejecución

```bash
# Pruebas de API (todo menos @defectoConocido y @rendimiento) + reporte Serenity
./gradlew test

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
2. **api** (si el anterior pasa): `./gradlew test`. En la ejecución diaria también corre `@rendimiento` a título informativo.

El reporte de Serenity y los resultados JUnit se publican como artefactos aunque haya fallos.

## Configuración

- **URL base**: `restapi.baseurl` en `serenity.properties`.
- **Cucumber** (glue, plugins, filtro de tags por defecto, paralelismo): `src/test/resources/junit-platform.properties`.

## Estructura

```
src/main/java/co/booker/
├── tasks/        Acciones del actor (auth, ping y CRUD de booking)
├── questions/    Lecturas de la respuesta (código, tiempo, campo JSON, cabecera, cuerpo)
├── models/       Modelos de datos
└── utils/        Endpoints, constantes y autenticación

src/test/java/co/booker/
├── runners/          CucumberTestSuite: una sola suite, se filtra por tags
├── stepdefinitions/  Pasos Cucumber → tareas Screenplay (ComunDefinition tiene hooks y validaciones)
└── unitarias/        Pruebas unitarias (esquemas JSON, autenticación, utilidades)

src/test/resources/
├── features/                  auth, booking, ping (.feature)
├── *.json                     JSON Schema de cada respuesta
└── junit-platform.properties  Configuración de Cucumber
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
| API (por defecto) | 32 | ✅ todas pasan, también en paralelo y en ejecuciones repetidas |
| `@defectoConocido` | 5 | ❌ fallan a propósito: documentan errores de la API |
| `@rendimiento` | 1 | ⚠️ depende de la latencia |
| Unitarias | 31 | ✅ |
| Validación de features | 38 escenarios | ✅ todos los pasos implementados |

## Autor

**Abel Fernando Gutierrez Arias** — SR QA Engineer / SDET — [GitHub @Dyplox](https://github.com/Dyplox)
