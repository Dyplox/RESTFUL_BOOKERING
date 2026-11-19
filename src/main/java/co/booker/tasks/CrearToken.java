package co.booker.tasks;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.annotations.Step;

import static co.booker.utils.endpoints.uris.AUTH;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Tarea que solicita un token de autenticación a la API.
 *
 * <p>Envía {@code POST /auth} con un body JSON {@code {"username", "password"}} e imprime la
 * respuesta en consola. No guarda el token: para usarlo en peticiones posteriores hay que
 * ejecutar después {@link co.booker.tasks.booking.GuardaToken}.</p>
 *
 * <p>Restful Booker responde {@code 200} tanto con credenciales válidas ({@code {"token": ...}})
 * como inválidas ({@code {"reason": "Bad credentials"}}).</p>
 */
public class CrearToken implements Task {
    private String user;
    private String password;

    /**
     * Constructor usado por Serenity al instrumentar la tarea; usar {@link #usuario(String, String)}.
     *
     * @param user     nombre de usuario
     * @param password contraseña
     */
    public CrearToken(String user, String password) {
        this.user = user;
        this.password = password;
    }

    /**
     * Envía la petición de autenticación en nombre del actor.
     *
     * @param actor actor con la habilidad {@code CallAnApi}
     * @param <T>   tipo del actor
     */
    @Override
    @Step("create the token")
    public <T extends Actor> void performAs(T actor) {
        String body = "{\n" +
                "    \"username\" : \"" + user + "\",\n" +
                "    \"password\" : \"" + password + "\"\n" +
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

    /**
     * Crea la tarea instrumentada por Serenity.
     *
     * @param user     nombre de usuario
     * @param password contraseña
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static CrearToken usuario(String user, String password) {
        return instrumented(CrearToken.class, user, password);
    }
}