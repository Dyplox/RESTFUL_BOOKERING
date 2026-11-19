package co.booker.tasks.booking.gets;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Tarea que filtra las reservas por fechas ({@code GET /booking?checkin=&checkout=}).
 *
 * <p>Los filtros de la API son estrictos: {@code checkin} devuelve reservas que entran
 * <b>después</b> de esa fecha y {@code checkout}, reservas que salen <b>antes</b>.</p>
 */
public class BookingByCheck implements Task {

    private final String checkIn;
    private final String checkOut;

    /**
     * Constructor usado por Serenity al instrumentar la tarea; usar {@link #delServicio(String, String)}.
     *
     * @param checkIn  fecha de entrada, formato {@code yyyy-MM-dd}
     * @param checkOut fecha de salida, formato {@code yyyy-MM-dd}
     */
    public BookingByCheck(String checkIn, String checkOut) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    /**
     * Envía la consulta filtrada en nombre del actor.
     *
     * @param actor actor con la habilidad {@code CallAnApi}
     * @param <T>   tipo del actor
     */
    @Override
    @Step("{0} busca reservas con entrada después de #checkIn y salida antes de #checkOut")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Get.resource(BOOKING.toString())
                .with(peticion -> peticion.queryParam("checkin", checkIn).queryParam("checkout", checkOut)));
    }

    /**
     * Crea la tarea instrumentada por Serenity.
     *
     * @param checkIn  fecha de entrada, formato {@code yyyy-MM-dd}
     * @param checkOut fecha de salida, formato {@code yyyy-MM-dd}
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static BookingByCheck delServicio(String checkIn, String checkOut) {
        return instrumented(BookingByCheck.class, checkIn, checkOut);
    }
}
