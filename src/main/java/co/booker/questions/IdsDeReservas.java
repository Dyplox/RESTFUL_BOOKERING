package co.booker.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

import java.util.Collection;

/** Pregunta que devuelve los {@code bookingid} de una respuesta de {@code GET /booking}. */
public class IdsDeReservas implements Question<Collection<Integer>> {

    /** Constructor público; usar {@link #enLaRespuesta()}. */
    public IdsDeReservas() {
        // Vacío a propósito: la pregunta no tiene estado; se crea con su método de fábrica.
    }

    /**
     * Crea la pregunta, para usarla en {@code Ensure.that(...)}.
     *
     * @return pregunta sobre los ids de la lista
     */
    public static IdsDeReservas enLaRespuesta() {
        return new IdsDeReservas();
    }

    /**
     * @param actor actor que hace la pregunta (no se usa)
     * @return ids de la lista de la última respuesta
     */
    @Override
    public Collection<Integer> answeredBy(Actor actor) {
        return SerenityRest.lastResponse().jsonPath().getList("bookingid", Integer.class);
    }

    @Override
    public String getSubject() {
        return "los ids de la lista de reservas";
    }
}
