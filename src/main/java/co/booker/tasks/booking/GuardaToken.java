package co.booker.tasks.booking;

import net.serenitybdd.annotations.Step;
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
 */
public class GuardaToken implements Task {

    /** Constructor usado por Serenity al instrumentar la tarea; usar {@link #ingreso()}. */
    public GuardaToken() {
        // Vacío a propósito: la tarea no tiene parámetros; Tasks.instrumented necesita este constructor.
    }

    /**
     * Extrae el token de la última respuesta y lo recuerda en el actor.
     *
     * @param actor actor que guardará el token
     * @param <T>   tipo del actor
     */
    @Override
    @Step("{0} guarda el token de autenticación")
    public <T extends Actor> void performAs(T actor) {
        String token = lastResponse().jsonPath().getString("token");
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
