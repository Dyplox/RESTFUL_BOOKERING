package co.booker.tasks.booking.gets;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/** Tarea que obtiene la lista de identificadores de todas las reservas ({@code GET /booking}). */
public class BookingIds implements Task {

    /** Constructor usado por Serenity al instrumentar la tarea; usar {@link #delServicio()}. */
    public BookingIds() {
        // Vacío a propósito: la tarea no tiene parámetros; Tasks.instrumented necesita este constructor.
    }

    /**
     * Envía la petición de listado en nombre del actor.
     *
     * @param actor actor con la habilidad {@code CallAnApi}
     * @param <T>   tipo del actor
     */
    @Override
    @Step("{0} consulta la lista de reservas")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Get.resource(BOOKING.toString()));
    }

    /**
     * Crea la tarea instrumentada por Serenity.
     *
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static BookingIds delServicio() {
        return instrumented(BookingIds.class);
    }
}
