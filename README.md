# RESTFUL_BOOKERING

Pruebas automatizadas de la API pública [Restful Booker](https://restful-booker.herokuapp.com/apidoc/index.html) con **Serenity BDD**, patrón **Screenplay**, **Cucumber** (escenarios en español) y **RestAssured**.

Cubre:

- **Auth**: generación de token con credenciales válidas e inválidas, tiempo de respuesta.
- **Booking**: CRUD completo (listar, filtrar por nombre y fechas, consultar por ID, crear, actualizar, actualizar parcialmente y eliminar por cookie o Basic Auth).
- **Ping**: health check y tiempo de respuesta.

Cada prueba valida el **código HTTP**, la **estructura del JSON** de respuesta y, en algunos casos, el **tiempo de respuesta**.

> 📘 Arquitectura, flujo de una prueba y problemas conocidos: [`docs/CONTEXTO.md`](docs/CONTEXTO.md)

## Stack

| Tecnología | Versión | Uso en el proyecto |
|---|---|---|
| Java (JDK) | 27 | Lenguaje; configurado como *toolchain* de Gradle |
| Gradle (wrapper) | 9.9.1 | Compilación, dependencias y ejecución de pruebas |
| Serenity BDD (`serenity-core`) | 3.3.9-RC3 | Framework de pruebas y reportes HTML |
| Serenity Screenplay REST | 3.3.9-RC3 | Patrón Screenplay e interacciones HTTP (`Get`, `Post`, `Put`, `Patch`, `Delete`) |
| Serenity Cucumber | 3.3.9-RC3 | Integración de Cucumber con Serenity (`CucumberWithSerenity`) |
| Serenity Gradle Plugin | 2.0.81 | Generación del reporte agregado (`aggregate`) |
| Cucumber (`cucumber-java`, `cucumber-junit`) | 4.2.0 | Escenarios BDD en Gherkin, en español |
| RestAssured | 5.3.0 | Motor HTTP bajo Serenity (`SerenityRest`) |
| RestAssured JSON Schema Validator | 5.3.0 | Validación del body contra un esquema JSON |
| Hamcrest | 2.2 | Matchers de aserción (`equalTo`, `lessThan`) |
| JUnit | 4 (runner) / 5.5.2 (plataforma) | Ejecución de los runners de Cucumber |
| Gson | 2.8.9 | Serialización JSON |
| SonarQube Gradle Plugin | 3.3 | Análisis estático de código (`./gradlew sonarqube`) |

## Requisitos

- **JDK 27**
- Conexión a internet (las pruebas llaman a la API real)

Gradle no hace falta instalarlo: el proyecto incluye el wrapper (Gradle 9.9.1). Las demás dependencias (Serenity, Cucumber, RestAssured, Hamcrest) se descargan solas.

## Instalación

```bash
git clone https://github.com/Dyplox/RESTFUL_BOOKERING.git
cd RESTFUL_BOOKERING
chmod +x gradlew   # solo la primera vez, si gradlew no es ejecutable
```

## Ejecución

```bash
# Todas las pruebas + reporte Serenity
./gradlew clean test

# Una sola feature
./gradlew test --tests 'co.booker.runners.AuthRunner'
./gradlew test --tests 'co.booker.runners.BookingRunner'
./gradlew test --tests 'co.booker.runners.PingRunner'
```

El reporte HTML se genera al terminar en:

```
target/site/serenity/index.html
```

> El build tiene `ignoreFailures = true`: termina en `BUILD SUCCESSFUL` aunque fallen pruebas. Revisa el reporte o la salida de consola.

## Configuración

La URL base se define en `serenity.properties`:

```properties
restapi.baseurl = "https://restful-booker.herokuapp.com/"
```

## Estructura

```
src/main/java/co/booker/
├── tasks/        Acciones del actor (CrearToken, Ping, CRUD de booking)
├── questions/    Lecturas de la respuesta (código HTTP, tiempo de respuesta)
├── models/       Modelos de datos
└── utils/        Endpoints y constantes

src/test/
├── java/co/booker/runners/          Un runner por feature
├── java/co/booker/stepdefinitions/  Pasos Cucumber → tareas Screenplay
└── resources/
    ├── features/                    auth, booking, ping (.feature)
    └── *.json                       Archivos de validación de respuesta
```

## Ejemplo de escenario

```gherkin
Escenario: Verificar si la creacion de Bookings es valida
  Dado que Abel quiere logueaser en la Api
  Cuando el llama el servicio para crear el bookin
  Entonces el verifica que el codigo HTTP sea 200
  Entonces el deberia recibir el token y validar el esquema "postBooking.json"
```

## Estado

La suite tiene fallos conocidos que no dependen de la versión de Java (archivos de esquema inválidos, IDs fijos, un error en `GuardaToken`). Están detallados con su solución en [`docs/CONTEXTO.md`](docs/CONTEXTO.md#8-problemas-conocidos).

## Autor

**Abel Fernando Gutierrez Arias** — SR QA Engineer / SDET — [GitHub @Dyplox](https://github.com/Dyplox)
