package co.booker.tasks.booking.gets;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Tarea que consulta una reserva ({@code GET /booking/{id}}) pidiendo un formato concreto con la
 * cabecera {@code Accept} (p. ej. {@code application/xml}).
 */
public class BookingEnFormato implements Task {

    private final String id;
    private final String accept;

    /**
     * Constructor usado por Serenity al instrumentar la tarea; usar {@link #delServicio(String, String)}.
     *
     * @param id     identificador de la reserva
     * @param accept valor de la cabecera {@code Accept}
     */
    public BookingEnFormato(String id, String accept) {
        this.id = id;
        this.accept = accept;
    }

    /**
     * Envía la consulta en nombre del actor.
     *
     * @param actor actor con la habilidad {@code CallAnApi}
     * @param <T>   tipo del actor
     */
    @Override
    @Step("{0} consulta la reserva #id con Accept '#accept'")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Get.resource(BOOKING + id).with(peticion -> peticion.accept(accept)));
    }

    /**
     * Crea la tarea instrumentada por Serenity.
     *
     * @param id     identificador de la reserva
     * @param accept valor de la cabecera {@code Accept}
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static BookingEnFormato delServicio(String id, String accept) {
        return instrumented(BookingEnFormato.class, id, accept);
    }
}
