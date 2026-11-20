package co.booker.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

/**
 * Pregunta que devuelve el cuerpo de la última respuesta como texto, sin procesar.
 *
 * <p>Útil para respuestas que no son JSON, como los errores en texto plano de la API
 * ({@code Forbidden}, {@code Not Found}) o las respuestas XML.</p>
 */
public class CuerpoRespuesta implements Question<String> {

    /** Constructor público; usar {@link #comoTexto()}. */
    public CuerpoRespuesta() {
        // Vacío a propósito: la pregunta no tiene estado; se crea con su método de fábrica.
    }

    /**
     * Crea la pregunta, para usarla en {@code seeThat(...)}.
     *
     * @return pregunta sobre el cuerpo de la respuesta
     */
    public static CuerpoRespuesta comoTexto() {
        return new CuerpoRespuesta();
    }

    /**
     * @param actor actor que hace la pregunta (no se usa)
     * @return cuerpo de la última respuesta como texto
     */
    @Override
    public String answeredBy(Actor actor) {
        return SerenityRest.lastResponse().getBody().asString();
    }

    @Override
    public String getSubject() {
        return "el cuerpo de la respuesta";
    }
}
