package co.booker.tasks;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.thucydides.core.annotations.Step;

import static co.booker.utils.endpoints.uris.AUTH;
import static net.serenitybdd.screenplay.Tasks.instrumented;

public class CrearToken implements Task {
    private String user;
    private String password;

    public CrearToken(String user, String password) {
        this.user = user;
        this.password = password;
    }

    @Override
    @Step("create the token")
    public <T extends Actor> void performAs(T actor) {
        String body = "{\n" +
                "    \"username\" : \"admin\",\n" +
                "    \"password\" : \"password123\"\n" +
                "}";

        actor.attemptsTo(
                Post.to(AUTH.toString())
                        .with(requestSpecification -> requestSpecification
                                .header("Content-Type", "application/json")
                                .body(body)
                                .relaxedHTTPSValidation()
                        )
        );
        SerenityRest.lastResponse().getBody().prettyPrint();
    }

    public static CrearToken usuario(String user, String password) {
        return instrumented(CrearToken.class, user, password);
    }
}