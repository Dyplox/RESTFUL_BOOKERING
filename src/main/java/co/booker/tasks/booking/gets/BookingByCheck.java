package co.booker.tasks.booking.gets;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.thucydides.core.annotations.Step;

import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class BookingByCheck implements Task {

    private String checkIn;
    private String checkOut;

    public BookingByCheck(String checkIn, String checkOut) {
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }

    @Override
    @Step("Obtener libro por Nombre")
    public <T extends Actor> void performAs(T actor) {
        String queryParams = "?checkin=" + checkIn +
                "&checkout=" + checkOut;

        actor.attemptsTo(
                Get.resource(BOOKING + queryParams)
                        .with(requestSpecification -> requestSpecification
                                .header("Content-Type", "application/json")
                                .relaxedHTTPSValidation()
                        )
        );
    }

    public static BookingByCheck delServicio(String checkIn, String checkOut) {
        return instrumented(BookingByCheck.class, checkIn, checkOut);
    }
}