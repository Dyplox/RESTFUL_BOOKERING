package co.booker.tasks.booking.gets;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.thucydides.core.annotations.Step;

import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class BookingByName implements Task {

    private String nombre;
    private String apellido;

    public BookingByName(String nombre, String apellido) {
        this.nombre = nombre;
        this.apellido = apellido;
    }

    @Override
    @Step("Obtener libro por Nombre")
    public <T extends Actor> void performAs(T actor) {
        String queryParams = "?firstname=" + nombre +
                "&lastname=" + apellido;

        actor.attemptsTo(
                Get.resource(BOOKING + queryParams)
                        .with(requestSpecification -> requestSpecification
                                .header("Content-Type", "application/json")
                                .relaxedHTTPSValidation()
                        )
        );
    }

    public static BookingByName delServicio(String nombre, String apellido) {
        return instrumented(BookingByName.class, nombre, apellido);
    }
}