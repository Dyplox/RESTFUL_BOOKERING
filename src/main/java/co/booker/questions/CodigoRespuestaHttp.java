package co.booker.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

/**
 * Pregunta que devuelve el código de estado HTTP de la última respuesta.
 *
 * <p>Lee {@code SerenityRest.lastResponse()}, es decir, la última petición ejecutada en el
 * hilo, sin importar qué actor la hizo.</p>
 */
public class CodigoRespuestaHttp implements Question<Integer> {
    /** Constructor público; usar {@link #fue()}. */
    public CodigoRespuestaHttp() {
    }

    /**
     * Crea la pregunta, para usarla en {@code seeThat(...)}.
     *
     * @return pregunta sobre el código HTTP
     */
    public static Question<Integer> fue() {
        return new CodigoRespuestaHttp();
    }

    /**
     * @param actor actor que hace la pregunta (no se usa)
     * @return código de estado de la última respuesta
     */
    @Override
    public Integer answeredBy(Actor actor) {
        return SerenityRest.lastResponse().statusCode();
    }
}
