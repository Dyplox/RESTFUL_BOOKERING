package co.booker.tasks.booking;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.annotations.Step;

import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Tarea que crea una reserva ({@code POST /booking}).
 *
 * <p>Usa un body fijo (Jim Brown, 2018-01-01 a 2019-01-01, Breakfast) construido como texto, e
 * imprime la respuesta en consola. La respuesta incluye el {@code bookingid} generado.</p>
 */
public class CrearBooking implements Task {

    /** Constructor usado por Serenity al instrumentar la tarea; usar {@link #usuario()}. */
    public CrearBooking() {
    }

    /**
     * Envía la petición de creación en nombre del actor.
     *
     * @param actor actor con la habilidad {@code CallAnApi}
     * @param <T>   tipo del actor
     */
    @Override
    @Step("create the new booking")
    public <T extends Actor> void performAs(T actor) {
        String body = "{\n" +
                "    \"firstname\" : \"Jim\",\n" +
                "    \"lastname\" : \"Brown\",\n" +
                "    \"totalprice\" : 111,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2018-01-01\",\n" +
                "        \"checkout\" : \"2019-01-01\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";

        actor.attemptsTo(
                Post.to(BOOKING.toString())
                        .with(requestSpecification -> requestSpecification
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
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static CrearBooking usuario() {
        return instrumented(CrearBooking.class);
    }
}