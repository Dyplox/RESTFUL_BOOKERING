package co.booker.tasks.booking.gets;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/** Tarea que obtiene el detalle de una reserva por su identificador ({@code GET /booking/{id}}). */
public class BookingByID implements Task {

    private final String id;

    /**
     * Constructor usado por Serenity al instrumentar la tarea; usar {@link #delServicio(String)}.
     *
     * @param id identificador de la reserva
     */
    public BookingByID(String id) {
        this.id = id;
    }

    /**
     * Envía la consulta de la reserva en nombre del actor.
     *
     * @param actor actor con la habilidad {@code CallAnApi}
     * @param <T>   tipo del actor
     */
    @Override
    @Step("{0} consulta la reserva #id")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Get.resource(BOOKING + id));
    }

    /**
     * Crea la tarea instrumentada por Serenity.
     *
     * @param id identificador de la reserva
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static BookingByID delServicio(String id) {
        return instrumented(BookingByID.class, id);
    }
}
