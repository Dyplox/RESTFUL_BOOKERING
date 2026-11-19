package co.booker.tasks.booking.updates;

import co.booker.models.Booking;
import co.booker.models.ReservaFactory;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Put;

import static co.booker.utils.Autenticacion.conToken;
import static co.booker.utils.Constantes.RESERVA_ENVIADA;
import static co.booker.utils.Constantes.TOKEN;
import static co.booker.utils.Json.aJson;
import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Tarea que reemplaza por completo una reserva ({@code PUT /booking/{id}}).
 *
 * <p>Envía la cookie {@code token} solo si el actor tiene token guardado, y guarda la reserva
 * enviada en el actor ({@code RESERVA_ENVIADA}) para compararla con la respuesta.</p>
 */
public class UpdateBooking implements Task {

    private final Number id;
    private final Booking reserva;

    /**
     * Constructor usado por Serenity al instrumentar la tarea; usar las factorías estáticas.
     *
     * @param id      identificador de la reserva a actualizar
     * @param reserva nuevos datos de la reserva
     */
    public UpdateBooking(Number id, Booking reserva) {
        this.id = id;
        this.reserva = reserva;
    }

    /**
     * Envía la actualización en nombre del actor.
     *
     * @param actor actor que puede tener guardado el token ({@code Constantes.TOKEN})
     * @param <T>   tipo del actor
     */
    @Override
    @Step("{0} reemplaza la reserva #id")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall(TOKEN);
        actor.remember(RESERVA_ENVIADA, reserva);
        actor.attemptsTo(Put.to(BOOKING.toString() + id)
                .with(peticion -> conToken(peticion, token).body(aJson(reserva))));
    }

    /**
     * Crea la tarea con datos nuevos y únicos ({@link ReservaFactory#actualizada()}).
     *
     * @param id identificador de la reserva a actualizar
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static UpdateBooking usuario(Integer id) {
        return con(id, ReservaFactory.actualizada());
    }

    /**
     * Crea la tarea con los datos indicados.
     *
     * @param id      identificador de la reserva a actualizar
     * @param reserva nuevos datos de la reserva
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static UpdateBooking con(Integer id, Booking reserva) {
        return instrumented(UpdateBooking.class, id, reserva);
    }
}
