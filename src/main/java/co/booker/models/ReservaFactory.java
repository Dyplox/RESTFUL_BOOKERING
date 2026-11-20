package co.booker.models;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Fábrica de reservas de prueba.
 *
 * <p>Cada reserva lleva un apellido único y fechas propias, para que los filtros de la API la
 * encuentren solo a ella aunque haya otros escenarios (o usuarios de la API pública) creando datos
 * a la vez.</p>
 */
public final class ReservaFactory {

    private ReservaFactory() {
        throw new IllegalStateException("No se debe instanciar la clase");
    }

    /** @return una reserva válida con datos únicos */
    public static Booking valida() {
        LocalDate entrada = fechaFuturaAleatoria();
        return new Booking("Jim", apellidoUnico(), precioAleatorio(), true,
                BookingDates.de(entrada, entrada.plusDays(3)), "Breakfast");
    }

    /**
     * @return una reserva válida, con datos únicos y distintos en todos los campos a los de
     *         {@link #valida()}, para usar como body de un {@code PUT}
     */
    public static Booking actualizada() {
        LocalDate entrada = fechaFuturaAleatoria();
        return new Booking("Juan", apellidoUnico(), precioAleatorio() + 1000, false,
                BookingDates.de(entrada, entrada.plusDays(7)), "Lunch");
    }

    /** @return una reserva parcial (solo nombre y apellido), para usar como body de un {@code PATCH} */
    public static Booking cambioDeNombre() {
        return new Booking("Jose", apellidoUnico(), null, null, null, null);
    }

    private static String apellidoUnico() {
        return "Prueba-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private static int precioAleatorio() {
        return ThreadLocalRandom.current().nextInt(50, 1000);
    }

    private static LocalDate fechaFuturaAleatoria() {
        return LocalDate.now(ZoneOffset.UTC).plusYears(5).plusDays(ThreadLocalRandom.current().nextInt(0, 365));
    }
}
