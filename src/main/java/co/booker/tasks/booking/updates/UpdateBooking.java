package co.booker.tasks.booking.updates;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Put;
import net.thucydides.core.annotations.Step;

import static co.booker.utils.Constantes.TOKEN;
import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class UpdateBooking implements Task {

    private Number id;

    public UpdateBooking(Number id) {
        this.id = id;
    }

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
                        .with(requestSpecification -> requestSpecification
                                .header("Content-Type", "application/json")
                                .header("Cookie", "token=" + token)
                                .body(body)
                                .relaxedHTTPSValidation()
                        )
        );
        SerenityRest.lastResponse().getBody().prettyPrint();
    }

    public static UpdateBooking usuario(Integer id) {
        return instrumented(UpdateBooking.class, id);
    }
}