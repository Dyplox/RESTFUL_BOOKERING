package co.booker.tasks.booking.gets;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.annotations.Step;

import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/** Tarea que obtiene la lista de identificadores de todas las reservas ({@code GET /booking}). */
public class BookingIds implements Task {

    /** Constructor usado por Serenity al instrumentar la tarea; usar {@link #delServicio()}. */
    public BookingIds() {
    }

    /**
     * Envía la petición de listado en nombre del actor.
     *
     * @param actor actor con la habilidad {@code CallAnApi}
     * @param <T>   tipo del actor
     */
    @Override
    @Step("Lista de libros en el servicio")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Get.resource(BOOKING.toString())
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
    public static BookingIds delServicio() {
        return instrumented(BookingIds.class);
    }
}