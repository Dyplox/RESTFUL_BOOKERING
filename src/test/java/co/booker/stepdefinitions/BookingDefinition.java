package co.booker.stepdefinitions;

import co.booker.tasks.booking.CrearBooking;
import co.booker.tasks.booking.deletes.DeleteBookingBasicAuth;
import co.booker.tasks.booking.deletes.DeleteBookingByCookie;
import co.booker.tasks.booking.gets.BookingByCheck;
import co.booker.tasks.booking.gets.BookingByID;
import co.booker.tasks.booking.gets.BookingByName;
import co.booker.tasks.booking.gets.BookingIds;
import co.booker.tasks.booking.updates.PartialUpdateBooking;
import co.booker.tasks.booking.updates.UpdateBooking;
import io.cucumber.java.es.Cuando;

import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;

/** Step definitions del módulo booking: consultas, creación, actualización y borrado de reservas. */
public class BookingDefinition {

    //Gests
    /** Consulta la lista de todas las reservas. */
    @Cuando("el llama el servicio para ver la lista de bookings")
    public void elLlamaElServicioParaVerLaListaDeBookings() {
        theActorInTheSpotlight().attemptsTo(BookingIds.delServicio());
    }

    /**
     * Consulta las reservas filtradas por nombre y apellido.
     *
     * @param nombre   nombre del huésped
     * @param apellido apellido del huésped
     */
    @Cuando("el llama el servicio para ver la lista de bookings con nombre {string} y {string}")
    public void elLlamaElServicioParaVerLaListaDeBookingsConNombreY(String nombre, String apellido) {
        theActorInTheSpotlight().attemptsTo(BookingByName.delServicio(nombre, apellido));
    }

    /**
     * Consulta las reservas filtradas por fechas.
     *
     * @param checkIn  fecha de entrada, formato {@code yyyy-MM-dd}
     * @param checkOut fecha de salida, formato {@code yyyy-MM-dd}
     */
    @Cuando("el llama el servicio para ver la lista de bookings con Check {string} y {string}")
    public void elLlamaElServicioParaVerLaListaDeBookingsConCheckY(String checkIn, String checkOut) {
        theActorInTheSpotlight().attemptsTo(BookingByCheck.delServicio(checkIn, checkOut));
    }

    /**
     * Consulta una reserva por id.
     *
     * @param id identificador de la reserva
     */
    @Cuando("el llama el servicio para ver el bookin con id {int}")
    public void elLlamaElServicioParaVerElBookinConId(Integer id) {
        theActorInTheSpotlight().attemptsTo(BookingByID.delServicio(id.toString()));
    }

    //Post
    /** Crea una reserva con datos fijos. */
    @Cuando("el llama el servicio para crear el bookin")
    public void elLlamaElServicioParaCrearElBookin() {
        theActorInTheSpotlight().attemptsTo(CrearBooking.usuario());
    }

    //PUT
    /**
     * Reemplaza una reserva (PUT); requiere token guardado.
     *
     * @param id identificador de la reserva
     */
    @Cuando("el llama el servicio para actualizar el bookin {int}")
    public void elLlamaElServicioParaActualizarElBookin(Integer id) {
        theActorInTheSpotlight().attemptsTo(UpdateBooking.usuario(id));
    }

    //PATH
    /**
     * Actualiza parcialmente una reserva (PATCH); requiere token guardado.
     *
     * @param id identificador de la reserva
     */
    @Cuando("el llama el servicio para actualizar parcialmente el bookin {int}")
    public void elLlamaElServicioParaActualizarParcialmenteElBookin(Integer id) {
        theActorInTheSpotlight().attemptsTo(PartialUpdateBooking.usuario(id));
    }

    //DELETE
    /**
     * Elimina una reserva autenticándose por cookie; requiere token guardado.
     *
     * @param id identificador de la reserva
     */
    @Cuando("el llama el servicio para eliminar mediante cookie el bookin {int}")
    public void elLlamaElServicioParaEliminarMedianteCookieElBookin(Integer id) {
        theActorInTheSpotlight().attemptsTo(DeleteBookingByCookie.delServicio(id.toString()));
    }

    /**
     * Elimina una reserva autenticándose con Basic Auth.
     *
     * @param id identificador de la reserva
     */
    @Cuando("el llama el servicio para eliminar mediante BasicAuth el bookin {int}")
    public void elLlamaElServicioParaEliminarMedianteBasicAuthElBookin(Integer id) {
        theActorInTheSpotlight().attemptsTo(DeleteBookingBasicAuth.delServicio(id.toString()));
    }

}