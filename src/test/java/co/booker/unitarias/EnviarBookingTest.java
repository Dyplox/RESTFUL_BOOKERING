package co.booker.unitarias;

import co.booker.tasks.booking.EnviarBooking;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/** Pruebas unitarias de {@link EnviarBooking#idEnRespuesta(String)}: el id que se recuerda para limpiar. */
class EnviarBookingTest {

    private static final String RESERVA = "{\"firstname\":\"A\",\"lastname\":\"B\",\"totalprice\":1,\"depositpaid\":true,"
            + "\"bookingdates\":{\"checkin\":\"2031-01-01\",\"checkout\":\"2031-01-02\"}}";

    @Test
    @DisplayName("Lee el bookingid de una respuesta JSON")
    void leeElIdDeUnaRespuestaJson() {
        assertEquals(42, EnviarBooking.idEnRespuesta("{\"bookingid\":42,\"booking\":" + RESERVA + "}"));
    }

    @Test
    @DisplayName("Lee el bookingid de una respuesta XML (Accept: application/xml)")
    void leeElIdDeUnaRespuestaXml() {
        String xml = """
                <?xml version='1.0'?>
                <created-booking>
                    <bookingid>37</bookingid>
                    <booking><firstname>A</firstname><lastname>B</lastname></booking>
                </created-booking>
                """;
        assertEquals(37, EnviarBooking.idEnRespuesta(xml));
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @ValueSource(strings = {"", "Created", "{x", "{\"reason\":\"Bad credentials\"}", "<html>error</html>"})
    @DisplayName("Devuelve null si el body no trae un bookingid")
    void devuelveNullSiNoHayId(String cuerpo) {
        assertNull(EnviarBooking.idEnRespuesta(cuerpo));
    }
}
