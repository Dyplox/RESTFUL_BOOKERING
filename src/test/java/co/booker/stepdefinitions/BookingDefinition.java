package co.booker.stepdefinitions;

import co.booker.tasks.booking.CrearBooking;
import co.booker.tasks.booking.gets.BookingByCheck;
import co.booker.tasks.booking.gets.BookingByID;
import co.booker.tasks.booking.gets.BookingByName;
import co.booker.tasks.booking.gets.BookingIds;
import co.booker.tasks.booking.updates.PartialUpdateBooking;
import co.booker.tasks.booking.updates.UpdateBooking;
import io.cucumber.java.es.Cuando;

import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;

public class BookingDefinition {

    //Gests
    @Cuando("el llama el servicio para ver la lista de bookings")
    public void elLlamaElServicioParaVerLaListaDeBookings() {
        theActorInTheSpotlight().attemptsTo(BookingIds.delServicio());
    }

    @Cuando("el llama el servicio para ver la lista de bookings con nombre {string} y {string}")
    public void elLlamaElServicioParaVerLaListaDeBookingsConNombreY(String nombre, String apellido) {
        theActorInTheSpotlight().attemptsTo(BookingByName.delServicio(nombre, apellido));
    }

    @Cuando("el llama el servicio para ver la lista de bookings con Check {string} y {string}")
    public void elLlamaElServicioParaVerLaListaDeBookingsConCheckY(String checkIn, String checkOut) {
        theActorInTheSpotlight().attemptsTo(BookingByCheck.delServicio(checkIn, checkOut));
    }

    @Cuando("el llama el servicio para ver el bookin con id {int}")
    public void elLlamaElServicioParaVerElBookinConId(Integer id) {
        theActorInTheSpotlight().attemptsTo(BookingByID.delServicio(id.toString()));
    }

    //Post
    @Cuando("el llama el servicio para crear el bookin")
    public void elLlamaElServicioParaCrearElBookin() {
        theActorInTheSpotlight().attemptsTo(CrearBooking.usuario());
    }

    //PUT
    @Cuando("el llama el servicio para actualizar el bookin {int}")
    public void elLlamaElServicioParaActualizarElBookin(Integer id) {
        theActorInTheSpotlight().attemptsTo(UpdateBooking.usuario(id));
    }

    //PATH
    @Cuando("el llama el servicio para actualizar parcialmente el bookin {int}")
    public void elLlamaElServicioParaActualizarParcialmenteElBookin(Integer id) {
        theActorInTheSpotlight().attemptsTo(PartialUpdateBooking.usuario(id));
    }

}