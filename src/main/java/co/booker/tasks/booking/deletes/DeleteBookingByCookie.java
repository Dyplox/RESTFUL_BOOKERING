package co.booker.tasks.booking.deletes;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Delete;
import net.thucydides.core.annotations.Step;

import static co.booker.utils.Constantes.TOKEN;
import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class DeleteBookingByCookie implements Task {

    private String id;

    public DeleteBookingByCookie(String id) {
        this.id = id;
    }

    @Override
    @Step("Obtener libro por Id")
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall(TOKEN);
        actor.attemptsTo(
                Delete.from(BOOKING + id)
                        .with(requestSpecification -> requestSpecification
                                .header("Content-Type", "application/json")
                                .header("Cookie", "token=" + token)
                                .relaxedHTTPSValidation()
                        )
        );
    }

    public static DeleteBookingByCookie delServicio(String id) {
        return instrumented(DeleteBookingByCookie.class, id);
    }
}