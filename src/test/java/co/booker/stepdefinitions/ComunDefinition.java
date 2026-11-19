package co.booker.stepdefinitions;

import co.booker.questions.CabeceraRespuesta;
import co.booker.questions.CampoRespuesta;
import co.booker.questions.CodigoRespuestaHttp;
import co.booker.questions.CuerpoRespuesta;
import co.booker.questions.TiempoRespuestaHttp;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import co.booker.utils.Autenticacion;
import co.booker.utils.ClienteApi;
import co.booker.utils.Configuracion;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.restassured.RestAssured;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

import static co.booker.utils.Constantes.BOOKING_ID;
import static co.booker.utils.endpoints.uris.BOOKING;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.lessThan;

/**
 * Hooks y pasos comunes a todas las features: preparación del escenario, creación del actor,
 * validaciones de la última respuesta y limpieza de datos al terminar.
 */
public class ComunDefinition {

    private static final int INTENTOS_HEALTH_CHECK = 3;

    /**
     * Se ejecuta una vez antes de toda la suite: aplica la configuración global del cliente HTTP
     * ({@link ClienteApi}) y comprueba que la API del entorno activo responde a {@code GET /ping}.
     * Si no responde, la suite se detiene con un mensaje claro en lugar de fallar escenario por
     * escenario. No se ejecuta en el dry-run de {@code validarFeatures}.
     */
    @BeforeAll
    public static void verificarApiDisponible() {
        ClienteApi.configurar();
        String url = Configuracion.urlBase();
        String ultimoError = "sin respuesta";
        for (int intento = 1; intento <= INTENTOS_HEALTH_CHECK; intento++) {
            try {
                int codigo = RestAssured.given().baseUri(url).get("ping").statusCode();
                if (codigo == 201) {
                    return;
                }
                ultimoError = "GET /ping respondió " + codigo;
            } catch (Exception e) {
                // RestAssured lanza excepciones checked (p. ej. ConnectException) sin declararlas
                ultimoError = e.getClass().getSimpleName() + ": " + e.getMessage();
            }
            if (intento < INTENTOS_HEALTH_CHECK) {
                esperar(2000L * intento);
            }
        }
        throw new IllegalStateException("La API no está disponible en " + url + " (" + ultimoError
                + "). Si usas -Penv=docker, arranca el contenedor: "
                + "docker run -d -p 3001:3001 mwinteringham/restfulbooker");
    }

    /** Prepara cada escenario creando el {@code OnlineCast}. */
    @Before
    public void prepararEscenario() {
        OnStage.setTheStage(new OnlineCast());
    }

    /**
     * Elimina la reserva que creó el escenario, si existe, para no acumular datos en la API
     * pública. Usa RestAssured directamente para no ensuciar el reporte de Serenity; los errores
     * (p. ej. la reserva ya fue borrada por el propio escenario) se ignoran.
     */
    @After
    public void limpiarReservaCreada() {
        try {
            Integer id = theActorInTheSpotlight().recall(BOOKING_ID);
            if (id != null) {
                RestAssured.given().baseUri(Configuracion.urlBase())
                        .header("Authorization", Autenticacion.basic(Configuracion.usuario(), Configuracion.clave()))
                        .delete(BOOKING.toString() + id);
            }
        } catch (RuntimeException ignorada) {
            // La limpieza nunca debe hacer fallar un escenario.
        }
    }

    /** Crea al actor {@code Abel} con la habilidad de llamar a la API en la URL base. */
    @Dado("que Abel quiere usar la API")
    public void queAbelQuiereUsarLaApi() {
        theActorCalled("Abel").whoCan(CallAnApi.at(Configuracion.urlBase()));
    }

    /**
     * Verifica el código HTTP de la última respuesta.
     *
     * @param codigo código esperado
     */
    @Entonces("el código de respuesta debería ser {int}")
    public void elCodigoDeRespuestaDeberiaSer(Integer codigo) {
        theActorInTheSpotlight().should(seeThat("el código de respuesta", CodigoRespuestaHttp.fue(), equalTo(codigo)));
    }

    /**
     * Valida el body de la última respuesta contra un JSON Schema de {@code src/test/resources}.
     *
     * @param esquema nombre del archivo de esquema en el classpath
     */
    @Entonces("la respuesta debería cumplir el esquema {string}")
    public void laRespuestaDeberiaCumplirElEsquema(String esquema) {
        assertThat(SerenityRest.lastResponse().getBody().asString(), matchesJsonSchemaInClasspath(esquema));
    }

    /**
     * Verifica el valor de un campo del body JSON de la última respuesta.
     *
     * @param ruta  ruta JsonPath del campo (p. ej. {@code firstname}, {@code bookingdates.checkin})
     * @param valor valor esperado, como texto
     */
    @Entonces("el campo {string} de la respuesta debería ser {string}")
    public void elCampoDeLaRespuestaDeberiaSer(String ruta, String valor) {
        theActorInTheSpotlight().should(seeThat(CampoRespuesta.en(ruta), equalTo(valor)));
    }

    /**
     * Verifica que una cabecera de la última respuesta contiene un texto.
     *
     * @param cabecera nombre de la cabecera
     * @param texto    texto que debe contener
     */
    @Entonces("la cabecera {string} de la respuesta debería contener {string}")
    public void laCabeceraDeLaRespuestaDeberiaContener(String cabecera, String texto) {
        theActorInTheSpotlight().should(seeThat(CabeceraRespuesta.llamada(cabecera), containsString(texto)));
    }

    /**
     * Verifica el cuerpo completo de la última respuesta, como texto.
     *
     * @param texto cuerpo esperado
     */
    @Entonces("el cuerpo de la respuesta debería ser {string}")
    public void elCuerpoDeLaRespuestaDeberiaSer(String texto) {
        theActorInTheSpotlight().should(seeThat(CuerpoRespuesta.comoTexto(), equalTo(texto)));
    }

    /**
     * Verifica que el cuerpo de la última respuesta contiene un texto.
     *
     * @param texto texto que debe contener
     */
    @Entonces("el cuerpo de la respuesta debería contener {string}")
    public void elCuerpoDeLaRespuestaDeberiaContener(String texto) {
        theActorInTheSpotlight().should(seeThat(CuerpoRespuesta.comoTexto(), containsString(texto)));
    }

    /**
     * Verifica que la última respuesta tardó menos del límite dado.
     *
     * @param milisegundos límite en milisegundos (exclusivo)
     */
    @Entonces("el tiempo de respuesta debería ser menor a {int} ms")
    public void elTiempoDeRespuestaDeberiaSerMenorA(Integer milisegundos) {
        Actor actor = theActorInTheSpotlight();
        actor.should(seeThat("el tiempo de respuesta (ms)", TiempoRespuestaHttp.fue(), lessThan(milisegundos.longValue())));
    }

    private static void esperar(long milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Espera interrumpida", e);
        }
    }
}
