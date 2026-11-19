# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

An API **test-automation** suite (not an API) against the public Restful Booker API (`https://restful-booker.herokuapp.com/`), built with Serenity BDD + Screenplay + Cucumber + RestAssured, plus JUnit Jupiter unit tests and a GitHub Actions pipeline. API tests hit the live API, so they need network access. Project language is Spanish: feature files, step text, class names, Javadoc, README and `docs/CONTEXTO.md` (full architecture + pending-work reference — keep it in sync when behavior changes).

## Commands

```bash
./gradlew test                                  # API tests (all except @defectoConocido and @rendimiento) + Serenity report
./gradlew test -Ptags="@smoke"                  # filter by Cucumber tag expression
./gradlew test -Ptags="@booking and @negativo"
./gradlew unitTest                              # JUnit Jupiter unit tests, no network
./gradlew unitTest --tests '*EsquemasJsonTest'  # single unit test class
./gradlew validarFeatures                       # Cucumber dry-run: fails if any step is undefined (no network)
./gradlew check                                 # unitTest + validarFeatures + test
./gradlew javadoc                               # must stay warning-free
./gradlew sonar                                 # static analysis
```

- To run one scenario, give it a tag and use `-Ptags`. `-Ptags` still excludes `@defectoConocido` and `@rendimiento` unless the expression mentions them (logic in `build.gradle`); the default filter lives in `junit-platform.properties`.
- A failing test fails the build. `test` depends on `clearReports` and is finalized by `aggregate` (forced to rerun), so `target/site/serenity/index.html` always reflects only the latest run.
- Engines are separated: `test` and `validarFeatures` include only `junit-platform-suite` (Cucumber), `unitTest` only `junit-jupiter`.

## Versions and Java 27 compatibility

Java 27 toolchain, Serenity 5.4.2, Cucumber 8.1.0, RestAssured 6.3.0, JUnit 6.1.3. Library versions live in the `ext` block of `build.gradle`; keep Serenity artifacts and the Serenity Gradle plugin on the same version. Serenity 4+ moved packages: `@Step` is `net.serenitybdd.annotations.Step`, `EnvironmentVariables` is `net.thucydides.model.util.EnvironmentVariables`.

Serenity REST injects generated classes via ByteBuddy reflection, which Java 26+ blocks. All `Test` tasks set `--add-opens java.base/java.lang=ALL-UNNAMED` **and** `net.bytebuddy.safe=false`. Without them every API test fails with `Could not initialize class RestSpecificationFactory`. ByteBuddy's error message says to set `net.bytebuddy.safe` to `true`; that is wrong on Java 26+ (default is already `true`), `false` is what works.

## Architecture (Screenplay)

Flow: `.feature` (Gherkin `# language: es`) → step definitions (`src/test/java/co/booker/stepdefinitions`) → `theActorInTheSpotlight().attemptsTo(Task)` → Task in `src/main/java/co/booker/tasks` issues a Serenity REST interaction → assertions via `should(seeThat(Question, matcher))`.

- **Single runner**: `co.booker.runners.CucumberTestSuite` (`@Suite`, `@IncludeEngines("cucumber")`, `@SelectClasspathResource("features")`). Glue, plugins (`SerenityReporterParallel` is required for Serenity reports), default tag filter and parallelism (4 threads) are in `src/test/resources/junit-platform.properties`. Don't add per-feature runners or JUnit 4 `CucumberWithSerenity` runners (deprecated for removal).
- **`ComunDefinition`** owns the hooks and every shared step: `@Before` creates the `OnlineCast` and reads `restapi.baseurl`; `Dado que Abel quiere usar la API` (in each feature's `Antecedentes`); response checks (`el código de respuesta debería ser`, `la respuesta debería cumplir el esquema`, `el campo ... debería ser`, `la cabecera ... debería contener`, `el cuerpo de la respuesta debería ser/contener`, `el tiempo de respuesta debería ser menor a ... ms`). Reuse these; put module-specific steps in `AuthDefinition`/`BookingDefinition`/`PingDefinition`.
- **Questions read `SerenityRest.lastResponse()`**, which is per thread, so assertions apply to the last request of the current scenario. `CampoRespuesta` (JsonPath → String), `CabeceraRespuesta`, `CuerpoRespuesta`, `CodigoRespuestaHttp`, `TiempoRespuestaHttp`.
- **Auth**: `Dado el está autenticado` gets a token for `admin/password123`, asserts it, and stores it under `Constantes.TOKEN`; `Dado el tiene un token inválido` stores a bogus one. Update/delete-by-cookie tasks add the cookie via `Autenticacion.conToken`, **only when a token exists**, so "no token" scenarios really send none. Basic Auth headers are built with `Autenticacion.basic(user, pass)`; RestAssured's `auth().preemptive().basic()` throws `ClassCastException` on Serenity's decorated request spec.
- **Tasks are created with `Tasks.instrumented(Class, args...)`**: args must match a public constructor exactly. `EnviarBooking` posts an arbitrary raw body (with custom `Content-Type`/`Accept`) for negative cases; feature files pass it as a DocString. Cucumber 8 treats any `"""` block after a step as its DocString, so use `#` for comments.

## Test data and validation conventions

- **Scenarios create their own data; never hardcode booking IDs.** `Dado el tiene una reserva creada` runs `CrearBooking`, asserts 200 and stores the id under `Constantes.BOOKING_ID` (`CrearBooking` remembers it on any 200). The `@After` hook in `ComunDefinition` deletes that booking with plain RestAssured (outside the Serenity report), ignoring errors. This is what makes parallel execution safe.
- **`src/test/resources/*.json` are JSON Schema draft-04** (what RestAssured's validator supports) with `additionalProperties: false`; shared sub-schemas are inlined under `definitions`. `EsquemasJsonTest` checks each schema accepts real API payloads and rejects broken ones; add cases there when changing a schema.
- **Known API defects** (intentional in Restful Booker) are scenarios tagged `@defectoConocido` that assert the *correct* behavior and therefore fail today: missing fields → 500, checkout before checkin accepted, non-numeric price stored as null, wrong `Content-Type` → 500, XML served as `text/html`. Don't "fix" them by asserting the buggy behavior.
- **Live API facts** (verified): valid credentials `admin/password123`; invalid credentials return **200** with `{"reason":"Bad credentials"}`; PUT/PATCH/DELETE without a valid token or Basic → 403 `Forbidden`; unknown or non-numeric id → 404 `Not Found`; malformed JSON → 400; `Accept: text/plain` → 418; DELETE → 201 plain-text `Created`; second DELETE → 405; `GET /ping` → 201.

## Unit tests

`src/test/java/co/booker/unitarias` (JUnit Jupiter, no network): JSON schemas, `Autenticacion`, `uris`/`Constantes`/`Esperar`. Tasks and Questions depend on a live `SerenityRest.lastResponse()` and are covered by the API suite, not unit tests.
