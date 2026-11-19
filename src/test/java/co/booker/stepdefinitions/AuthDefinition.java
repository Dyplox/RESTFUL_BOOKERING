package co.booker.stepdefinitions;

import co.booker.questions.CampoRespuesta;
import co.booker.questions.CodigoRespuestaHttp;
import co.booker.tasks.CrearToken;
import co.booker.tasks.booking.GuardaToken;
import co.booker.utils.Configuracion;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.ensure.Ensure;

import static co.booker.utils.Constantes.TOKEN;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;

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

    /** Solicita un token con las credenciales del entorno activo ({@code serenity.conf}). */
    @Cuando("el se autentica con las credenciales configuradas")
    public void elSeAutenticaConLasCredencialesConfiguradas() {
        theActorInTheSpotlight().attemptsTo(CrearToken.usuario(Configuracion.usuario(), Configuracion.clave()));
    }

    /** Guarda el token de la última respuesta en la memoria del actor. */
    @Cuando("el guarda el token")
    public void elGuardaElToken() {
        theActorInTheSpotlight().attemptsTo(GuardaToken.ingreso());
    }

    /**
     * Precondición: se autentica con las credenciales del entorno activo, verifica que recibió un
     * token y lo guarda en el actor.
     */
    @Dado("el está autenticado")
    public void elEstaAutenticado() {
        Actor actor = theActorInTheSpotlight();
        actor.attemptsTo(CrearToken.usuario(Configuracion.usuario(), Configuracion.clave()));
        actor.attemptsTo(
                Ensure.that("el código al autenticarse", CodigoRespuestaHttp.fue()).isEqualTo(200),
                Ensure.that("el token recibido", CampoRespuesta.en("token")).isNotBlank());
        actor.attemptsTo(GuardaToken.ingreso());
    }

    /** Precondición: el actor tiene guardado un token que la API no reconoce. */
    @Dado("el tiene un token inválido")
    public void elTieneUnTokenInvalido() {
        theActorInTheSpotlight().remember(TOKEN, "token-invalido");
    }
}
