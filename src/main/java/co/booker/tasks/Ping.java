package co.booker.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.thucydides.core.annotations.Step;

import static co.booker.utils.endpoints.uris.PING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class Ping implements Task {

    public Ping() {
    }

    @Override
    @Step("tiempo de respuesta del servicio")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Get.resource(PING.toString())
                        .with(requestSpecification -> requestSpecification
                        .header("Content-Type", "application/json")
                        .relaxedHTTPSValidation()
                )
        );
    }

    public static Ping delServicio() {
        return instrumented(Ping.class);
    }
}