# Contexto del proyecto — RESTFUL_BOOKERING

Documento de referencia técnica: qué es el proyecto, cómo está construido, cómo fluye una prueba y qué queda pendiente. Para instalar y ejecutar, ver el [README](../README.md).

---

## 1. Qué es

Suite de **pruebas automatizadas de API** (no una API) contra [Restful Booker](https://restful-booker.herokuapp.com/), una API pública de reservas de hotel diseñada para practicar testing, con errores introducidos a propósito. La API se reinicia periódicamente; por eso cada escenario crea y elimina sus propios datos.

| Módulo | Endpoint | Qué se prueba |
|---|---|---|
| Auth | `POST /auth` | Token con credenciales válidas; 8 combinaciones inválidas; tiempo de respuesta |
| Booking | `GET/POST/PUT/PATCH/DELETE /booking` | CRUD, filtros, XML, casos negativos (403, 404, 400, 405, 418) y defectos conocidos |
| Ping | `GET /ping` | Health check y tiempo de respuesta |

Además hay **pruebas unitarias** (sin red) y un pipeline de **CI** en GitHub Actions.

## 2. Stack técnico

| Pieza | Versión | Rol |
|---|---|---|
| Java | 27 (toolchain Gradle) | Lenguaje |
| Gradle | 9.9.1 (wrapper) | Build y ejecución |
| Serenity BDD | 5.4.2 | Framework de pruebas + reportes HTML |
| Serenity Screenplay REST | 5.4.2 | Interacciones HTTP (`Get`, `Post`, `Put`, `Patch`, `Delete`) |
| RestAssured | 6.3.0 | Motor HTTP bajo Serenity (`SerenityRest`) |
| json-schema-validator | 6.3.0 | Validación del cuerpo contra JSON Schema (draft-04) |
| Cucumber | 8.1.0 | Escenarios BDD en español (`# language: es`), motor JUnit Platform, en paralelo |
| JUnit Platform / Jupiter | 6.1.3 | Runner de Cucumber (`@Suite`) y pruebas unitarias |
| Hamcrest | 3.0 | Matchers de aserción |
| SonarQube plugin | 7.6.0 | Análisis estático (`./gradlew sonar`) |
| GitHub Actions | — | CI (`.github/workflows/ci.yml`) |

### Ajustes necesarios para Java 27

Serenity REST (`RestSpecificationFactory`) genera clases en tiempo de ejecución con ByteBuddy inyectándolas por reflexión en `ClassLoader.defineClass`. Desde Java 26 eso está bloqueado por defecto, así que todas las tareas `Test` añaden:

```groovy
jvmArgs '--add-opens', 'java.base/java.lang=ALL-UNNAMED'   // permite la reflexión sobre ClassLoader
systemProperty 'net.bytebuddy.safe', 'false'                 // reactiva la vía Unsafe de ByteBuddy
```

Ojo: el mensaje de error de ByteBuddy pide poner `net.bytebuddy.safe` a `true`, pero en Java 26+ su valor por defecto ya es `true` y es `false` lo que funciona (verificado en el bytecode de ByteBuddy 1.18.15). Sin estos dos ajustes todas las pruebas de API fallan con `Could not initialize class RestSpecificationFactory`.

## 3. Patrón Screenplay

Un *Actor* (`Abel`) tiene *Habilidades* (`CallAnApi`), ejecuta *Tareas* (`Task`) y responde *Preguntas* (`Question`).

```
Feature (.feature, español)
   │  Dado / Cuando / Entonces
   ▼
Step Definitions (src/test/.../stepdefinitions)
   │  theActorInTheSpotlight().attemptsTo(...) / should(seeThat(...))
   ▼
Tasks (src/main/.../tasks)  ──►  Interacciones Serenity REST  ──►  RestAssured  ──►  API
   ▲
Questions (src/main/.../questions)  ◄── SerenityRest.lastResponse()  (por hilo)
```

## 4. Estructura

```
src/main/java/co/booker/
├── tasks/
│   ├── CrearToken.java              POST /auth con usuario y contraseña
│   ├── Ping.java                    GET /ping
│   └── booking/
│       ├── CrearBooking.java        POST /booking (body fijo "Jim Brown"); guarda el bookingid
│       ├── EnviarBooking.java       POST /booking con body, Content-Type y Accept arbitrarios (casos negativos)
│       ├── GuardaToken.java         Guarda el "token" de la última respuesta en el actor
│       ├── gets/                    BookingIds, BookingByName, BookingByCheck, BookingByID, BookingEnFormato (Accept)
│       ├── updates/                 UpdateBooking (PUT), PartialUpdateBooking (PATCH): cookie solo si hay token
│       └── deletes/                 DeleteBookingByCookie, DeleteBookingBasicAuth (credenciales configurables)
├── questions/                       CodigoRespuestaHttp, TiempoRespuestaHttp, CampoRespuesta, CabeceraRespuesta, CuerpoRespuesta
├── models/                          Booking, BookingDates (+ User, Location, builders: restos de plantilla, sin uso)
├── utils/
│   ├── endpoints/uris.java          enum AUTH="auth", BOOKING="booking/", PING="ping"
│   ├── Autenticacion.java           conToken (cookie opcional) y basic (cabecera Basic en Base64)
│   ├── Constantes.java              claves TOKEN y BOOKING_ID para actor.remember/recall
│   └── Esperar.java, Builder.java
└── exception/                       ThreadStoppedException, ExceptionErrors

src/test/java/co/booker/
├── runners/CucumberTestSuite.java   Suite única; se filtra por tags
├── stepdefinitions/
│   ├── ComunDefinition.java         @Before, @After (limpieza), actor y validaciones comunes
│   ├── AuthDefinition.java          Autenticarse, guardar token, token inválido
│   ├── BookingDefinition.java       Precondiciones, consultas, CRUD y validaciones de booking
│   └── PingDefinition.java          Health check
└── unitarias/                       EsquemasJsonTest, AutenticacionTest, UtilidadesTest

src/test/resources/
├── features/                        auth.feature, booking.feature, ping.feature
├── *.json                           token, authError, getBookingIds, booking, postBooking (JSON Schema)
└── junit-platform.properties        Glue, plugins, tags por defecto y paralelismo de Cucumber

.github/workflows/ci.yml             Pipeline de CI
```

## 5. Cómo fluye una prueba

Ejemplo: *"Actualizar una reserva completa"*.

1. **`@Before`** (`ComunDefinition.prepararEscenario`): crea el escenario (`OnlineCast`) y lee `restapi.baseurl` (por defecto `https://restful-booker.herokuapp.com/`).
2. **`Dado que Abel quiere usar la API`** (en `Antecedentes`): crea el actor `Abel` con la habilidad `CallAnApi.at(urlBase)`.
3. **`Dado el tiene una reserva creada`**: `CrearBooking` → `POST /booking`; verifica `200` y guarda el `bookingid` (`BOOKING_ID`).
4. **`Y el está autenticado`**: `CrearToken` → `POST /auth` con `admin/password123`; verifica `200` y que hay token; `GuardaToken` lo guarda (`TOKEN`).
5. **`Cuando el actualiza la reserva creada`**: `UpdateBooking` → `PUT /booking/{id}` con la cookie `token`.
6. **`Entonces el código de respuesta debería ser 200`**: `seeThat(CodigoRespuestaHttp.fue(), equalTo(200))`.
7. **`Y la respuesta debería cumplir el esquema "booking.json"`**: `matchesJsonSchemaInClasspath(...)`.
8. **`Y el campo "firstname" de la respuesta debería ser "Juan"`**: `seeThat(CampoRespuesta.en("firstname"), equalTo("Juan"))`.
9. **`@After`** (`ComunDefinition.limpiarReservaCreada`): borra la reserva con RestAssured directo, fuera del reporte.

Como cada escenario crea y borra sus datos, no depende del orden ni de otros escenarios, y la suite corre en paralelo (4 hilos). `SerenityRest.lastResponse()` y el `OnStage` son por hilo, así que las validaciones no se mezclan entre escenarios.

## 6. Qué se valida

| Validación | Cómo | Dónde |
|---|---|---|
| Código HTTP | `CodigoRespuestaHttp` + `equalTo` | Todas las features |
| Estructura del cuerpo | `matchesJsonSchemaInClasspath`, JSON Schema draft-04 con `additionalProperties: false` | Auth, Booking |
| Valores del cuerpo | `CampoRespuesta` (JsonPath) + `equalTo` | Auth (mensaje de error), Booking |
| Cuerpo en texto plano | `CuerpoRespuesta` (`Forbidden`, `Not Found`, XML) | Booking |
| Cabeceras | `CabeceraRespuesta` + `containsString` | Booking (defecto de Content-Type XML) |
| Reserva incluida / lista vacía | `jsonPath().getList("bookingid")` + `hasItem` / `empty` | Booking |
| Efecto de un borrado | GET posterior → `404` (borrado) o `200` (borrado rechazado) | Booking |
| Tiempo de respuesta | `TiempoRespuestaHttp` + `lessThan` | Auth (< 1500 ms), Ping (< 500 ms, `@rendimiento`) |

| Esquema | Respuesta que valida |
|---|---|
| `token.json` | `POST /auth` correcto: `{"token": "..."}` |
| `authError.json` | `POST /auth` con credenciales inválidas: `{"reason": "Bad credentials"}` |
| `getBookingIds.json` | `GET /booking` con o sin filtros: `[{"bookingid": n}, ...]` |
| `booking.json` | Una reserva: `GET /booking/{id}`, `PUT` y `PATCH` |
| `postBooking.json` | `POST /booking`: `{"bookingid": n, "booking": {...}}` |

DELETE responde texto plano (`Created`), así que su efecto se comprueba con un GET posterior.

### Comportamiento real de la API (verificado)

| Llamada | Respuesta |
|---|---|
| Credenciales inválidas en `/auth` | `200` con `{"reason": "Bad credentials"}` |
| PUT/PATCH/DELETE sin token, con token inválido o Basic incorrecto | `403 Forbidden` |
| GET de id inexistente o no numérico | `404 Not Found` |
| POST con JSON malformado | `400 Bad Request` |
| `Accept: text/plain` | `418 I'm a Teapot` |
| DELETE correcto / repetido | `201 Created` / `405 Method Not Allowed` |
| `GET /ping` | `201 Created` |

### Defectos conocidos de la API (`@defectoConocido`)

Escenarios que describen el comportamiento **correcto** y hoy fallan. Excluidos por defecto; se ejecutan con `./gradlew test -Ptags="@defectoConocido"`.

| Caso | Esperado | Real |
|---|---|---|
| POST sin campos obligatorios | 400 | 500 |
| POST con checkout anterior al checkin | 400 | 200 (la acepta) |
| POST con `totalprice` no numérico | 400 | 200 (guarda `null`) |
| POST con `Content-Type: text/plain` | 415 | 500 |
| Respuesta XML | `Content-Type` XML | `text/html` |

## 7. Configuración, tags y CI

- **`serenity.properties`**: nombre del proyecto en reportes y `restapi.baseurl`. Contiene configuración de WebDriver heredada que no se usa.
- **`junit-platform.properties`**: glue, plugins (`SerenityReporterParallel` es necesario para el reporte), filtro por defecto `not @defectoConocido and not @rendimiento` y paralelismo fijo de 4 hilos.
- **`build.gradle`**:
  - `test`: solo el motor de suites (Cucumber); `-Ptags` se combina con las exclusiones por defecto; borra el reporte anterior (`clearReports`) y lo regenera siempre (`aggregate`).
  - `unitTest`: solo JUnit Jupiter, sin red.
  - `validarFeatures`: dry-run de Cucumber sobre los 38 escenarios; falla si algún paso no está implementado.
  - `check` depende de las tres.
- **Tags**: `@auth`/`@booking`/`@ping` (módulo), `@smoke`, `@negativo`, `@defectoConocido`, `@rendimiento`.
- **CI** (`.github/workflows/ci.yml`): push a `main`, pull requests, diario a las 06:00 UTC y manual (con tags opcionales). Job `verificacion` (unitarias, `validarFeatures`, Javadoc) y, si pasa, job `api`. Publica el reporte de Serenity y los resultados JUnit como artefactos.
- **Credenciales**: `admin` / `password123` son las públicas de Restful Booker; el Basic se genera en tiempo de ejecución (`Autenticacion.basic`).

## 8. Estado y pendientes

| Suite | Pruebas | Resultado |
|---|---|---|
| API (por defecto) | 32 | ✅ estables en ejecuciones repetidas, también con 8 hilos |
| `@defectoConocido` | 5 | ❌ a propósito |
| `@rendimiento` | 1 | ⚠️ depende de la latencia (p. ej. 532 ms con umbral de 500) |
| Unitarias | 31 | ✅ |
| `validarFeatures` | 38 escenarios | ✅ |

Se verificó que las validaciones fallan cuando deben (esquema o valor incorrecto, esquema relajado, paso sin implementar → `BUILD FAILED`).

| # | Pendiente | Efecto | Ubicación |
|---|---|---|---|
| 1 | Bodies JSON construidos como `String` concatenado | Los modelos `Booking`/`BookingDates` existen pero no se usan | `tasks/` |
| 2 | Cabeceras repetidas en cada tarea (`Content-Type`, `relaxedHTTPSValidation`) | Duplicación; convendría una `RequestSpecification` base | `tasks/` |
| 3 | Algunas descripciones `@Step` copiadas (p. ej. "Obtener libro por Nombre" en el filtro por fechas) | Reportes Serenity engañosos | `tasks/` |
| 4 | Credenciales en código y features | `admin`/`password123` en `.feature` y en `DeleteBookingBasicAuth`/`ComunDefinition` | `tasks/`, `stepdefinitions/`, `features/` |
| 5 | `prettyPrint()` en tareas y clases sin uso de la plantilla | Ruido en consola; código muerto (`User`, `Location`, builders, `ExceptionErrors`) | `tasks/`, `models/`, `exception/` |
| 6 | Sin análisis Sonar en CI | Requiere un servidor y `SONAR_TOKEN` | `.github/workflows/ci.yml` |

## 9. Próximos pasos sugeridos

1. Construir los bodies con los modelos y una `RequestSpecification` base (cabeceras comunes una sola vez).
2. Corregir las descripciones `@Step` restantes y quitar `prettyPrint()`.
3. Credenciales a `serenity.conf`/variables de entorno (secretos en CI).
4. Eliminar las clases de la plantilla sin uso y la configuración WebDriver de `serenity.properties`.
5. Añadir Sonar al CI cuando haya servidor disponible.
6. Retirar `--add-opens` y `net.bytebuddy.safe=false` cuando Serenity deje de inyectar clases por reflexión.
