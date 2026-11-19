package co.booker.stepdefinitions;

import co.booker.questions.CodigoRespuestaHttp;
import co.booker.tasks.CrearToken;
import co.booker.tasks.booking.GuardaToken;
import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.restassured.response.Response;
import io.restassured.response.ResponseOptions;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.thucydides.model.util.EnvironmentVariables;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Step definitions del módulo auth y pasos comunes a todas las features.
 *
 * <p>Contiene el {@code @Before} que prepara el escenario, el paso que crea al actor y los pasos
 * de validación (código HTTP y esquema JSON) que reutilizan booking y ping.</p>
 */
public class AuthDefinition {

    public static ResponseOptions<Response> response;
    private String theRestApiBaseUrl;
    private EnvironmentVariables environmentVariables;

    /**
     * Prepara el escenario antes de cada escenario de Cucumber: crea el {@code OnlineCast} y lee
     * {@code restapi.baseurl} de {@code serenity.properties} (por defecto la API pública).
     */
    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
        theRestApiBaseUrl = environmentVariables.optionalProperty("restapi.baseurl")
                .orElse("https://restful-booker.herokuapp.com/");
    }

    /** Crea al actor {@code Abel} con la habilidad de llamar a la API en la URL base. */
    @Dado("que Abel quiere logueaser en la Api")
    public void queAbelquiereLogueaserEnLaApi() {
        theActorCalled("Abel").whoCan(CallAnApi.at(theRestApiBaseUrl));
    }

    /**
     * Solicita un token con las credenciales dadas.
     *
     * @param userName nombre de usuario (puede ir vacío)
     * @param password contraseña (puede ir vacía)
     */
    @Cuando("el ingresa {string} and {string}")
    public void elIngresaAnd(String userName, String password) {
        theActorInTheSpotlight().attemptsTo(CrearToken.usuario(userName, password));
    }

    /** Guarda el token de la última respuesta en la memoria del actor. */
    @Cuando("el Guarda el token")
    public void elGuardaElToken() {
        theActorInTheSpotlight().attemptsTo(GuardaToken.ingreso());
    }

    /**
     * Verifica el código HTTP de la última respuesta.
     *
     * @param statusHttps código esperado
     */
    @Entonces("el verifica que el codigo HTTP sea {int}")
    public void elVerificaQueElCodigoHTTPSea(Integer statusHttps) {
        theActorInTheSpotlight().should(
                seeThat("el codigo de respuesta", CodigoRespuestaHttp.fue(), equalTo(statusHttps))
                //.orComplainWith(SerenityRest.lastResponse().getBody().jsonPath().get("reason"))
        );
    }

    /**
     * Valida el body de la última respuesta contra un archivo de {@code src/test/resources}.
     *
     * <p>Pese al nombre, se usa para cualquier respuesta, no solo la del token. Ojo: los archivos
     * actuales son ejemplos de respuesta, no JSON Schema, así que la validación es débil o falla.</p>
     *
     * @param jsonName nombre del archivo en el classpath
     */
    @Entonces("el deberia recibir el token y validar el esquema {string}")
    public void elDeberiaRecibirElTokenYValidarElEsquema(String jsonName) {
        String responseBody = SerenityRest.lastResponse().getBody().asString();
        assertThat(responseBody, matchesJsonSchemaInClasspath(jsonName));
    }
}