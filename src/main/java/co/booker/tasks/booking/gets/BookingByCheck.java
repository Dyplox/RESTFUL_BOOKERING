package co.booker.tasks.booking.gets;

import co.booker.models.Booking;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.thucydides.core.annotations.Step;

import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class BookingByCheck implements Task {

    private Booking booking;

    public BookingByCheck(Booking booking) {
        this.booking = booking;
    }

    @Override
    @Step("Obtener libro por Nombre")
    public <T extends Actor> void performAs(T actor) {
        String dates = booking.getBookingDates().toString();
        actor.attemptsTo(
                Get.resource(BOOKING + dates)
                        .with(requestSpecification -> requestSpecification
                                .header("Content-Type", "application/json")
                                .relaxedHTTPSValidation()
                        )
        );
    }

    public static BookingByCheck delServicio() {
        return instrumented(BookingByCheck.class);
    }
}