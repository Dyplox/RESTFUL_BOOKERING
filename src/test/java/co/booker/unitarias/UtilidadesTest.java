package co.booker.unitarias;

import co.booker.utils.Constantes;
import co.booker.utils.endpoints.uris;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Pruebas unitarias de las utilidades: endpoints y constantes. */
class UtilidadesTest {

    @Test
    void endpointsRelativosALaUrlBase() {
        assertEquals("auth", uris.AUTH.toString());
        assertEquals("ping", uris.PING.toString());
        // BOOKING termina en "/" para concatenarle el id o los query params
        assertEquals("booking/", uris.BOOKING.toString());
        assertEquals("booking/42", uris.BOOKING + "42");
    }

    @Test
    void lasClavesDeMemoriaDelActorNoColisionan() {
        assertEquals(4, java.util.Set.of(Constantes.TOKEN, Constantes.BOOKING_ID,
                Constantes.RESERVA_CREADA, Constantes.RESERVA_ENVIADA).size());
    }
}
