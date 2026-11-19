package co.booker.tasks.booking.gets;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.annotations.Step;

import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Tarea que filtra las reservas por fechas ({@code GET /booking?checkin=&checkout=}).
 *
 * <p>Nota: la descripción de su {@code @Step} ("Obtener libro por Nombre") está copiada de
 * {@link BookingByName} y no corresponde a esta tarea.</p>
 */
public class BookingByCheck implements Task {

    private String checkIn;
    private String checkOut;

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
    @Step("Obtener libro por Nombre")
    public <T extends Actor> void performAs(T actor) {
        String queryParams = "?checkin=" + checkIn +
                "&checkout=" + checkOut;

        actor.attemptsTo(
                Get.resource(BOOKING + queryParams)
                        .with(requestSpecification -> requestSpecification
                                .header("Content-Type", "application/json")
                                .relaxedHTTPSValidation()
                        )
        );
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