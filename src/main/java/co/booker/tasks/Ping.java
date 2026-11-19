package co.booker.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.annotations.Step;

import static co.booker.utils.endpoints.uris.PING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Tarea que llama al health check de la API ({@code GET /ping}).
 *
 * <p>Se usa junto con {@link co.booker.questions.TiempoRespuestaHttp} para validar el tiempo de
 * respuesta del servicio.</p>
 */
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
    @Step("tiempo de respuesta del servicio")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Get.resource(PING.toString())
                        .with(requestSpecification -> requestSpecification
                        .header("Content-Type", "application/json")
                        .relaxedHTTPSValidation()
                )
        );
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