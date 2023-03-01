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
import net.thucydides.core.util.EnvironmentVariables;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AuthDefinition {

    public static ResponseOptions<Response> response;
    private String theRestApiBaseUrl;
    private EnvironmentVariables environmentVariables;

    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
        theRestApiBaseUrl = environmentVariables.optionalProperty("restapi.baseurl")
                .orElse("https://restful-booker.herokuapp.com/");
    }

    @Dado("que Abel quiere logueaser en la Api")
    public void queAbelquiereLogueaserEnLaApi() {
        theActorCalled("Abel").whoCan(CallAnApi.at(theRestApiBaseUrl));
    }

    @Cuando("el ingresa {string} and {string}")
    public void elIngresaAnd(String userName, String password) {
        theActorInTheSpotlight().attemptsTo(CrearToken.usuario(userName, password));
    }

    @Cuando("el Guarda el token")
    public void elGuardaElToken() {
        theActorInTheSpotlight().attemptsTo(GuardaToken.ingreso());
    }

    @Entonces("el verifica que el codigo HTTP sea {int}")
    public void elVerificaQueElCodigoHTTPSea(Integer statusHttps) {
        theActorInTheSpotlight().should(
                seeThat("el codigo de respuesta", CodigoRespuestaHttp.fue(), equalTo(statusHttps))
                //.orComplainWith(SerenityRest.lastResponse().getBody().jsonPath().get("reason"))
        );
    }

    @Entonces("el deberia recibir el token y validar el esquema {string}")
    public void elDeberiaRecibirElTokenYValidarElEsquema(String jsonName) {
        String responseBody = SerenityRest.lastResponse().getBody().asString();
        assertThat(responseBody, matchesJsonSchemaInClasspath(jsonName));
    }
}