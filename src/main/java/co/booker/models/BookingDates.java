package co.booker.models;

import java.util.Date;

/** Fechas de entrada ({@code checkin}) y salida ({@code checkout}) de una reserva. */
public class BookingDates {

    private Date checkin;
    private Date checkout;

    /**
     * Crea el rango de fechas.
     *
     * @param checkin  fecha de entrada
     * @param checkout fecha de salida
     */
    public BookingDates(Date checkin, Date checkout) {
        this.checkin = checkin;
        this.checkout = checkout;
    }

    public Date getCheckin() {
        return checkin;
    }

    public void setCheckin(Date checkin) {
        this.checkin = checkin;
    }

    public Date getCheckout() {
        return checkout;
    }

    public void setCheckout(Date checkout) {
        this.checkout = checkout;
    }
}
