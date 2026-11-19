package co.booker.utils;

/** Constantes compartidas del proyecto. No instanciable. */
public class Constantes {

    /**
     * Clave con la que el actor guarda y recupera el token de autenticación
     * ({@code actor.remember(TOKEN, ...)} / {@code actor.recall(TOKEN)}).
     */
    public static final String TOKEN = "token";

    /**
     * Clave con la que el actor guarda el {@code bookingid} de la reserva que creó
     * ({@link co.booker.tasks.booking.CrearBooking}), para reutilizarlo en consultas,
     * actualizaciones y borrados del mismo escenario.
     */
    public static final String BOOKING_ID = "booking id";

    /** Clave con la que el actor guarda la {@link co.booker.models.Booking} que creó. */
    public static final String RESERVA_CREADA = "reserva creada";

    /**
     * Clave con la que el actor guarda la última {@link co.booker.models.Booking} que envió en un
     * POST, PUT o PATCH, para compararla con la respuesta.
     */
    public static final String RESERVA_ENVIADA = "reserva enviada";

    private Constantes() {
        throw new IllegalStateException("No se debe instanciar la clase");
    }
}
