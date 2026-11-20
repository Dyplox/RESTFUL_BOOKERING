package co.booker.stepdefinitions;

import co.booker.questions.CabeceraRespuesta;
import co.booker.questions.CampoRespuesta;
import co.booker.questions.CodigoRespuestaHttp;
import co.booker.questions.CuerpoRespuesta;
import co.booker.questions.ErroresDeEsquema;
import co.booker.questions.TiempoRespuestaHttp;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import co.booker.utils.Autenticacion;
import co.booker.utils.ClienteApi;
import co.booker.utils.Configuracion;
import co.booker.utils.ReintentoInfraestructura;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.restassured.RestAssured;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.ensure.Ensure;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;

import static co.booker.utils.Constantes.BOOKING_ID;
import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;

/**
 * Hooks y pasos comunes a todas las features: preparación del escenario, creación del actor,
 * validaciones de la última respuesta y limpieza de datos al terminar.
 */
public class ComunDefinition {

    private static final int INTENTOS_HEALTH_CHECK = 3;
    private static final long ESPERA_INICIAL_HEALTH_CHECK_MS = 2000;

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
        String error;
        try {
            // Mismo reintento con espera exponencial que el resto de peticiones (2 s y 4 s) ante errores
            // de conexión o 502/503/504, p. ej. mientras la API pública de Heroku despierta.
            // noFilters() evita sumar el reintento global configurado en ClienteApi.
            int codigo = RestAssured.given().noFilters()
                    .filter(new ReintentoInfraestructura(INTENTOS_HEALTH_CHECK, ESPERA_INICIAL_HEALTH_CHECK_MS))
                    .baseUri(url).get("ping").statusCode();
            if (codigo == 201) {
                return;
            }
            error = "GET /ping respondió " + codigo;
        } catch (Exception e) {
            // RestAssured lanza excepciones checked (p. ej. ConnectException) sin declararlas
            error = e.getClass().getSimpleName() + ": " + e.getMessage();
        }
        throw new IllegalStateException("La API no está disponible en " + url + " (" + error
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
        } catch (RuntimeException _) {
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
        theActorInTheSpotlight().attemptsTo(Ensure.that("el código de respuesta", CodigoRespuestaHttp.fue()).isEqualTo(codigo));
    }

    /**
     * Valida el body de la última respuesta contra un JSON Schema de {@code src/test/resources}.
     *
     * @param esquema nombre del archivo de esquema en el classpath
     */
    @Entonces("la respuesta debería cumplir el esquema {string}")
    public void laRespuestaDeberiaCumplirElEsquema(String esquema) {
        theActorInTheSpotlight().attemptsTo(Ensure.that(ErroresDeEsquema.contra(esquema)).isEmpty());
    }

    /**
     * Verifica el valor de un campo del body JSON de la última respuesta.
     *
     * @param ruta  ruta JsonPath del campo (p. ej. {@code firstname}, {@code bookingdates.checkin})
     * @param valor valor esperado, como texto
     */
    @Entonces("el campo {string} de la respuesta debería ser {string}")
    public void elCampoDeLaRespuestaDeberiaSer(String ruta, String valor) {
        theActorInTheSpotlight().attemptsTo(Ensure.that(CampoRespuesta.en(ruta)).isEqualTo(valor));
    }

    /**
     * Verifica que una cabecera de la última respuesta contiene un texto.
     *
     * @param cabecera nombre de la cabecera
     * @param texto    texto que debe contener
     */
    @Entonces("la cabecera {string} de la respuesta debería contener {string}")
    public void laCabeceraDeLaRespuestaDeberiaContener(String cabecera, String texto) {
        theActorInTheSpotlight().attemptsTo(Ensure.that(CabeceraRespuesta.llamada(cabecera)).contains(texto));
    }

    /**
     * Verifica el cuerpo completo de la última respuesta, como texto.
     *
     * @param texto cuerpo esperado
     */
    @Entonces("el cuerpo de la respuesta debería ser {string}")
    public void elCuerpoDeLaRespuestaDeberiaSer(String texto) {
        theActorInTheSpotlight().attemptsTo(Ensure.that(CuerpoRespuesta.comoTexto()).isEqualTo(texto));
    }

    /**
     * Verifica que el cuerpo de la última respuesta contiene un texto.
     *
     * @param texto texto que debe contener
     */
    @Entonces("el cuerpo de la respuesta debería contener {string}")
    public void elCuerpoDeLaRespuestaDeberiaContener(String texto) {
        theActorInTheSpotlight().attemptsTo(Ensure.that(CuerpoRespuesta.comoTexto()).contains(texto));
    }

    /**
     * Verifica que la última respuesta tardó menos del límite dado.
     *
     * @param milisegundos límite en milisegundos (exclusivo)
     */
    @Entonces("el tiempo de respuesta debería ser menor a {int} ms")
    public void elTiempoDeRespuestaDeberiaSerMenorA(Integer milisegundos) {
        theActorInTheSpotlight().attemptsTo(
                Ensure.that("el tiempo de respuesta (ms)", TiempoRespuestaHttp.fue()).isLessThan(milisegundos.longValue()));
    }
}
