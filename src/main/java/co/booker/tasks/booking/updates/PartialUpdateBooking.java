package co.booker.tasks.booking.updates;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Patch;
import net.thucydides.core.annotations.Step;

import static co.booker.utils.Constantes.TOKEN;
import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class PartialUpdateBooking implements Task {

    private Number id;

    public PartialUpdateBooking(Number id) {
        this.id = id;
    }

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

    public static PartialUpdateBooking usuario(Integer id) {
        return instrumented(PartialUpdateBooking.class, id);
    }
}