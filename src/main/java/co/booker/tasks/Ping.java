package co.booker.tasks;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import static co.booker.utils.endpoints.uris.PING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/** Tarea que llama al health check de la API ({@code GET /ping}, responde {@code 201}). */
public class Ping implements Task {

    /** Constructor usado por Serenity al instrumentar la tarea; usar {@link #delServicio()}. */
    public Ping() {
    }

    /**
     * Envía la petición de health check en nombre del actor.
     *
     * @param actor actor con la habilidad {@code CallAnApi}
     * @param <T>   tipo del actor
     */
    @Override
    @Step("{0} consulta el health check")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Get.resource(PING.toString()));
    }

    /**
     * Crea la tarea instrumentada por Serenity.
     *
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static Ping delServicio() {
        return instrumented(Ping.class);
    }
}
