package co.booker.tasks.booking.updates;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Put;
import net.serenitybdd.annotations.Step;

import static co.booker.utils.Autenticacion.conToken;
import static co.booker.utils.Constantes.TOKEN;
import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Tarea que reemplaza por completo una reserva ({@code PUT /booking/{id}}).
 *
 * <p>Se autentica con la cookie {@code token} (solo si el actor tiene token), usando el token guardado por
 * {@link co.booker.tasks.booking.GuardaToken}. El body es fijo (Juan Gutierrez Arias,
 * 2023-03-02 a 2023-03-09). Nota: la descripción de su {@code @Step} ("create the token") está
 * copiada y no corresponde a esta tarea.</p>
 */
public class UpdateBooking implements Task {

    private Number id;

    /**
     * Constructor usado por Serenity al instrumentar la tarea; usar {@link #usuario(Integer)}.
     *
     * @param id identificador de la reserva a actualizar
     */
    public UpdateBooking(Number id) {
        this.id = id;
    }

    /**
     * Envía la actualización en nombre del actor.
     *
     * @param actor actor que tiene guardado el token ({@code Constantes.TOKEN})
     * @param <T>   tipo del actor
     */
    @Override
    @Step("create the token")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall(TOKEN);
        String body = "{\n" +
                "    \"firstname\" : \"Juan\",\n" +
                "    \"lastname\" : \"Gutierrez Arias\",\n" +
                "    \"totalprice\" : 9898,\n" +
                "    \"depositpaid\" : false,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2023-03-02\",\n" +
                "        \"checkout\" : \"2023-03-09\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"lunch\"\n" +
                "}";

        actor.attemptsTo(
                Put.to(BOOKING.toString() + id)
                        .with(requestSpecification -> conToken(requestSpecification, token)
                                .header("Content-Type", "application/json")
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
    public static UpdateBooking usuario(Integer id) {
        return instrumented(UpdateBooking.class, id);
    }
}