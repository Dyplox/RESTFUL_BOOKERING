package co.booker.tasks.booking;

import co.booker.models.Booking;
import io.restassured.path.json.JsonPath;
import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.Optional;

import static co.booker.utils.Constantes.BOOKING_ID;
import static co.booker.utils.Constantes.RESERVA_ENVIADA;
import static co.booker.utils.Json.desde;
import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Tarea que envía a {@code POST /booking} un body arbitrario, tal cual, sin validarlo.
 *
 * <p>Pensada para casos negativos (campos faltantes, tipos incorrectos, JSON malformado,
 * {@code Content-Type} o {@code Accept} distintos) y valores límite. Si el body es una reserva
 * legible, la guarda como {@code RESERVA_ENVIADA} para compararla con la respuesta. Si la API la
 * crea (incluso cuando no debería), guarda el {@code bookingid} para que la limpieza la elimine.</p>
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
    @Step("{0} envía una reserva con Content-Type '#contentType' y Accept '#accept'")
    public <T extends Actor> void performAs(T actor) {
        reservaLegible().ifPresent(reserva -> actor.remember(RESERVA_ENVIADA, reserva));
        actor.attemptsTo(Post.to(BOOKING.toString())
                .with(peticion -> peticion.contentType(contentType).accept(accept).body(body)));
        Integer id = idCreado();
        if (id != null && actor.recall(BOOKING_ID) == null) {
            actor.remember(BOOKING_ID, id);
        }
    }

    private Optional<Booking> reservaLegible() {
        try {
            return Optional.ofNullable(desde(body, Booking.class));
        } catch (RuntimeException _) {
            return Optional.empty();
        }
    }

    private static Integer idCreado() {
        Response respuesta = SerenityRest.lastResponse();
        return respuesta.statusCode() == 200 ? idEnRespuesta(respuesta.getBody().asString()) : null;
    }

    /**
     * Extrae el {@code bookingid} de la respuesta de {@code POST /booking}, en JSON o en XML. Con
     * {@code Accept: application/xml} la API crea la reserva y responde {@code <created-booking>}
     * (servido como {@code text/html}): sin leer el id de ahí, la limpieza no la borraría.
     *
     * @param cuerpo body de la respuesta
     * @return id de la reserva creada, o {@code null} si el body no lo contiene
     */
    public static Integer idEnRespuesta(String cuerpo) {
        try {
            return cuerpo.stripLeading().startsWith("<")
                    ? Integer.valueOf(XmlPath.from(cuerpo).getString("created-booking.bookingid"))
                    : JsonPath.from(cuerpo).getObject("bookingid", Integer.class);
        } catch (RuntimeException _) {
            return null;
        }
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
