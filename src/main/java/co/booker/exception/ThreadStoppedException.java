package co.booker.exception;

/** Se lanza cuando una espera de {@link co.booker.utils.Esperar} es interrumpida. */
public class ThreadStoppedException extends RuntimeException{
    /**
     * Crea la excepción con su mensaje y causa.
     *
     * @param message descripción del error
     * @param cause   la {@link InterruptedException} original
     */
    public ThreadStoppedException(String message, Throwable cause) {
        super(message, cause);
    }
}
