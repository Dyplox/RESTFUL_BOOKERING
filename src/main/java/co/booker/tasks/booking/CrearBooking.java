package co.booker.tasks.booking;

import co.booker.models.Booking;
import co.booker.models.ReservaFactory;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import static co.booker.utils.Constantes.BOOKING_ID;
import static co.booker.utils.Constantes.RESERVA_CREADA;
import static co.booker.utils.Constantes.RESERVA_ENVIADA;
import static co.booker.utils.Json.aJson;
import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Tarea que crea una reserva ({@code POST /booking}).
 *
 * <p>Guarda en el actor la reserva enviada ({@code RESERVA_ENVIADA}) y, si la API responde
 * {@code 200}, también el {@code bookingid} ({@code BOOKING_ID}) y la reserva creada
 * ({@code RESERVA_CREADA}) para los pasos siguientes y para la limpieza del escenario.</p>
 */
public class CrearBooking implements Task {

    private final Booking reserva;

    /**
     * Constructor usado por Serenity al instrumentar la tarea; usar las factorías estáticas.
     *
     * @param reserva reserva a crear
     */
    public CrearBooking(Booking reserva) {
        this.reserva = reserva;
    }

    /**
     * Envía la petición de creación en nombre del actor.
     *
     * @param actor actor con la habilidad {@code CallAnApi}
     * @param <T>   tipo del actor
     */
    @Override
    @Step("{0} crea una reserva para #reserva")
    public <T extends Actor> void performAs(T actor) {
        actor.remember(RESERVA_ENVIADA, reserva);
        actor.attemptsTo(Post.to(BOOKING.toString()).with(peticion -> peticion.body(aJson(reserva))));
        if (SerenityRest.lastResponse().statusCode() == 200) {
            actor.remember(BOOKING_ID, SerenityRest.lastResponse().jsonPath().getInt("bookingid"));
            actor.remember(RESERVA_CREADA, reserva);
        }
    }

    /**
     * Crea la tarea con una reserva válida de datos únicos ({@link ReservaFactory#valida()}).
     *
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static CrearBooking valida() {
        return con(ReservaFactory.valida());
    }

    /**
     * Crea la tarea con la reserva indicada.
     *
     * @param reserva reserva a crear
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static CrearBooking con(Booking reserva) {
        return instrumented(CrearBooking.class, reserva);
    }
}
