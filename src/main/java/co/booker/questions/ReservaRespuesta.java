package co.booker.questions;

import co.booker.models.Booking;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

import static co.booker.utils.Json.aJson;
import static co.booker.utils.Json.desde;

/**
 * Pregunta que devuelve la reserva de la última respuesta como JSON canónico (mismo orden de campos
 * que {@link Booking}, sin campos desconocidos).
 *
 * <p>Se compara con {@code Ensure.that(...).isEqualTo(Json.aJson(esperada))}: si falla, el reporte
 * muestra la reserva esperada y la obtenida completas.</p>
 */
public class ReservaRespuesta implements Question<String> {

    private final String campo;

    /**
     * Constructor de la pregunta; usar {@link #completa()} o {@link #enElCampo(String)}.
     *
     * @param campo campo de primer nivel que contiene la reserva, o {@code null} si es el body entero
     */
    public ReservaRespuesta(String campo) {
        this.campo = campo;
    }

    /** @return pregunta sobre el body entero (GET por id, PUT, PATCH) */
    public static ReservaRespuesta completa() {
        return new ReservaRespuesta(null);
    }

    /**
     * @param campo campo que contiene la reserva (p. ej. {@code booking} en la respuesta de un POST)
     * @return pregunta sobre ese campo
     */
    public static ReservaRespuesta enElCampo(String campo) {
        return new ReservaRespuesta(campo);
    }

    /**
     * @param actor actor que hace la pregunta (no se usa)
     * @return la reserva de la respuesta como JSON canónico
     */
    @Override
    public String answeredBy(Actor actor) {
        String body = SerenityRest.lastResponse().getBody().asString();
        Booking reserva = campo == null ? desde(body, Booking.class) : desde(body, campo, Booking.class);
        return aJson(reserva);
    }

    @Override
    public String getSubject() {
        return campo == null ? "la reserva de la respuesta" : "la reserva del campo '" + campo + "' de la respuesta";
    }
}
