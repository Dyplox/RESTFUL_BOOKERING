package co.booker.tasks.booking;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;

import static co.booker.utils.Constantes.TOKEN;
import static net.serenitybdd.rest.SerenityRest.lastResponse;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class GuardaToken implements Task {
    private String token;

    public GuardaToken(String token) {
        this.token = token;
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        token = lastResponse().getBody().jsonPath().get("token");
        actor.remember(TOKEN, token);
    }

    public static GuardaToken ingreso() {
        return instrumented(GuardaToken.class);
    }
}