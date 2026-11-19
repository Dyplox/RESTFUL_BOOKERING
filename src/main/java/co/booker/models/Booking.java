package co.booker.models;

/**
 * Reserva de Restful Booker, con los mismos nombres de campo que el JSON de la API.
 *
 * <p>Se serializa con {@link co.booker.utils.Json}. Los campos {@code null} se omiten, así que una
 * reserva con solo algunos campos sirve como body de un {@code PATCH}.</p>
 *
 * @param firstname       nombre del huésped
 * @param lastname        apellido del huésped
 * @param totalprice      precio total
 * @param depositpaid     si se pagó el depósito
 * @param bookingdates    fechas de entrada y salida
 * @param additionalneeds necesidades adicionales (opcional)
 */
public record Booking(String firstname, String lastname, Integer totalprice, Boolean depositpaid,
                      BookingDates bookingdates, String additionalneeds) {

    /**
     * Devuelve una copia de esta reserva con los campos no nulos de {@code cambios} aplicados,
     * igual que hace la API con un {@code PATCH}.
     *
     * @param cambios reserva parcial
     * @return reserva resultante
     */
    public Booking con(Booking cambios) {
        return new Booking(
                cambios.firstname() != null ? cambios.firstname() : firstname,
                cambios.lastname() != null ? cambios.lastname() : lastname,
                cambios.totalprice() != null ? cambios.totalprice() : totalprice,
                cambios.depositpaid() != null ? cambios.depositpaid() : depositpaid,
                cambios.bookingdates() != null ? cambios.bookingdates() : bookingdates,
                cambios.additionalneeds() != null ? cambios.additionalneeds() : additionalneeds);
    }
}
