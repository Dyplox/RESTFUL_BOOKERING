package co.booker.tasks.booking.gets;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.thucydides.core.annotations.Step;

import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class ListBookings implements Task {

    public ListBookings() {
    }

    @Override
    @Step("Lista de libros en el servicio")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Get.resource(BOOKING.toString())
                        .with(requestSpecification -> requestSpecification
                                .header("Content-Type", "application/json")
                                .relaxedHTTPSValidation()
                        )
        );
    }

    public static ListBookings delServicio() {
        return instrumented(ListBookings.class);
    }
}