package co.booker.unitarias;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * Pruebas de los JSON Schema de {@code src/test/resources}, sin red.
 *
 * <p>Comprueban que cada esquema acepta respuestas reales de Restful Booker (copiadas de la API) y
 * rechaza variantes incorrectas. Si un esquema se relaja por error (p. ej. deja de exigir un campo),
 * estas pruebas fallan antes de que las pruebas de API empiecen a dar falsos positivos.</p>
 */
class EsquemasJsonTest {

    private static final String RESERVA =
            "{\"firstname\":\"Jim\",\"lastname\":\"Brown\",\"totalprice\":111,\"depositpaid\":true,"
                    + "\"bookingdates\":{\"checkin\":\"2018-01-01\",\"checkout\":\"2019-01-01\"},"
                    + "\"additionalneeds\":\"Breakfast\"}";

    static Stream<Arguments> respuestasValidas() {
        return Stream.of(
                arguments("token.json", "{\"token\":\"5a6634509bf717a\"}"),
                arguments("authError.json", "{\"reason\":\"Bad credentials\"}"),
                arguments("getBookingIds.json", "[{\"bookingid\":1},{\"bookingid\":2}]"),
                arguments("getBookingIds.json", "[]"),
                arguments("booking.json", RESERVA),
                arguments("booking.json", RESERVA.replace(",\"additionalneeds\":\"Breakfast\"", "")),
                arguments("postBooking.json", "{\"bookingid\":4983,\"booking\":" + RESERVA + "}")
        );
    }

    static Stream<Arguments> respuestasInvalidas() {
        return Stream.of(
                arguments("token.json", "{\"reason\":\"Bad credentials\"}", "error de auth en vez de token"),
                arguments("token.json", "{\"token\":\"\"}", "token vacío"),
                arguments("token.json", "{\"token\":\"abc\",\"extra\":1}", "campo no esperado"),
                arguments("authError.json", "{\"token\":\"abc\"}", "token en vez de error"),
                arguments("authError.json", "{\"reason\":\"Otra cosa\"}", "mensaje distinto"),
                arguments("getBookingIds.json", "{\"bookingid\":1}", "objeto en vez de lista"),
                arguments("getBookingIds.json", "[{\"bookingid\":\"1\"}]", "id como texto"),
                arguments("getBookingIds.json", "[{\"id\":1}]", "nombre de campo incorrecto"),
                arguments("booking.json", RESERVA.replace("\"firstname\":\"Jim\",", ""), "falta firstname"),
                arguments("booking.json", RESERVA.replace("111", "\"111\""), "precio como texto"),
                arguments("booking.json", RESERVA.replace("111", "null"), "precio null"),
                arguments("booking.json", RESERVA.replace("2018-01-01", "01/01/2018"), "fecha con otro formato"),
                arguments("booking.json", RESERVA.replace("}", ",\"extra\":1}"), "campo no esperado"),
                arguments("booking.json", "{\"bookingid\":1,\"booking\":" + RESERVA + "}", "respuesta de POST en vez de reserva"),
                arguments("postBooking.json", RESERVA, "reserva sin bookingid"),
                arguments("postBooking.json", "{\"bookingid\":0,\"booking\":" + RESERVA + "}", "bookingid no positivo")
        );
    }

    @org.junit.jupiter.api.Test
    @DisplayName("ErroresDeEsquema devuelve vacío si cumple y la ruta del campo si no")
    void erroresDeEsquemaIndicaElCampoQueFalla() {
        org.junit.jupiter.api.Assertions.assertEquals("", co.booker.questions.ErroresDeEsquema.validar("booking.json", RESERVA));
        String errores = co.booker.questions.ErroresDeEsquema.validar("booking.json", RESERVA.replace("111", "\"111\""));
        assertTrue(errores.startsWith("/totalprice: "), errores);
        assertTrue(co.booker.questions.ErroresDeEsquema.validar("booking.json", "{x").startsWith("el body no es JSON"));
    }

    @ParameterizedTest(name = "{0} acepta {1}")
    @MethodSource("respuestasValidas")
    @DisplayName("Los esquemas aceptan respuestas reales de la API")
    void aceptaRespuestaValida(String esquema, String json) {
        assertTrue(matchesJsonSchemaInClasspath(esquema).matches(json));
    }

    @ParameterizedTest(name = "{0} rechaza: {2}")
    @MethodSource("respuestasInvalidas")
    @DisplayName("Los esquemas rechazan respuestas incorrectas")
    void rechazaRespuestaInvalida(String esquema, String json, String caso) {
        assertFalse(matchesJsonSchemaInClasspath(esquema).matches(json), caso);
    }
}
