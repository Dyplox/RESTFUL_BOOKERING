package co.booker.unitarias;

import co.booker.models.Booking;
import co.booker.models.BookingDates;
import co.booker.models.ReservaFactory;
import co.booker.utils.Json;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.IntStream;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas unitarias de {@link ReservaFactory}, {@link Booking} y {@link Json}. */
class ReservaFactoryTest {

    @Test
    void laReservaValidaCumpleElEsquemaDeLaApi() {
        String json = Json.aJson(ReservaFactory.valida());
        assertTrue(matchesJsonSchemaInClasspath("booking.json").matches(json), json);
    }

    @Test
    void laReservaActualizadaCumpleElEsquemaDeLaApi() {
        String json = Json.aJson(ReservaFactory.actualizada());
        assertTrue(matchesJsonSchemaInClasspath("booking.json").matches(json), json);
    }

    @Test
    void cadaReservaTieneUnApellidoUnico() {
        Set<String> apellidos = new HashSet<>();
        IntStream.range(0, 500).forEach(i -> apellidos.add(ReservaFactory.valida().lastname()));
        assertEquals(500, apellidos.size());
    }

    @Test
    void laSalidaEsPosteriorALaEntradaYEstaEnElFuturo() {
        for (int i = 0; i < 200; i++) {
            BookingDates fechas = ReservaFactory.valida().bookingdates();
            assertTrue(fechas.salida().isAfter(fechas.entrada()), fechas.toString());
            assertTrue(fechas.entrada().isAfter(java.time.LocalDate.now()), fechas.toString());
        }
    }

    @Test
    void laReservaActualizadaCambiaTodosLosCampos() {
        Booking original = ReservaFactory.valida();
        Booking nueva = ReservaFactory.actualizada();
        assertNotEquals(original.firstname(), nueva.firstname());
        assertNotEquals(original.lastname(), nueva.lastname());
        assertNotEquals(original.totalprice(), nueva.totalprice());
        assertNotEquals(original.depositpaid(), nueva.depositpaid());
        assertNotEquals(original.bookingdates(), nueva.bookingdates());
        assertNotEquals(original.additionalneeds(), nueva.additionalneeds());
    }

    @Test
    void elCambioParcialSoloSerializaNombreYApellido() {
        var campos = JsonParser.parseString(Json.aJson(ReservaFactory.cambioDeNombre())).getAsJsonObject().keySet();
        assertEquals(Set.of("firstname", "lastname"), campos);
    }

    @Test
    void conAplicaSoloLosCamposNoNulosComoUnPatch() {
        Booking original = ReservaFactory.valida();
        Booking cambios = new Booking("Ana", null, 5, null, null, null);
        Booking resultado = original.con(cambios);
        assertEquals(new Booking("Ana", original.lastname(), 5, original.depositpaid(),
                original.bookingdates(), original.additionalneeds()), resultado);
    }

    @Test
    void laSerializacionEsReversibleYUsaLosNombresDeLaApi() {
        Booking reserva = ReservaFactory.valida();
        String json = Json.aJson(reserva);
        assertTrue(json.contains("\"bookingdates\":{\"checkin\":"), json);
        assertEquals(reserva, Json.desde(json, Booking.class));
        assertEquals(reserva, Json.desde("{\"bookingid\":7,\"booking\":" + json + "}", "booking", Booking.class));
    }
}
