package co.booker.stepdefinitions;

import co.booker.questions.CampoRespuesta;
import co.booker.questions.CodigoRespuestaHttp;
import co.booker.tasks.CrearToken;
import co.booker.tasks.booking.GuardaToken;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import net.serenitybdd.screenplay.Actor;

import static co.booker.utils.Constantes.TOKEN;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.emptyOrNullString;

/** Step definitions de autenticación: pedir y guardar el token, o preparar un token inválido. */
public class AuthDefinition {

    /**
     * Solicita un token con las credenciales dadas.
     *
     * @param usuario nombre de usuario (puede ir vacío)
     * @param clave   contraseña (puede ir vacía)
     */
    @Cuando("el se autentica con usuario {string} y contraseña {string}")
    public void elSeAutenticaConUsuarioYContrasena(String usuario, String clave) {
        theActorInTheSpotlight().attemptsTo(CrearToken.usuario(usuario, clave));
    }

    /** Guarda el token de la última respuesta en la memoria del actor. */
    @Cuando("el guarda el token")
    public void elGuardaElToken() {
        theActorInTheSpotlight().attemptsTo(GuardaToken.ingreso());
    }

    /**
     * Precondición: se autentica con las credenciales públicas de Restful Booker, verifica que
     * recibió un token y lo guarda en el actor.
     */
    @Dado("el está autenticado")
    public void elEstaAutenticado() {
        Actor actor = theActorInTheSpotlight();
        actor.attemptsTo(CrearToken.usuario("admin", "password123"));
        actor.should(
                seeThat("el código al autenticarse", CodigoRespuestaHttp.fue(), equalTo(200)),
                seeThat(CampoRespuesta.en("token"), not(emptyOrNullString()))
        );
        actor.attemptsTo(GuardaToken.ingreso());
    }

    /** Precondición: el actor tiene guardado un token que la API no reconoce. */
    @Dado("el tiene un token inválido")
    public void elTieneUnTokenInvalido() {
        theActorInTheSpotlight().remember(TOKEN, "token-invalido");
    }
}
