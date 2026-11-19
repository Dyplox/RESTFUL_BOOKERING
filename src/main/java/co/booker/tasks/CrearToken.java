package co.booker.tasks;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.Map;

import static co.booker.utils.Json.aJson;
import static co.booker.utils.endpoints.uris.AUTH;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Tarea que solicita un token de autenticación ({@code POST /auth}).
 *
 * <p>No guarda el token: para usarlo después hay que ejecutar
 * {@link co.booker.tasks.booking.GuardaToken}. La API responde {@code 200} tanto con credenciales
 * válidas ({@code {"token": ...}}) como inválidas ({@code {"reason": "Bad credentials"}}).</p>
 */
public class CrearToken implements Task {

    private final String usuario;
    private final String clave;

    /**
     * Constructor usado por Serenity al instrumentar la tarea; usar {@link #usuario(String, String)}.
     *
     * @param usuario nombre de usuario
     * @param clave   contraseña
     */
    public CrearToken(String usuario, String clave) {
        this.usuario = usuario;
        this.clave = clave;
    }

    /**
     * Envía la petición de autenticación en nombre del actor.
     *
     * @param actor actor con la habilidad {@code CallAnApi}
     * @param <T>   tipo del actor
     */
    @Override
    @Step("{0} solicita un token con el usuario '#usuario'")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Post.to(AUTH.toString())
                .with(peticion -> peticion.body(aJson(Map.of("username", usuario, "password", clave)))));
    }

    /**
     * Crea la tarea instrumentada por Serenity.
     *
     * @param usuario nombre de usuario
     * @param clave   contraseña
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static CrearToken usuario(String usuario, String clave) {
        return instrumented(CrearToken.class, usuario, clave);
    }
}
