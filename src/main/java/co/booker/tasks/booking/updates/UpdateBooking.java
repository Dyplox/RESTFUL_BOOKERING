package co.booker.tasks.booking.updates;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Put;
import net.thucydides.core.annotations.Step;

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
        String body = "{\n" +
                "    \"username\" : \"admin\",\n" +
                "    \"password\" : \"password123\"\n" +
                "}";

        actor.attemptsTo(
                Put.to(BOOKING.toString() + id)
                        .with(requestSpecification -> requestSpecification
                                .header("Content-Type", "application/json")
                                .header("Cookie", "token=abc123")
                                .body(body)
                                .relaxedHTTPSValidation()
                        )
        );
        SerenityRest.lastResponse().getBody().prettyPrint();
    }

    public static UpdateBooking usuario() {
        return instrumented(UpdateBooking.class);
    }
}