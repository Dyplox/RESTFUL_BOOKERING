package co.booker.unitarias;

import co.booker.exception.ThreadStoppedException;
import co.booker.utils.Constantes;
import co.booker.utils.Esperar;
import co.booker.utils.endpoints.uris;
import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas unitarias de las utilidades: endpoints, constantes y esperas. */
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
        assertNotEquals(Constantes.TOKEN, Constantes.BOOKING_ID);
    }

    @Test
    void esperarDetieneElHiloAlMenosElTiempoIndicado() {
        long inicio = System.nanoTime();
        Esperar.stopExecution(50);
        long transcurridoMs = (System.nanoTime() - inicio) / 1_000_000;
        assertTrue(transcurridoMs >= 50, "esperó " + transcurridoMs + " ms");
    }

    @Test
    void esperarInterrumpidoLanzaExcepcionYConservaLaInterrupcion() throws InterruptedException {
        AtomicReference<Throwable> error = new AtomicReference<>();
        AtomicReference<Boolean> sigueInterrumpido = new AtomicReference<>();
        Thread hilo = new Thread(() -> {
            Thread.currentThread().interrupt();
            try {
                Esperar.stopExecution(10_000);
            } catch (Throwable t) {
                error.set(t);
                sigueInterrumpido.set(Thread.currentThread().isInterrupted());
            }
        });
        hilo.start();
        hilo.join(5_000);

        assertInstanceOf(ThreadStoppedException.class, error.get());
        assertInstanceOf(InterruptedException.class, error.get().getCause());
        assertTrue(sigueInterrumpido.get(), "el flag de interrupción debe restaurarse");
    }
}
