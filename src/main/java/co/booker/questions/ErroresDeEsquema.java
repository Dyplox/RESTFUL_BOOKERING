package co.booker.questions;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Pregunta que valida el body de la última respuesta contra un JSON Schema del classpath y devuelve
 * los errores encontrados, uno por línea con la ruta del campo, o una cadena vacía si lo cumple.
 *
 * <p>Se usa con {@code Ensure.that(ErroresDeEsquema.contra("x.json")).isEmpty()}: si falla, el
 * reporte muestra exactamente qué campo no cumple el esquema (p. ej.
 * {@code /totalprice: instance type (integer) does not match ... (allowed: ["string"])}). Usa
 * directamente el validador que hay bajo {@code JsonSchemaValidator} de RestAssured, porque este
 * solo describe el fallo repitiendo el body.</p>
 */
public class ErroresDeEsquema implements Question<String> {

    private static final JsonSchemaFactory FABRICA = JsonSchemaFactory.byDefault();

    private final String esquema;

    /**
     * Constructor de la pregunta; usar {@link #contra(String)}.
     *
     * @param esquema nombre del archivo de esquema en el classpath
     */
    public ErroresDeEsquema(String esquema) {
        this.esquema = esquema;
    }

    /**
     * Crea la pregunta, para usarla en {@code Ensure.that(...)}.
     *
     * @param esquema nombre del archivo de esquema en el classpath
     * @return pregunta sobre los errores de validación
     */
    public static ErroresDeEsquema contra(String esquema) {
        return new ErroresDeEsquema(esquema);
    }

    /**
     * @param actor actor que hace la pregunta (no se usa)
     * @return errores de validación, o {@code ""} si el body cumple el esquema
     */
    @Override
    public String answeredBy(Actor actor) {
        return validar(esquema, SerenityRest.lastResponse().getBody().asString());
    }

    /**
     * Valida un JSON contra un esquema del classpath.
     *
     * @param esquema nombre del archivo de esquema en el classpath
     * @param json    documento a validar
     * @return errores de validación, uno por línea, o {@code ""} si es válido
     */
    public static String validar(String esquema, String json) {
        JsonNode documento;
        try {
            documento = JsonLoader.fromString(json);
        } catch (IOException e) {
            return "el body no es JSON válido: " + e.getMessage();
        }
        try {
            JsonNode definicion = JsonLoader.fromResource("/" + esquema);
            ProcessingReport informe = FABRICA.getJsonSchema(definicion).validate(documento, true);
            List<String> errores = new ArrayList<>();
            for (ProcessingMessage mensaje : informe) {
                if (mensaje.getLogLevel().compareTo(LogLevel.ERROR) >= 0) {
                    agregarErrores(mensaje.asJson(), errores);
                }
            }
            return String.join("\n", errores);
        } catch (IOException | ProcessingException e) {
            throw new IllegalStateException("No se pudo cargar el esquema '" + esquema + "'", e);
        }
    }

    /**
     * Añade los errores de un mensaje. Los de combinadores ({@code allOf}, {@code anyOf}…) traen los
     * errores reales anidados en {@code reports}; se recorren para informar del campo concreto.
     */
    private static void agregarErrores(JsonNode mensaje, List<String> errores) {
        JsonNode anidados = mensaje.path("reports");
        if (anidados.isObject() && anidados.size() > 0) {
            int antes = errores.size();
            anidados.forEach(lista -> lista.forEach(hijo -> agregarErrores(hijo, errores)));
            if (errores.size() > antes) {
                return;
            }
        }
        String ruta = mensaje.path("instance").path("pointer").asText();
        errores.add((ruta.isEmpty() ? "/" : ruta) + ": " + mensaje.path("message").asText());
    }

    @Override
    public String getSubject() {
        return "los errores de la respuesta contra el esquema '" + esquema + "'";
    }
}
