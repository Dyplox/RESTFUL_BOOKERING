package co.booker.tasks.booking.updates;

import co.booker.models.Booking;
import co.booker.models.ReservaFactory;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Patch;

import static co.booker.utils.Autenticacion.conToken;
import static co.booker.utils.Constantes.RESERVA_ENVIADA;
import static co.booker.utils.Constantes.TOKEN;
import static co.booker.utils.Json.aJson;
import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Tarea que actualiza parcialmente una reserva ({@code PATCH /booking/{id}}).
 *
 * <p>Solo se envían los campos no nulos de la reserva parcial. Envía la cookie {@code token} solo
 * si el actor tiene token guardado, y guarda los cambios enviados en el actor
 * ({@code RESERVA_ENVIADA}).</p>
 */
public class PartialUpdateBooking implements Task {

    private final Number id;
    private final Booking cambios;

    /**
     * Constructor usado por Serenity al instrumentar la tarea; usar las factorías estáticas.
     *
     * @param id      identificador de la reserva a actualizar
     * @param cambios reserva parcial con los campos a cambiar
     */
    public PartialUpdateBooking(Number id, Booking cambios) {
        this.id = id;
        this.cambios = cambios;
    }

    /**
     * Envía la actualización parcial en nombre del actor.
     *
     * @param actor actor que puede tener guardado el token ({@code Constantes.TOKEN})
     * @param <T>   tipo del actor
     */
    @Override
    @Step("{0} actualiza parcialmente la reserva #id")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall(TOKEN);
        actor.remember(RESERVA_ENVIADA, cambios);
        actor.attemptsTo(Patch.to(BOOKING.toString() + id)
                .with(peticion -> conToken(peticion, token).body(aJson(cambios))));
    }

    /**
     * Crea la tarea cambiando nombre y apellido ({@link ReservaFactory#cambioDeNombre()}).
     *
     * @param id identificador de la reserva a actualizar
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static PartialUpdateBooking usuario(Integer id) {
        return con(id, ReservaFactory.cambioDeNombre());
    }

    /**
     * Crea la tarea con los cambios indicados.
     *
     * @param id      identificador de la reserva a actualizar
     * @param cambios reserva parcial con los campos a cambiar
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static PartialUpdateBooking con(Integer id, Booking cambios) {
        return instrumented(PartialUpdateBooking.class, id, cambios);
    }
}
