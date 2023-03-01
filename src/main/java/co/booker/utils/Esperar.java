package co.booker.utils;

import co.booker.exception.ThreadStoppedException;

import static co.booker.utils.Constantes.THREAD_STOPPED_EXCEPTION_MESSAGE;

public class Esperar {
    private Esperar() {
    }

    public static void stopExecution(int milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ThreadStoppedException(THREAD_STOPPED_EXCEPTION_MESSAGE, e);
        }
    }
}

