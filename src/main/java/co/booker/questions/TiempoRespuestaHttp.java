package co.booker.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Pregunta que devuelve el tiempo de respuesta, en milisegundos, de la última petición.
 *
 * <p>Lee {@code SerenityRest.lastResponse()}, es decir, la última petición ejecutada en el
 * hilo, sin importar qué actor la hizo.</p>
 */
public class TiempoRespuestaHttp implements Question<Long> {

    /** Constructor público; usar {@link #fue()}. */
    public TiempoRespuestaHttp() {
    }

    /**
     * Crea la pregunta, para usarla en {@code seeThat(...)}.
     *
     * @return pregunta sobre el tiempo de respuesta
     */
    public static Question<Long> fue() {
        return new TiempoRespuestaHttp();
    }

    /**
     * @param actor actor que hace la pregunta (no se usa)
     * @return tiempo de la última respuesta en milisegundos
     */
    @Override
    public Long answeredBy(Actor actor) {
        return SerenityRest.lastResponse().getTimeIn(MILLISECONDS);
    }
}