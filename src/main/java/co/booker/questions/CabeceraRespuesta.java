package co.booker.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

/** Pregunta que devuelve el valor de una cabecera de la última respuesta HTTP. */
public class CabeceraRespuesta implements Question<String> {

    private final String nombre;

    /**
     * Constructor de la pregunta; usar {@link #llamada(String)}.
     *
     * @param nombre nombre de la cabecera (no distingue mayúsculas)
     */
    public CabeceraRespuesta(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Crea la pregunta, para usarla en {@code seeThat(...)}.
     *
     * @param nombre nombre de la cabecera (no distingue mayúsculas)
     * @return pregunta sobre el valor de la cabecera
     */
    public static CabeceraRespuesta llamada(String nombre) {
        return new CabeceraRespuesta(nombre);
    }

    /**
     * @param actor actor que hace la pregunta (no se usa)
     * @return valor de la cabecera, o {@code null} si no viene
     */
    @Override
    public String answeredBy(Actor actor) {
        return SerenityRest.lastResponse().getHeader(nombre);
    }

    @Override
    public String getSubject() {
        return "la cabecera '" + nombre + "' de la respuesta";
    }
}
