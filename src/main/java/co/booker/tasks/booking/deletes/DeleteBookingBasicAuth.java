package co.booker.tasks.booking.deletes;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Delete;
import net.thucydides.core.annotations.Step;

import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class DeleteBookingBasicAuth implements Task {

    private String id;

    public DeleteBookingBasicAuth(String id) {
        this.id = id;
    }

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

    public static DeleteBookingBasicAuth delServicio(String id) {
        return instrumented(DeleteBookingBasicAuth.class, id);
    }
}