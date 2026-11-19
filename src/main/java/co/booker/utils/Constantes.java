package co.booker.utils;

/** Constantes compartidas del proyecto. No instanciable. */
public class Constantes {

    /**
     * Clave con la que el actor guarda y recupera el token de autenticación
     * ({@code actor.remember(TOKEN, ...)} / {@code actor.recall(TOKEN)}).
     */
    public static final String TOKEN = "token vacio";

    /** Mensaje base de {@link co.booker.exception.ThreadStoppedException}. */
    public static final String THREAD_STOPPED_EXCEPTION_MESSAGE = "Esta tarea se detuvo inesperadamente: ";

    private Constantes() {
        throw new IllegalStateException("No se debe instanciar la clase");
    }
}
