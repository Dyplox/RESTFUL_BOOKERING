package co.booker.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

/**
 * Pregunta que devuelve, como texto, el valor de un campo del body JSON de la última respuesta.
 *
 * <p>El campo se indica con una ruta de JsonPath de RestAssured (p. ej. {@code firstname},
 * {@code bookingdates.checkin}). Lee {@code SerenityRest.lastResponse()}, es decir, la última
 * petición ejecutada en el hilo.</p>
 */
public class CampoRespuesta implements Question<String> {

    private final String ruta;

    /**
     * Constructor de la pregunta; usar {@link #en(String)}.
     *
     * @param ruta ruta JsonPath del campo
     */
    public CampoRespuesta(String ruta) {
        this.ruta = ruta;
    }

    /**
     * Crea la pregunta, para usarla en {@code seeThat(...)}.
     *
     * @param ruta ruta JsonPath del campo
     * @return pregunta sobre el valor del campo
     */
    public static CampoRespuesta en(String ruta) {
        return new CampoRespuesta(ruta);
    }

    /**
     * @param actor actor que hace la pregunta (no se usa)
     * @return valor del campo como texto, o {@code null} si no existe
     */
    @Override
    public String answeredBy(Actor actor) {
        return SerenityRest.lastResponse().jsonPath().getString(ruta);
    }

    @Override
    public String getSubject() {
        return "el campo '" + ruta + "' de la respuesta";
    }
}
