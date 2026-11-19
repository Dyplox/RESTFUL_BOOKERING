package co.booker.tasks.booking;

import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Tarea que envía a {@code POST /booking} un body arbitrario, tal cual, sin validarlo.
 *
 * <p>Pensada para casos negativos (campos faltantes, tipos incorrectos, JSON malformado,
 * {@code Content-Type} o {@code Accept} distintos). A diferencia de {@link CrearBooking}, no guarda
 * el {@code bookingid}: el escenario decide qué validar.</p>
 */
public class EnviarBooking implements Task {

    private final String body;
    private final String contentType;
    private final String accept;

    /**
     * Constructor usado por Serenity al instrumentar la tarea; usar las factorías estáticas.
     *
     * @param body        body de la petición, sin procesar
     * @param contentType valor de la cabecera {@code Content-Type}
     * @param accept      valor de la cabecera {@code Accept}
     */
    public EnviarBooking(String body, String contentType, String accept) {
        this.body = body;
        this.contentType = contentType;
        this.accept = accept;
    }

    /**
     * Envía el body en nombre del actor.
     *
     * @param actor actor con la habilidad {@code CallAnApi}
     * @param <T>   tipo del actor
     */
    @Override
    @Step("{0} envía una reserva con Content-Type #contentType y Accept #accept")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Post.to(BOOKING.toString())
                        .with(requestSpecification -> requestSpecification
                                .contentType(contentType)
                                .accept(accept)
                                .body(body)
                                .relaxedHTTPSValidation()
                        )
        );
    }

    /**
     * Crea la tarea con {@code Content-Type} y {@code Accept} JSON.
     *
     * @param body body de la petición, sin procesar
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static EnviarBooking conBody(String body) {
        return conBody(body, "application/json", "application/json");
    }

    /**
     * Crea la tarea con cabeceras a medida.
     *
     * @param body        body de la petición, sin procesar
     * @param contentType valor de la cabecera {@code Content-Type}
     * @param accept      valor de la cabecera {@code Accept}
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static EnviarBooking conBody(String body, String contentType, String accept) {
        return instrumented(EnviarBooking.class, body, contentType, accept);
    }
}
