package co.booker.tasks.booking.deletes;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Delete;

import static co.booker.utils.Autenticacion.conToken;
import static co.booker.utils.Constantes.TOKEN;
import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Tarea que elimina una reserva autenticándose por cookie ({@code DELETE /booking/{id}}).
 *
 * <p>Envía la cookie {@code token} solo si el actor tiene token guardado. La API responde
 * {@code 201} si la elimina y {@code 403} sin un token válido.</p>
 */
public class DeleteBookingByCookie implements Task {

    private final String id;

    /**
     * Constructor usado por Serenity al instrumentar la tarea; usar {@link #delServicio(String)}.
     *
     * @param id identificador de la reserva a eliminar
     */
    public DeleteBookingByCookie(String id) {
        this.id = id;
    }

    /**
     * Envía la petición de borrado en nombre del actor.
     *
     * @param actor actor que puede tener guardado el token ({@code Constantes.TOKEN})
     * @param <T>   tipo del actor
     */
    @Override
    @Step("{0} elimina la reserva #id con su token")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall(TOKEN);
        actor.attemptsTo(Delete.from(BOOKING + id).with(peticion -> conToken(peticion, token)));
    }

    /**
     * Crea la tarea instrumentada por Serenity.
     *
     * @param id identificador de la reserva a eliminar
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static DeleteBookingByCookie delServicio(String id) {
        return instrumented(DeleteBookingByCookie.class, id);
    }
}
