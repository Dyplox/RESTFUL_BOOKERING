package co.booker.tasks.booking.deletes;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Delete;
import net.serenitybdd.annotations.Step;

import static co.booker.utils.Autenticacion.conToken;
import static co.booker.utils.Constantes.TOKEN;
import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Tarea que elimina una reserva autenticándose por cookie ({@code DELETE /booking/{id}}).
 *
 * <p>Envía la cookie {@code token} (solo si el actor tiene token) con el token guardado por
 * {@link co.booker.tasks.booking.GuardaToken}. La API responde {@code 201} si la elimina.
 * Nota: la descripción de su {@code @Step} ("Obtener libro por Id") está copiada.</p>
 */
public class DeleteBookingByCookie implements Task {

    private String id;

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
     * @param actor actor que tiene guardado el token ({@code Constantes.TOKEN})
     * @param <T>   tipo del actor
     */
    @Override
    @Step("Obtener libro por Id")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall(TOKEN);
        actor.attemptsTo(
                Delete.from(BOOKING + id)
                        .with(requestSpecification -> conToken(requestSpecification, token)
                                .header("Content-Type", "application/json")
                                .relaxedHTTPSValidation()
                        )
        );
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