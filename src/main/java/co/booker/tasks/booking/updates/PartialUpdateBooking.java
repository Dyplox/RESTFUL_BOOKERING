package co.booker.tasks.booking.updates;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Patch;
import net.serenitybdd.annotations.Step;

import static co.booker.utils.Constantes.TOKEN;
import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Tarea que actualiza parcialmente una reserva ({@code PATCH /booking/{id}}).
 *
 * <p>Solo cambia {@code firstname} y {@code lastname} (Jose Gutierrez Rendon). Se autentica con
 * la cabecera {@code Cookie: token=...}, usando el token guardado por
 * {@link co.booker.tasks.booking.GuardaToken}. Nota: la descripción de su {@code @Step}
 * ("create the token") está copiada y no corresponde a esta tarea.</p>
 */
public class PartialUpdateBooking implements Task {

    private Number id;

    /**
     * Constructor usado por Serenity al instrumentar la tarea; usar {@link #usuario(Integer)}.
     *
     * @param id identificador de la reserva a actualizar
     */
    public PartialUpdateBooking(Number id) {
        this.id = id;
    }

    /**
     * Envía la actualización parcial en nombre del actor.
     *
     * @param actor actor que tiene guardado el token ({@code Constantes.TOKEN})
     * @param <T>   tipo del actor
     */
    @Override
    @Step("create the token")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall(TOKEN);
        String body = "{\n" +
                "    \"firstname\" : \"Jose\",\n" +
                "    \"lastname\" : \"Gutierrez Rendon\"\n" +
                "}";

        actor.attemptsTo(
                Patch.to(BOOKING.toString() + id)
                        .with(requestSpecification -> requestSpecification
                                .header("Content-Type", "application/json")
                                .header("Cookie", "token=" + token)
                                .body(body)
                                .relaxedHTTPSValidation()
                        )
        );
        SerenityRest.lastResponse().getBody().prettyPrint();
    }

    /**
     * Crea la tarea instrumentada por Serenity.
     *
     * @param id identificador de la reserva a actualizar
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static PartialUpdateBooking usuario(Integer id) {
        return instrumented(PartialUpdateBooking.class, id);
    }
}