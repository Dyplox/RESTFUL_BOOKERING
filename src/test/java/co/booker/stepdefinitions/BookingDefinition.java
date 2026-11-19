package co.booker.stepdefinitions;

import co.booker.questions.CodigoRespuestaHttp;
import co.booker.tasks.booking.CrearBooking;
import co.booker.tasks.booking.EnviarBooking;
import co.booker.tasks.booking.deletes.DeleteBookingBasicAuth;
import co.booker.tasks.booking.deletes.DeleteBookingByCookie;
import co.booker.tasks.booking.gets.BookingByCheck;
import co.booker.tasks.booking.gets.BookingByID;
import co.booker.tasks.booking.gets.BookingByName;
import co.booker.tasks.booking.gets.BookingEnFormato;
import co.booker.tasks.booking.gets.BookingIds;
import co.booker.tasks.booking.updates.PartialUpdateBooking;
import co.booker.tasks.booking.updates.UpdateBooking;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;

import static co.booker.utils.Constantes.BOOKING_ID;
import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;

/**
 * Step definitions del módulo booking: consultas, creación, actualización y borrado de reservas.
 *
 * <p>Cada escenario trabaja sobre una reserva que crea él mismo ({@code el tiene una reserva creada});
 * su {@code bookingid} se guarda en el actor bajo {@link co.booker.utils.Constantes#BOOKING_ID} y
 * {@link ComunDefinition#limpiarReservaCreada()} la elimina al terminar.</p>
 */
public class BookingDefinition {

    // Precondiciones

    /** Crea una reserva de prueba, verifica que la API respondió 200 y guarda su id en el actor. */
    @Dado("el tiene una reserva creada")
    public void elTieneUnaReservaCreada() {
        Actor actor = theActorInTheSpotlight();
        actor.attemptsTo(CrearBooking.usuario());
        actor.should(seeThat("el código al crear la reserva", CodigoRespuestaHttp.fue(), equalTo(200)));
    }

    // GET

    /** Consulta la lista de todas las reservas. */
    @Cuando("el consulta la lista de reservas")
    public void elConsultaLaListaDeReservas() {
        theActorInTheSpotlight().attemptsTo(BookingIds.delServicio());
    }

    /**
     * Consulta las reservas filtradas por nombre y apellido.
     *
     * @param nombre   nombre del huésped
     * @param apellido apellido del huésped
     */
    @Cuando("el consulta las reservas con nombre {string} y apellido {string}")
    public void elConsultaLasReservasConNombreYApellido(String nombre, String apellido) {
        theActorInTheSpotlight().attemptsTo(BookingByName.delServicio(nombre, apellido));
    }

    /**
     * Consulta las reservas filtradas por fechas.
     *
     * @param checkIn  fecha de entrada, formato {@code yyyy-MM-dd}
     * @param checkOut fecha de salida, formato {@code yyyy-MM-dd}
     */
    @Cuando("el consulta las reservas con check-in {string} y check-out {string}")
    public void elConsultaLasReservasConCheckInYCheckOut(String checkIn, String checkOut) {
        theActorInTheSpotlight().attemptsTo(BookingByCheck.delServicio(checkIn, checkOut));
    }

    /** Consulta la reserva creada en el escenario. */
    @Cuando("el consulta la reserva creada")
    public void elConsultaLaReservaCreada() {
        theActorInTheSpotlight().attemptsTo(BookingByID.delServicio(idReservaCreada().toString()));
    }

    /**
     * Consulta la reserva creada pidiendo un formato con la cabecera {@code Accept}.
     *
     * @param accept valor de la cabecera {@code Accept} (p. ej. {@code application/xml})
     */
    @Cuando("el consulta la reserva creada en formato {string}")
    public void elConsultaLaReservaCreadaEnFormato(String accept) {
        theActorInTheSpotlight().attemptsTo(BookingEnFormato.delServicio(idReservaCreada().toString(), accept));
    }

    /**
     * Consulta una reserva por un id arbitrario (p. ej. uno que no existe).
     *
     * @param id identificador a consultar
     */
    @Cuando("el consulta la reserva con id {string}")
    public void elConsultaLaReservaConId(String id) {
        theActorInTheSpotlight().attemptsTo(BookingByID.delServicio(id));
    }

    // POST

    /** Crea una reserva con datos fijos (sin verificar la respuesta, para que lo haga el escenario). */
    @Cuando("el crea una reserva")
    public void elCreaUnaReserva() {
        theActorInTheSpotlight().attemptsTo(CrearBooking.usuario());
    }

    /**
     * Envía a {@code POST /booking} el body indicado, tal cual, con cabeceras JSON.
     *
     * @param body body de la petición (DocString del escenario)
     */
    @Cuando("el envía la siguiente reserva:")
    public void elEnviaLaSiguienteReserva(String body) {
        theActorInTheSpotlight().attemptsTo(EnviarBooking.conBody(body));
    }

    /**
     * Envía a {@code POST /booking} el body indicado con un {@code Content-Type} a medida.
     *
     * @param contentType valor de la cabecera {@code Content-Type}
     * @param body        body de la petición (DocString del escenario)
     */
    @Cuando("el envía la siguiente reserva con Content-Type {string}:")
    public void elEnviaLaSiguienteReservaConContentType(String contentType, String body) {
        theActorInTheSpotlight().attemptsTo(EnviarBooking.conBody(body, contentType, "application/json"));
    }

    /**
     * Envía a {@code POST /booking} el body indicado pidiendo la respuesta en otro formato.
     *
     * @param accept valor de la cabecera {@code Accept}
     * @param body   body de la petición (DocString del escenario)
     */
    @Cuando("el envía la siguiente reserva aceptando {string}:")
    public void elEnviaLaSiguienteReservaAceptando(String accept, String body) {
        theActorInTheSpotlight().attemptsTo(EnviarBooking.conBody(body, "application/json", accept));
    }

    // PUT / PATCH

    /** Reemplaza la reserva creada (PUT); envía el token del actor si lo tiene. */
    @Cuando("el actualiza la reserva creada")
    public void elActualizaLaReservaCreada() {
        theActorInTheSpotlight().attemptsTo(UpdateBooking.usuario(idReservaCreada()));
    }

    /** Actualiza parcialmente la reserva creada (PATCH); envía el token del actor si lo tiene. */
    @Cuando("el actualiza parcialmente la reserva creada")
    public void elActualizaParcialmenteLaReservaCreada() {
        theActorInTheSpotlight().attemptsTo(PartialUpdateBooking.usuario(idReservaCreada()));
    }

    // DELETE

    /** Elimina la reserva creada autenticándose por cookie; envía el token del actor si lo tiene. */
    @Cuando("el elimina la reserva creada mediante cookie")
    public void elEliminaLaReservaCreadaMedianteCookie() {
        theActorInTheSpotlight().attemptsTo(DeleteBookingByCookie.delServicio(idReservaCreada().toString()));
    }

    /** Elimina la reserva creada con Basic Auth y las credenciales públicas de la API. */
    @Cuando("el elimina la reserva creada mediante Basic Auth")
    public void elEliminaLaReservaCreadaMedianteBasicAuth() {
        theActorInTheSpotlight().attemptsTo(DeleteBookingBasicAuth.delServicio(idReservaCreada().toString()));
    }

    /**
     * Intenta eliminar la reserva creada con Basic Auth y credenciales a medida.
     *
     * @param usuario usuario de Basic Auth
     * @param clave   contraseña de Basic Auth
     */
    @Cuando("el elimina la reserva creada mediante Basic Auth con usuario {string} y contraseña {string}")
    public void elEliminaLaReservaCreadaMedianteBasicAuthCon(String usuario, String clave) {
        theActorInTheSpotlight().attemptsTo(
                DeleteBookingBasicAuth.conCredenciales(idReservaCreada().toString(), usuario, clave));
    }

    // Validaciones

    /** Verifica que la reserva creada ya no existe: un GET por su id debe responder 404. */
    @Entonces("la reserva creada ya no debería existir")
    public void laReservaCreadaYaNoDeberiaExistir() {
        verificarCodigoAlConsultarReservaCreada(404);
    }

    /** Verifica que la reserva creada sigue existiendo: un GET por su id debe responder 200. */
    @Entonces("la reserva creada debería seguir existiendo")
    public void laReservaCreadaDeberiaSeguirExistiendo() {
        verificarCodigoAlConsultarReservaCreada(200);
    }

    /** Verifica que la lista de reservas de la última respuesta incluye la reserva creada. */
    @Entonces("la lista debería contener la reserva creada")
    public void laListaDeberiaContenerLaReservaCreada() {
        assertThat("ids de la lista de reservas", idsDeLaLista(), hasItem(idReservaCreada()));
    }

    /** Verifica que la lista de reservas de la última respuesta está vacía. */
    @Entonces("la lista debería estar vacía")
    public void laListaDeberiaEstarVacia() {
        assertThat("ids de la lista de reservas", idsDeLaLista(), empty());
    }

    private static void verificarCodigoAlConsultarReservaCreada(int codigo) {
        Actor actor = theActorInTheSpotlight();
        actor.attemptsTo(BookingByID.delServicio(idReservaCreada().toString()));
        actor.should(seeThat("el código al consultar la reserva creada", CodigoRespuestaHttp.fue(), equalTo(codigo)));
    }

    private static java.util.List<Integer> idsDeLaLista() {
        return SerenityRest.lastResponse().jsonPath().getList("bookingid", Integer.class);
    }

    private static Integer idReservaCreada() {
        Integer id = theActorInTheSpotlight().recall(BOOKING_ID);
        if (id == null) {
            throw new IllegalStateException("No hay reserva creada en este escenario: falta el paso 'el tiene una reserva creada'");
        }
        return id;
    }
}
