# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

An API **test-automation** suite (not an API) against the public Restful Booker API (`https://restful-booker.herokuapp.com/`), built with Serenity BDD + Screenplay + Cucumber + RestAssured, plus JUnit Jupiter unit tests and a GitHub Actions pipeline. API tests hit the live API, so they need network access. Project language is Spanish: feature files, step text, class names, Javadoc, README and `docs/CONTEXTO.md` (full architecture + pending-work reference — keep it in sync when behavior changes).

## Commands

```bash
docker run -d -p 3001:3001 mwinteringham/restfulbooker   # local API (amd64 image; emulated on ARM Macs)
./gradlew test -Penv=docker                     # API tests against the container (fast, isolated)
./gradlew test                                  # API tests against the public API (default env)
./gradlew test -Ptags="@smoke"                  # filter by Cucumber tag expression
./gradlew test -Ptags="@booking and @negativo"
./gradlew unitTest                              # JUnit Jupiter unit tests, no network
./gradlew unitTest --tests '*EsquemasJsonTest'  # single unit test class
./gradlew validarFeatures                       # Cucumber dry-run: fails if any step is undefined (no network)
./gradlew check                                 # unitTest + validarFeatures + test
./gradlew javadoc                               # must stay warning-free
./gradlew sonar                                 # static analysis
```

- `-Penv` selects the `environments` block of `src/test/resources/serenity.conf` (`default`/`publico` = public Heroku API, `docker` = `localhost:3001`). The container behaves identically to the public API (verified on every case the suite covers).
- To run one scenario, give it a tag and use `-Ptags`. `-Ptags` still excludes `@defectoConocido` and `@rendimiento` unless the expression mentions them (logic in `build.gradle`); the default filter lives in `junit-platform.properties`.
- A failing test fails the build. `test` depends on `clearReports` and is finalized by `aggregate` (forced to rerun), so `target/site/serenity/index.html` always reflects only the latest run.
- Engines are separated: `test` and `validarFeatures` include only `junit-platform-suite` (Cucumber), `unitTest` only `junit-jupiter`.

## Versions and Java 27 compatibility

Java 27 toolchain, Serenity 5.4.2, Cucumber 8.1.0, RestAssured 6.3.0, JUnit 6.1.3. Library versions live in the `ext` block of `build.gradle`; keep Serenity artifacts and the Serenity Gradle plugin on the same version. Serenity 4+ moved packages: `@Step` is `net.serenitybdd.annotations.Step`, `EnvironmentVariables` is `net.thucydides.model.util.EnvironmentVariables`.

Serenity REST injects generated classes via ByteBuddy reflection, which Java 26+ blocks. All `Test` tasks set `--add-opens java.base/java.lang=ALL-UNNAMED` **and** `net.bytebuddy.safe=false`. Without them every API test fails with `Could not initialize class RestSpecificationFactory`. ByteBuddy's error message says to set `net.bytebuddy.safe` to `true`; that is wrong on Java 26+ (default is already `true`), `false` is what works.

## Configuration gotchas

- `serenity.conf` must live in `src/test/resources` (Serenity doesn't load it from the project root). Read it through `co.booker.utils.Configuracion` (`urlBase()`, `usuario()`, `clave()`), never with hardcoded values.
- Serenity does **not** resolve HOCON `${?VAR}` substitutions inside `environments` (it returns the raw "unresolved merge" text). Env-var overrides (`RESTFUL_BOOKER_USUARIO`, `RESTFUL_BOOKER_CLAVE`) are therefore resolved in Java in `Configuracion`.

## Architecture (Screenplay)

Flow: `.feature` (Gherkin `# language: es`) → step definitions (`src/test/java/co/booker/stepdefinitions`) → `theActorInTheSpotlight().attemptsTo(Task)` → Task in `src/main/java/co/booker/tasks` issues a Serenity REST interaction → assertions via `should(seeThat(Question, matcher))`.

- **Single runner**: `co.booker.runners.CucumberTestSuite` (`@Suite`, `@IncludeEngines("cucumber")`, `@SelectClasspathResource("features")`). Glue, plugins (`SerenityReporterParallel` is required for Serenity reports), default tag filter and parallelism (4 threads) are in `src/test/resources/junit-platform.properties`. Don't add per-feature runners or JUnit 4 `CucumberWithSerenity` runners (deprecated for removal).
- **HTTP client setup is global** (`ClienteApi.configurar()`, called from `@BeforeAll`): a default request spec with `Content-Type`/`Accept` `application/json` and relaxed HTTPS, plus the `ReintentoInfraestructura` filter. Tasks must not repeat these headers; they only add what differs (cookie, body, another `Accept`). Use the literal `"application/json"`: `ContentType.JSON` sends a multi-value `Accept` and Restful Booker answers **418** to anything but an exact match.
- **`ReintentoInfraestructura`** retries only idempotent methods (never POST) on 502/503/504 or I/O errors, 3 attempts with exponential backoff. A RestAssured `FilterContext` can only be walked once (`next()` returns `null` the second time), so retries go through `ctx.send(RestAssured.given().spec(req).noFilters())`. `ReintentoInfraestructuraTest` covers this against a local `HttpServer`.
- **`ComunDefinition`** owns the hooks and every shared step: `@BeforeAll` configures the client and health-checks `GET /ping` (3 attempts), failing the whole suite with an actionable message if the API is down (catch `Exception`, not `RuntimeException`: RestAssured sneaky-throws checked `ConnectException`); `@Before` creates the `OnlineCast`; `Dado que Abel quiere usar la API` (in each feature's `Antecedentes`); response checks (`el código de respuesta debería ser`, `la respuesta debería cumplir el esquema`, `el campo ... debería ser`, `la cabecera ... debería contener`, `el cuerpo de la respuesta debería ser/contener`, `el tiempo de respuesta debería ser menor a ... ms`). Reuse these; put module-specific steps in `AuthDefinition`/`BookingDefinition`/`PingDefinition`.
- **Questions read `SerenityRest.lastResponse()`**, which is per thread, so assertions apply to the last request of the current scenario. `CampoRespuesta` (JsonPath → String), `CabeceraRespuesta`, `CuerpoRespuesta`, `CodigoRespuestaHttp`, `TiempoRespuestaHttp`.
- **Auth**: `Dado el está autenticado` gets a token for `admin/password123`, asserts it, and stores it under `Constantes.TOKEN`; `Dado el tiene un token inválido` stores a bogus one. Update/delete-by-cookie tasks add the cookie via `Autenticacion.conToken`, **only when a token exists**, so "no token" scenarios really send none. Basic Auth headers are built with `Autenticacion.basic(user, pass)`; RestAssured's `auth().preemptive().basic()` throws `ClassCastException` on Serenity's decorated request spec.
- **Tasks are created with `Tasks.instrumented(Class, args...)`**: args must match a public constructor exactly. Request bodies are `Booking`/`BookingDates` **records** (field names = API JSON names) serialized with `co.booker.utils.Json` (Gson, nulls omitted, so a partial `Booking` is a valid PATCH body). `EnviarBooking` posts an arbitrary raw body (with custom `Content-Type`/`Accept`) for negative cases; feature files pass it as a DocString. Cucumber 8 treats any `"""` block after a step as its DocString, so use `#` for comments.

## Test data and validation conventions

- **Scenarios create their own data via `ReservaFactory`; never hardcode IDs, names or dates.** `valida()` gives a unique lastname (`Prueba-<uuid8>`) and future dates, `actualizada()` changes every field, `cambioDeNombre()` is a partial. `Dado el tiene una reserva creada` runs `CrearBooking`, asserts 200 and stores `BOOKING_ID` and `RESERVA_CREADA`; create/update tasks also store `RESERVA_ENVIADA`. Assert with whole-object steps (`la respuesta debería coincidir con la reserva creada/enviada`, `...tener los cambios enviados sobre la reserva creada` using `Booking.con()`), not per-field literals. The `@After` hook in `ComunDefinition` deletes that booking with plain RestAssured (outside the Serenity report), ignoring errors. This is what makes parallel execution safe.
- **`src/test/resources/*.json` are JSON Schema draft-04** (what RestAssured's validator supports) with `additionalProperties: false`; shared sub-schemas are inlined under `definitions`. `EsquemasJsonTest` checks each schema accepts real API payloads and rejects broken ones; add cases there when changing a schema.
- **Known API defects** (intentional in Restful Booker) are scenarios tagged `@defectoConocido` that assert the *correct* behavior and therefore fail today: missing fields → 500, checkout before checkin accepted, non-numeric price stored as null, wrong `Content-Type` → 500, XML served as `text/html`. Don't "fix" them by asserting the buggy behavior.
- **Filters**: name filter is exact and case-sensitive (unique lastname → exactly one result); date filters are **strict** (`checkin` = enters after, `checkout` = leaves before), so querying a booking's own dates returns nothing.
- **Live API facts** (verified): valid credentials `admin/password123`; invalid credentials return **200** with `{"reason":"Bad credentials"}`; PUT/PATCH/DELETE without a valid token or Basic → 403 `Forbidden`; unknown or non-numeric id → 404 `Not Found`; malformed JSON → 400; `Accept: text/plain` → 418; DELETE → 201 plain-text `Created`; second DELETE → 405; `GET /ping` → 201.

## Unit tests

`src/test/java/co/booker/unitarias` (JUnit Jupiter, no network): JSON schemas, `ReservaFactory`/`Booking`/`Json` (factory output validated against `booking.json`), `ReintentoInfraestructura` (local `HttpServer`), `Configuracion`, `Autenticacion`, `uris`/`Constantes`. Tasks and Questions depend on a live `SerenityRest.lastResponse()` and are covered by the API suite, not unit tests.
