package co.booker.tasks.booking;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

import static co.booker.utils.Constantes.TOKEN;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class GuardaToken implements Task {
    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.remember(TOKEN, lastResponse().getBody().jsonPath().get("token"));
    }

    public static GuardaToken ingreso() {
        return instrumented(GuardaToken.class);
    }
}