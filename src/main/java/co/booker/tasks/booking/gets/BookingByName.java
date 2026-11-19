package co.booker.tasks.booking.gets;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;

import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Tarea que filtra las reservas por nombre y apellido ({@code GET /booking?firstname=&lastname=}).
 *
 * <p>El filtro de la API es por coincidencia exacta y distingue mayúsculas.</p>
 */
public class BookingByName implements Task {

    private final String nombre;
    private final String apellido;

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
    @Step("{0} busca reservas de '#nombre #apellido'")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Get.resource(BOOKING.toString())
                .with(peticion -> peticion.queryParam("firstname", nombre).queryParam("lastname", apellido)));
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
