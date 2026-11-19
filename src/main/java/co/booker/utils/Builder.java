package co.booker.utils;

/**
 * Contrato del patrón Builder usado por los builders de modelos.
 *
 * @param <T> tipo del objeto que se construye
 */
public interface Builder <T>{
    /**
     * Construye el objeto.
     *
     * @return nueva instancia de {@code T}
     */
    T build();
}