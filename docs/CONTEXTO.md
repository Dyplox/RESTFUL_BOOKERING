# Contexto del proyecto — RESTFUL_BOOKERING

Documento de referencia técnica: qué es el proyecto, cómo está construido, cómo fluye una prueba y qué problemas conocidos tiene. Para instalar y ejecutar, ver el [README](../README.md).

---

## 1. Qué es

Suite de **pruebas automatizadas de API** (no una API) contra [Restful Booker](https://restful-booker.herokuapp.com/), una API pública de reservas de hotel diseñada para practicar testing. La API se reinicia periódicamente y sus datos cambian, lo que afecta a las pruebas que usan IDs fijos (ver §8).

Cubre tres módulos de la API:

| Módulo | Endpoint | Qué se prueba |
|---|---|---|
| Auth | `POST /auth` | Generación de token con credenciales válidas e inválidas, tiempo de respuesta |
| Booking | `GET/POST/PUT/PATCH/DELETE /booking` | CRUD completo, filtros por nombre y fechas, borrado por cookie y por Basic Auth |
| Ping | `GET /ping` | Health check y tiempo de respuesta |

## 2. Stack técnico

| Pieza | Versión | Rol |
|---|---|---|
| Java | 27 (toolchain Gradle) | Lenguaje |
| Gradle | 9.9.1 (wrapper) | Build y ejecución |
| Serenity BDD | 3.3.9-RC3 | Framework de pruebas + reportes HTML |
| Serenity Screenplay REST | 3.3.9-RC3 | Interacciones HTTP (`Get`, `Post`, `Put`, `Patch`, `Delete`) |
| RestAssured | 5.3.0 | Motor HTTP bajo Serenity (`SerenityRest`) |
| json-schema-validator | 5.3.0 | Validación de cuerpo contra esquema |
| Cucumber | 4.2.0 | Escenarios BDD en español (`# language: es`) |
| Hamcrest | 2.2 | Matchers de aserción |
| SonarQube plugin | 3.3 | Análisis estático (`gradle sonarqube`) |

> El `pom.xml` es un resto de una plantilla Maven; el build real es **Gradle**.

### Ajustes necesarios para Java 27

Serenity 3.x usa ByteBuddy 1.12, anterior a Java 27. En `build.gradle` la tarea `test` añade:

```groovy
jvmArgs '--add-opens', 'java.base/java.lang=ALL-UNNAMED'   // ByteBuddy define clases por reflexión
systemProperty 'net.bytebuddy.experimental', 'true'         // acepta bytecode de Java 27
```

Es un parche; la solución de fondo es actualizar Serenity a una versión reciente.

## 3. Patrón Screenplay

El proyecto sigue **Screenplay**: un *Actor* (`Abel`) tiene *Habilidades* (`CallAnApi`), ejecuta *Tareas* (`Task`) y responde *Preguntas* (`Question`).

```
Feature (.feature, español)
   │  Dado / Cuando / Entonces
   ▼
Step Definitions (src/test/.../stepdefinitions)
   │  theActorInTheSpotlight().attemptsTo(...) / should(seeThat(...))
   ▼
Tasks (src/main/.../tasks)  ──►  Interacciones Serenity REST  ──►  RestAssured  ──►  API
   ▲
Questions (src/main/.../questions)  ◄── SerenityRest.lastResponse()
```

## 4. Estructura

```
src/main/java/co/booker/
├── tasks/
│   ├── CrearToken.java            POST /auth con usuario y contraseña
│   ├── Ping.java                  GET /ping
│   └── booking/
│       ├── CrearBooking.java      POST /booking (body fijo "Jim Brown")
│       ├── GuardaToken.java       Lee "token" de la última respuesta y lo guarda en la memoria del actor
│       ├── gets/                  BookingIds, BookingByName, BookingByCheck, BookingByID
│       ├── updates/               UpdateBooking (PUT), PartialUpdateBooking (PATCH) — usan cookie token
│       └── deletes/               DeleteBookingByCookie, DeleteBookingBasicAuth
├── questions/
│   ├── CodigoRespuestaHttp.java   status code de la última respuesta
│   └── TiempoRespuestaHttp.java   tiempo de respuesta en ms
├── models/                        Booking, BookingDates (+ User, Location, builders: restos de plantilla, sin uso)
├── utils/
│   ├── endpoints/uris.java        enum AUTH="auth", BOOKING="booking/", PING="ping"
│   ├── Constantes.java            clave TOKEN para actor.remember/recall
│   └── Esperar.java, Builder.java
└── exception/                     ThreadStoppedException, ExceptionErrors

src/test/java/co/booker/
├── runners/                       AuthRunner, BookingRunner, PingRunner (un runner por feature)
└── stepdefinitions/               AuthDefinition (incluye @Before y pasos comunes), BookingDefinition, PingDefinition

src/test/resources/
├── features/                      auth.feature, booking.feature, ping.feature
└── *.json                         token, getBookingIds, getBookingID, postBooking, deleteBooking
```

## 5. Cómo fluye una prueba

Ejemplo: *"Verificar si la actualizacion de bookings es valida"*.

1. **`@Before`** (`AuthDefinition.setTheStage`): crea el escenario (`OnlineCast`) y lee `restapi.baseurl` de `serenity.properties` (por defecto `https://restful-booker.herokuapp.com/`).
2. **`Dado que Abel quiere logueaser en la Api`**: crea el actor `Abel` con la habilidad `CallAnApi.at(baseUrl)`.
3. **`Cuando el ingresa "admin" and "password123"`**: tarea `CrearToken` → `POST /auth`.
4. **`Y el Guarda el token`**: tarea `GuardaToken` → `actor.remember(TOKEN, token)`.
5. **`Cuando el llama el servicio para actualizar el bookin 1212`**: tarea `UpdateBooking` → `actor.recall(TOKEN)` y `PUT /booking/1212` con cabecera `Cookie: token=...`.
6. **`Entonces el verifica que el codigo HTTP sea 200`**: `seeThat(CodigoRespuestaHttp.fue(), equalTo(200))`.
7. **`Entonces el deberia recibir el token y validar el esquema "postBooking.json"`**: `matchesJsonSchemaInClasspath(...)` sobre el cuerpo.

Los pasos de validación (`verifica que el codigo HTTP`, `validar el esquema`, `tiempo menor a`) son compartidos entre features.

## 6. Qué se valida (y qué no)

| Validación | Cómo | Dónde |
|---|---|---|
| Código HTTP | `CodigoRespuestaHttp` + `equalTo` | Todas las features |
| Estructura del cuerpo | `matchesJsonSchemaInClasspath` | Auth, Booking |
| Tiempo de respuesta | `TiempoRespuestaHttp` + `lessThan` | Auth (< 1500 ms), Ping (< 500 ms) |
| **Valores del cuerpo** | **No se valida** | — |

## 7. Configuración

- **`serenity.properties`**: nombre del proyecto en reportes, `restapi.baseurl`, codificación. Contiene configuración de WebDriver heredada que no se usa.
- **`build.gradle`**: `ignoreFailures = true` (el build termina en éxito aunque fallen pruebas) y `test.finalizedBy(aggregate)` (genera el reporte siempre).
- **Credenciales**: `admin` / `password123` son las públicas de Restful Booker; `DeleteBookingBasicAuth` las tiene en Base64 en el código.

## 8. Problemas conocidos

Estado al migrar a Java 27: **10 pruebas, 2 pasan, 8 fallan** — ninguna por la versión de Java.

| # | Problema | Efecto | Ubicación |
|---|---|---|---|
| 1 | Los `.json` de recursos son **ejemplos de respuesta, no JSON Schema** | Los de tipo objeto validan cualquier cosa (sus claves no son palabras de esquema); `getBookingIds.json` (array) lanza `invalid JSON Schema` | `src/test/resources/*.json` |
| 2 | `GuardaToken` solo tiene constructor con `String`, pero se instancia con `instrumented(GuardaToken.class)` sin argumentos | `NullPointerException`; rompe todo escenario con "Guarda el token" | `tasks/booking/GuardaToken.java` |
| 3 | IDs fijos (11, 1212, 1214) | Fallos intermitentes: la API pública reinicia sus datos | `booking.feature` |
| 4 | Escenarios de login inválido validan contra `token.json` | La API responde 200 con `{"reason":"Bad credentials"}`; la intención del caso no se comprueba | `auth.feature` |
| 5 | Línea en blanco dentro de la tabla `Ejemplos` | Puede romper el parseo de Cucumber | `auth.feature` |
| 6 | Umbral de Ping de 500 ms | Falla según la latencia de red (p. ej. 569 ms) | `ping.feature` |
| 7 | Dos escenarios DELETE con el mismo título | Reportes confusos | `booking.feature` |
| 8 | Bodies JSON construidos como `String` concatenado | Los modelos `Booking`/`BookingDates` existen pero no se usan | `tasks/` |
| 9 | Descripciones `@Step` copiadas (p. ej. "Obtener libro por Nombre" en filtro por fechas) | Reportes Serenity engañosos | `tasks/` |
| 10 | Mezcla de versiones de Cucumber (4.2.0 / core 2.0.0) y JUnit 4/5 | Riesgo de conflictos de classpath | `build.gradle` |
| 11 | `gradlew` sin permiso de ejecución | Hay que lanzar `sh ./gradlew` | raíz |

## 9. Próximos pasos sugeridos

1. Arreglar `GuardaToken` (constructor sin argumentos).
2. Reemplazar los `.json` por JSON Schema reales (`type`, `properties`, `required`).
3. Crear el booking dentro del escenario y reutilizar su `bookingid` en GET/PUT/PATCH/DELETE.
4. Añadir Questions que validen valores del cuerpo (`jsonPath`).
5. Actualizar Serenity a una versión reciente y retirar los ajustes de `--add-opens`/ByteBuddy experimental.
6. Quitar `ignoreFailures = true` para que CI detecte fallos; eliminar `pom.xml` y configuración WebDriver sobrante.
