package co.booker.utils;

import co.booker.exception.ThreadStoppedException;

import static co.booker.utils.Constantes.THREAD_STOPPED_EXCEPTION_MESSAGE;

/** Utilidad para pausar la ejecución. No instanciable. */
public class Esperar {
    private Esperar() {
    }

    /**
     * Detiene el hilo actual durante el tiempo indicado.
     *
     * @param milliseconds tiempo de espera en milisegundos
     * @throws co.booker.exception.ThreadStoppedException si el hilo se interrumpe durante la espera
     */
    public static void stopExecution(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ThreadStoppedException(THREAD_STOPPED_EXCEPTION_MESSAGE, e);
        }
    }
}

