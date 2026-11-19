package co.booker.tasks.booking.gets;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.serenitybdd.annotations.Step;

import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/** Tarea que filtra las reservas por nombre y apellido ({@code GET /booking?firstname=&lastname=}). */
public class BookingByName implements Task {

    private String nombre;
    private String apellido;

    /**
     * Constructor usado por Serenity al instrumentar la tarea; usar {@link #delServicio(String, String)}.
     *
     * @param nombre   nombre del huésped ({@code firstname})
     * @param apellido apellido del huésped ({@code lastname})
     */
    public BookingByName(String nombre, String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
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
        String queryParams = "?firstname=" + nombre +
                "&lastname=" + apellido;

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
     * @param nombre   nombre del huésped
     * @param apellido apellido del huésped
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static BookingByName delServicio(String nombre, String apellido) {
        return instrumented(BookingByName.class, nombre, apellido);
    }
}