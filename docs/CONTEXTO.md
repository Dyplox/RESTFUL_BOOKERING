# Contexto del proyecto — RESTFUL_BOOKERING

Documento de referencia técnica: qué es el proyecto, cómo está construido, cómo fluye una prueba y qué queda pendiente. Para instalar y ejecutar, ver el [README](../README.md).

---

## 1. Qué es

Suite de **pruebas automatizadas de API** (no una API) contra [Restful Booker](https://restful-booker.herokuapp.com/), una API de reservas de hotel diseñada para practicar testing, con errores introducidos a propósito. Se puede ejecutar contra la API pública (compartida, con latencia y caídas puntuales) o contra un **contenedor Docker local** (`mwinteringham/restfulbooker`). El contenedor se comporta igual en todos los casos que cubre la suite: se verificó comparando 16 llamadas lado a lado. Cada escenario crea y elimina sus propios datos.

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
| Serenity Ensure | 5.4.2 | Aserciones en los step definitions, visibles en el reporte |
| Hamcrest | 3.0 | Matchers en pruebas unitarias |
| SonarQube plugin | 7.6.0 | Análisis estático (`./gradlew sonar`) |
| Gson | 2.15.0 | Serialización de los modelos (`co.booker.utils.Json`) |
| Docker (`mwinteringham/restfulbooker`) | latest | API local para desarrollo y CI |
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
   │  theActorInTheSpotlight().attemptsTo(Task...) / attemptsTo(Ensure.that(Question)...)
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
│       ├── CrearBooking.java        POST /booking con una Booking; guarda bookingid, reserva creada y enviada
│       ├── EnviarBooking.java       POST /booking con body, Content-Type y Accept arbitrarios (negativos y valores límite)
│       ├── GuardaToken.java         Guarda el "token" de la última respuesta en el actor
│       ├── gets/                    BookingIds, BookingByName, BookingByCheck, BookingByID, BookingEnFormato (Accept)
│       ├── updates/                 UpdateBooking (PUT), PartialUpdateBooking (PATCH): Booking como body; cookie solo si hay token
│       └── deletes/                 DeleteBookingByCookie, DeleteBookingBasicAuth (credenciales configurables)
├── questions/                       CodigoRespuestaHttp, TiempoRespuestaHttp, CampoRespuesta, CabeceraRespuesta, CuerpoRespuesta,
│                                    ErroresDeEsquema (errores con la ruta del campo), ReservaRespuesta (JSON canónico), IdsDeReservas
├── models/
│   ├── Booking.java, BookingDates.java   records con los nombres de campo del JSON; Booking.con() aplica un PATCH
│   └── ReservaFactory.java          valida() (apellido único y fechas futuras), actualizada(), cambioDeNombre()
└── utils/
    ├── Configuracion.java           URL base y credenciales del entorno activo (serenity.conf + variables de entorno)
    ├── ClienteApi.java              Especificación base (JSON, HTTPS relajado) y filtro de reintentos, una sola vez
    ├── ReintentoInfraestructura.java Reintenta 502/503/504 y errores de E/S, solo en métodos idempotentes
    ├── Json.java                    Gson: aJson (omite nulos) y desde
    ├── Autenticacion.java           conToken (cookie opcional) y basic (cabecera Basic en Base64)
    ├── Constantes.java              claves TOKEN, BOOKING_ID, RESERVA_CREADA, RESERVA_ENVIADA
    └── endpoints/uris.java          enum AUTH="auth", BOOKING="booking/", PING="ping"

src/test/java/co/booker/
├── runners/CucumberTestSuite.java   Suite única; se filtra por tags
├── stepdefinitions/
│   ├── ComunDefinition.java         @BeforeAll (cliente + health check), @Before, @After (limpieza), actor y validaciones
│   ├── AuthDefinition.java          Autenticarse, guardar token, token inválido
│   ├── BookingDefinition.java       Precondiciones, consultas, CRUD y validaciones de booking
│   └── PingDefinition.java          Health check
└── unitarias/                       Esquemas, ReservaFactory, ReintentoInfraestructura, Configuracion, Autenticacion, utilidades

src/test/resources/
├── features/                        auth.feature, booking.feature, ping.feature
├── *.json                           token, authError, getBookingIds, booking, postBooking (JSON Schema)
├── serenity.conf                    Entornos (default/publico/docker) y credenciales
└── junit-platform.properties        Glue, plugins, tags por defecto y paralelismo de Cucumber

.github/workflows/ci.yml             Pipeline de CI
```

## 5. Cómo fluye una prueba

Ejemplo: *"Actualizar una reserva completa"*.

0. **`@BeforeAll`** (una vez por ejecución): `ClienteApi.configurar()` y health check de `GET /ping`; si la API no responde, la suite se detiene con un mensaje claro.
1. **`@Before`**: crea el escenario (`OnlineCast`).
2. **`Dado que Abel quiere usar la API`** (en `Antecedentes`): crea el actor `Abel` con `CallAnApi.at(Configuracion.urlBase())`.
3. **`Dado el tiene una reserva creada`**: `CrearBooking.valida()` → `POST /booking` con datos únicos de `ReservaFactory`; verifica `200` y guarda `BOOKING_ID` y `RESERVA_CREADA`.
4. **`Y el está autenticado`**: `CrearToken` con las credenciales de `Configuracion`; verifica el token y `GuardaToken` lo guarda (`TOKEN`).
5. **`Cuando el actualiza la reserva creada`**: `UpdateBooking` envía `ReservaFactory.actualizada()` (guardada como `RESERVA_ENVIADA`) con la cookie `token`.
6. **`Entonces el código de respuesta debería ser 200`**.
7. **`Y la respuesta debería cumplir el esquema "booking.json"`**.
8. **`Y la respuesta debería coincidir con la reserva enviada`**: deserializa la respuesta a `Booking` y la compara entera con la enviada.
9. **`Y al consultarla de nuevo debería coincidir con la reserva enviada`**: GET posterior para comprobar que el cambio persistió.
10. **`@After`**: borra la reserva con RestAssured directo, fuera del reporte.

Como cada escenario crea y borra sus datos, no depende del orden ni de otros escenarios, y la suite corre en paralelo (4 hilos). `SerenityRest.lastResponse()` y el `OnStage` son por hilo, así que las validaciones no se mezclan entre escenarios.

## 6. Qué se valida

| Validación | Cómo | Dónde |
|---|---|---|
| Código HTTP | `CodigoRespuestaHttp` + `equalTo` | Todas las features |
| Estructura del cuerpo | `matchesJsonSchemaInClasspath`, JSON Schema draft-04 con `additionalProperties: false` | Auth, Booking |
| Reserva completa | Respuesta deserializada a `Booking` y comparada con la creada, la enviada o la creada con los cambios del PATCH (`Booking.con`) | Booking (GET, POST, PUT, PATCH, persistencia tras PUT) |
| Valores del cuerpo | `CampoRespuesta` (JsonPath) + `equalTo` | Auth (mensaje de error) |
| Cuerpo en texto plano | `CuerpoRespuesta` (`Forbidden`, `Not Found`, XML) | Booking |
| Cabeceras | `CabeceraRespuesta` + `containsString` | Booking (defecto de Content-Type XML) |
| Filtros | Lista de ids: contiene **solo** la reserva (nombre único), la contiene / no la contiene (fechas estrictas), vacía | Booking |
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
| Filtro por nombre | Coincidencia exacta, distingue mayúsculas |
| Filtros por fecha | Estrictos: `checkin` = entra **después**; `checkout` = sale **antes** |
| `Accept` distinto de `application/json` exacto | `418` (incluido el `Accept` múltiple de `ContentType.JSON` de RestAssured) |

### Defectos conocidos de la API (`@defectoConocido`)

Escenarios que describen el comportamiento **correcto** y hoy fallan. Excluidos por defecto; se ejecutan con `./gradlew test -Ptags="@defectoConocido"`.

| Caso | Esperado | Real |
|---|---|---|
| POST sin campos obligatorios | 400 | 500 |
| POST con checkout anterior al checkin | 400 | 200 (la acepta) |
| POST con `totalprice` no numérico | 400 | 200 (guarda `null`) |
| POST con `Content-Type: text/plain` | 415 | 500 |
| Respuesta XML | `Content-Type` XML | `text/html` |
| Precio negativo | 400 | 200 (lo acepta) |
| Precio con decimales (`99.99`) | 400 | 200 (lo trunca a `99`) |
| Nombre vacío | 400 | 200 (lo acepta) |
| Nombre numérico | 400 | 500 |
| `depositpaid: "yes"` | 400 | 200 (lo convierte en `true`) |
| Fecha `2031-02-30` | 400 | 200 (la cambia a `2031-03-02`) |
| Fecha `2031-13-01` o `"mañana"` | 400 | 200 (guarda `"0NaN-aN-aN"`) |

El job `vigilancia` del CI los ejecuta a diario y falla si alguno empieza a pasar (la API lo habría corregido).

## 7. Configuración, tags y CI

- **`src/test/resources/serenity.conf`**: nombre del proyecto y entornos `default`/`publico`/`docker` (`-Penv`). Credenciales comunes en `all`; las variables de entorno `RESTFUL_BOOKER_USUARIO`/`RESTFUL_BOOKER_CLAVE` tienen prioridad. Las resuelve `Configuracion` en Java, porque Serenity no resuelve `${?VAR}` dentro de `environments`.
- **`junit-platform.properties`**: glue, plugins (`SerenityReporterParallel` es necesario para el reporte), filtro por defecto `not @defectoConocido and not @rendimiento` y paralelismo fijo de 4 hilos.
- **`build.gradle`**:
  - `test`: solo el motor de suites (Cucumber); `-Penv` elige el entorno; `-Ptags` se combina con las exclusiones por defecto; borra el reporte anterior (`clearReports`) y lo regenera siempre (`aggregate`).
  - `unitTest`: solo JUnit Jupiter, sin red.
  - `validarFeatures`: dry-run de Cucumber sobre los 53 escenarios (no ejecuta hooks ni necesita red); falla si algún paso no está implementado.
  - `check` depende de las tres.
- **Tags**: `@auth`/`@booking`/`@ping` (módulo), `@smoke`, `@negativo`, `@defectoConocido`, `@rendimiento`.
- **CI** (`.github/workflows/ci.yml`): se lanza en push a `main`, en pull requests, a diario a las 06:00 UTC y a mano (con entorno y tags). Primero corre el job `verificacion` (unitarias, `validarFeatures`, Javadoc). Si pasa, corre el job `api`:
  - En push y PR, contra el contenedor como *service*.
  - En la ejecución diaria, contra la API pública.

  Publica el reporte de Serenity y los resultados JUnit como artefactos.

## 8. Estado y pendientes

| Suite | Pruebas | Resultado |
|---|---|---|
| API (por defecto) | 39 | ✅ en Docker (~3 s) y en la API pública (~11 s); estables en ejecuciones repetidas y en paralelo |
| `@defectoConocido` | 13 | ❌ a propósito, igual en ambos entornos |
| `@rendimiento` | 1 | ⚠️ depende de la latencia |
| Unitarias | 48 | ✅ |
| `validarFeatures` | 53 escenarios | ✅ |

Se verificó que las validaciones fallan cuando deben:
- Esquema relajado o valor incorrecto.
- PUT que envía datos distintos de los esperados.
- Paso sin implementar.
- API caída: la comprobación previa la detiene con un mensaje claro.

| # | Pendiente | Efecto |
|---|---|---|
| 1 | El CI nunca se ha ejecutado en GitHub | Verificado en local (actionlint y esquemas oficiales de workflow y Dependabot), pero falta el primer run real |
| 2 | GitHub Pages sin activar | Hay que elegir una vez *Settings → Pages → Source: GitHub Actions* para que se publique el reporte |
| 3 | Los POST no se reintentan | A propósito: reintentar un POST puede duplicar datos |
| 4 | Sin análisis Sonar en CI | Requiere un servidor y `SONAR_TOKEN` |
| 5 | Nombres mezclados en inglés y español | `CrearBooking`/`GuardaToken` frente a `BookingByID`/`DeleteBookingByCookie`; factorías `usuario()`/`delServicio()` sin criterio común |

## 9. Próximos pasos sugeridos

1. Subir la rama y comprobar el primer run del CI; activar GitHub Pages.
2. Unificar los nombres de tareas y factorías.
3. Concurrencia (dos PUT simultáneos) y casos de tokens (reutilización, cookie mal formada).
4. Formato y análisis estático automáticos (Spotless, Checkstyle o Error Prone) y Sonar cuando haya servidor.
5. Retirar `--add-opens` y `net.bytebuddy.safe=false` cuando Serenity deje de inyectar clases por reflexión.
