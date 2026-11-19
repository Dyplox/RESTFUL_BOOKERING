package co.booker.tasks.booking;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

import static co.booker.utils.Constantes.TOKEN;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Tarea que lee el campo {@code token} de la última respuesta HTTP y lo guarda en la memoria
 * del actor bajo la clave {@link co.booker.utils.Constantes#TOKEN}.
 *
 * <p>Debe ejecutarse justo después de {@link co.booker.tasks.CrearToken}. Las tareas de
 * actualización y borrado por cookie lo recuperan con {@code actor.recall(TOKEN)}.</p>
 *
 * <p><b>Defecto conocido:</b> {@link #ingreso()} instrumenta la clase sin argumentos, pero el único
 * constructor recibe un {@code String}; Serenity no puede crear la instancia y el paso falla con
 * {@code NullPointerException}. Ver {@code docs/CONTEXTO.md}.</p>
 */
public class GuardaToken implements Task {
    private String token;

    /**
     * Constructor de la tarea.
     *
     * @param token valor inicial del token; se sobrescribe con el de la última respuesta al ejecutar
     */
    public GuardaToken(String token) {
        this.token = token;
    }

    /**
     * Extrae el token de la última respuesta y lo recuerda en el actor.
     *
     * @param actor actor que guardará el token
     * @param <T>   tipo del actor
     */
    @Override
    public <T extends Actor> void performAs(T actor) {
        token = lastResponse().getBody().jsonPath().get("token");
        actor.remember(TOKEN, token);
    }

    /**
     * Crea la tarea instrumentada por Serenity.
     *
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static GuardaToken ingreso() {
        return instrumented(GuardaToken.class);
    }
}