package co.booker.utils.endpoints;

/**
 * Rutas de la API, relativas a {@code restapi.baseurl}.
 *
 * <p>{@link #BOOKING} termina en {@code /} para concatenarle directamente el id o los query params.</p>
 */
public enum uris {

    /** Autenticación: {@code POST /auth}. */
    AUTH("auth"),
    /** Reservas: {@code /booking/}, {@code /booking/{id}}, {@code /booking/?filtros}. */
    BOOKING("booking/"),
    /** Health check: {@code GET /ping}. */
    PING("ping");

    private final String endpoint;

    uris(String endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * @return la ruta del endpoint
     */
    @Override
    public String toString() {
        return endpoint;
    }
}