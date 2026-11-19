package co.booker.models;

import java.time.LocalDate;

/**
 * Fechas de una reserva, en formato {@code yyyy-MM-dd} como las devuelve la API.
 *
 * @param checkin  fecha de entrada
 * @param checkout fecha de salida
 */
public record BookingDates(String checkin, String checkout) {

    /**
     * Crea las fechas a partir de {@link LocalDate}.
     *
     * @param checkin  fecha de entrada
     * @param checkout fecha de salida
     * @return fechas en formato ISO
     */
    public static BookingDates de(LocalDate checkin, LocalDate checkout) {
        return new BookingDates(checkin.toString(), checkout.toString());
    }

    /** @return la fecha de entrada como {@link LocalDate} */
    public LocalDate entrada() {
        return LocalDate.parse(checkin);
    }

    /** @return la fecha de salida como {@link LocalDate} */
    public LocalDate salida() {
        return LocalDate.parse(checkout);
    }
}
