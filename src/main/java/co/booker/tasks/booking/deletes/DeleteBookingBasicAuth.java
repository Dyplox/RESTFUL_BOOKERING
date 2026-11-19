package co.booker.tasks.booking.deletes;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Delete;
import net.serenitybdd.annotations.Step;

import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Tarea que elimina una reserva autenticándose con Basic Auth ({@code DELETE /booking/{id}}).
 *
 * <p>Usa una cabecera {@code Authorization: Basic} fija con las credenciales públicas de
 * Restful Booker ({@code admin:password123}); no necesita token. La API responde {@code 201}
 * si la elimina y {@code 405} si la reserva no existe. Nota: la descripción de su {@code @Step}
 * ("Obtener libro por Id") está copiada.</p>
 */
public class DeleteBookingBasicAuth implements Task {

    private String id;

    /**
     * Constructor usado por Serenity al instrumentar la tarea; usar {@link #delServicio(String)}.
     *
     * @param id identificador de la reserva a eliminar
     */
    public DeleteBookingBasicAuth(String id) {
        this.id = id;
    }

    /**
     * Envía la petición de borrado en nombre del actor.
     *
     * @param actor actor con la habilidad {@code CallAnApi}
     * @param <T>   tipo del actor
     */
    @Override
    @Step("Obtener libro por Id")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Delete.from(BOOKING + id)
                        .with(requestSpecification -> requestSpecification
                                .header("Content-Type", "application/json")
                                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
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
    public static DeleteBookingBasicAuth delServicio(String id) {
        return instrumented(DeleteBookingBasicAuth.class, id);
    }
}