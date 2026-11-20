package co.booker.stepdefinitions;

import co.booker.models.Booking;
import co.booker.models.ReservaFactory;
import co.booker.questions.CodigoRespuestaHttp;
import co.booker.questions.CuerpoRespuesta;
import co.booker.questions.IdsDeReservas;
import co.booker.questions.ReservaRespuesta;
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
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.ensure.Ensure;

import static co.booker.utils.Constantes.BOOKING_ID;
import static co.booker.utils.Constantes.RESERVA_CREADA;
import static co.booker.utils.Constantes.RESERVA_ENVIADA;
import static co.booker.utils.Json.aJson;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;

/**
 * Step definitions del módulo booking: consultas, creación, actualización y borrado de reservas.
 *
 * <p>Cada escenario trabaja sobre una reserva que crea él mismo ({@code el tiene una reserva creada});
 * su {@code bookingid} se guarda en el actor bajo {@link co.booker.utils.Constantes#BOOKING_ID} y
 * {@link ComunDefinition#limpiarReservaCreada()} la elimina al terminar.</p>
 */
public class BookingDefinition {

    // Precondiciones

    /**
     * Crea una reserva con datos únicos ({@code ReservaFactory.valida()}), verifica que la API
     * respondió 200 y guarda en el actor su id y sus datos.
     */
    @Dado("el tiene una reserva creada")
    public void elTieneUnaReservaCreada() {
        Actor actor = theActorInTheSpotlight();
        actor.attemptsTo(CrearBooking.valida());
        actor.attemptsTo(Ensure.that("el código al crear la reserva", CodigoRespuestaHttp.fue()).isEqualTo(200));
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

    /** Busca reservas por el nombre y el apellido (único) de la reserva creada. */
    @Cuando("el consulta las reservas con el nombre de la reserva creada")
    public void elConsultaLasReservasConElNombreDeLaReservaCreada() {
        Booking creada = reservaCreada();
        theActorInTheSpotlight().attemptsTo(BookingByName.delServicio(creada.firstname(), creada.lastname()));
    }

    /**
     * Busca reservas con un rango de fechas que contiene a la reserva creada (un día antes de su
     * entrada y un día después de su salida, porque los filtros de la API son estrictos).
     */
    @Cuando("el consulta las reservas con fechas que rodean la reserva creada")
    public void elConsultaLasReservasConFechasQueRodeanLaReservaCreada() {
        Booking creada = reservaCreada();
        theActorInTheSpotlight().attemptsTo(BookingByCheck.delServicio(
                creada.bookingdates().entrada().minusDays(1).toString(),
                creada.bookingdates().salida().plusDays(1).toString()));
    }

    /**
     * Busca reservas que entran después de la entrada de la reserva creada: el filtro es estricto,
     * así que la reserva creada no debe aparecer.
     */
    @Cuando("el consulta las reservas con entrada posterior a la de la reserva creada")
    public void elConsultaLasReservasConEntradaPosteriorALaDeLaReservaCreada() {
        Booking creada = reservaCreada();
        theActorInTheSpotlight().attemptsTo(BookingByCheck.delServicio(
                creada.bookingdates().entrada().toString(),
                creada.bookingdates().salida().plusDays(1).toString()));
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

    /** Crea una reserva con datos únicos (sin verificar la respuesta, para que lo haga el escenario). */
    @Cuando("el crea una reserva")
    public void elCreaUnaReserva() {
        theActorInTheSpotlight().attemptsTo(CrearBooking.valida());
    }

    /**
     * Crea una reserva con datos únicos pidiendo la respuesta en otro formato (cabecera {@code Accept}).
     *
     * @param accept valor de la cabecera {@code Accept}, p. ej. {@code application/xml}
     */
    @Cuando("el crea una reserva pidiendo la respuesta en {string}")
    public void elCreaUnaReservaPidiendoLaRespuestaEn(String accept) {
        theActorInTheSpotlight().attemptsTo(EnviarBooking.conBody(aJson(ReservaFactory.valida()), "application/json", accept));
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

    /** Reemplaza la reserva creada (PUT) con datos nuevos; envía el token del actor si lo tiene. */
    @Cuando("el actualiza la reserva creada")
    public void elActualizaLaReservaCreada() {
        theActorInTheSpotlight().attemptsTo(UpdateBooking.usuario(idReservaCreada()));
    }

    /** Cambia nombre y apellido de la reserva creada (PATCH); envía el token del actor si lo tiene. */
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
        theActorInTheSpotlight().attemptsTo(Ensure.that(IdsDeReservas.enLaRespuesta()).contains(idReservaCreada()));
    }

    /** Verifica que la lista de reservas de la última respuesta contiene únicamente la reserva creada. */
    @Entonces("la lista debería contener solo la reserva creada")
    public void laListaDeberiaContenerSoloLaReservaCreada() {
        theActorInTheSpotlight().attemptsTo(Ensure.that(IdsDeReservas.enLaRespuesta()).containsExactly(idReservaCreada()));
    }

    /** Verifica que la lista de reservas de la última respuesta no incluye la reserva creada. */
    @Entonces("la lista no debería contener la reserva creada")
    public void laListaNoDeberiaContenerLaReservaCreada() {
        theActorInTheSpotlight().attemptsTo(Ensure.that(IdsDeReservas.enLaRespuesta()).doesNotContain(idReservaCreada()));
    }

    /** Verifica que el body de la última respuesta es exactamente la reserva creada. */
    @Entonces("la respuesta debería coincidir con la reserva creada")
    public void laRespuestaDeberiaCoincidirConLaReservaCreada() {
        ensureReserva(ReservaRespuesta.completa(), reservaCreada());
    }

    /** Verifica que el body de la última respuesta es exactamente la reserva enviada (PUT). */
    @Entonces("la respuesta debería coincidir con la reserva enviada")
    public void laRespuestaDeberiaCoincidirConLaReservaEnviada() {
        ensureReserva(ReservaRespuesta.completa(), reservaEnviada());
    }

    /** Verifica que el campo {@code booking} de la respuesta de un POST es la reserva enviada. */
    @Entonces("la reserva de la respuesta debería coincidir con la enviada")
    public void laReservaDeLaRespuestaDeberiaCoincidirConLaEnviada() {
        ensureReserva(ReservaRespuesta.enElCampo("booking"), reservaEnviada());
    }

    /**
     * Verifica que la respuesta de un PATCH es la reserva creada con los cambios enviados aplicados
     * (los campos no enviados deben conservarse).
     */
    @Entonces("la respuesta debería tener los cambios enviados sobre la reserva creada")
    public void laRespuestaDeberiaTenerLosCambiosEnviadosSobreLaReservaCreada() {
        ensureReserva(ReservaRespuesta.completa(), reservaCreada().con(reservaEnviada()));
    }

    /** Vuelve a consultar la reserva creada y verifica que persistió con los datos enviados. */
    @Entonces("al consultarla de nuevo debería coincidir con la reserva enviada")
    public void alConsultarlaDeNuevoDeberiaCoincidirConLaReservaEnviada() {
        Actor actor = theActorInTheSpotlight();
        actor.attemptsTo(BookingByID.delServicio(idReservaCreada().toString()));
        actor.attemptsTo(Ensure.that("el código al volver a consultar", CodigoRespuestaHttp.fue()).isEqualTo(200));
        ensureReserva(ReservaRespuesta.completa(), reservaEnviada());
    }

    /** Verifica que el cuerpo (XML) de la última respuesta contiene el apellido único de la reserva enviada. */
    @Entonces("el cuerpo de la respuesta debería contener el apellido de la reserva enviada en XML")
    public void elCuerpoDeberiaContenerElApellidoDeLaReservaEnviadaEnXml() {
        theActorInTheSpotlight().attemptsTo(
                Ensure.that(CuerpoRespuesta.comoTexto()).contains("<lastname>" + reservaEnviada().lastname() + "</lastname>"));
    }

    /** Verifica que el cuerpo (XML) de la última respuesta contiene el apellido único de la reserva creada. */
    @Entonces("el cuerpo de la respuesta debería contener el apellido de la reserva creada en XML")
    public void elCuerpoDeberiaContenerElApellidoDeLaReservaCreadaEnXml() {
        theActorInTheSpotlight().attemptsTo(
                Ensure.that(CuerpoRespuesta.comoTexto()).contains("<lastname>" + reservaCreada().lastname() + "</lastname>"));
    }

    /** Verifica que la lista de reservas de la última respuesta está vacía. */
    @Entonces("la lista debería estar vacía")
    public void laListaDeberiaEstarVacia() {
        theActorInTheSpotlight().attemptsTo(Ensure.that(IdsDeReservas.enLaRespuesta()).isEmpty());
    }

    private static void verificarCodigoAlConsultarReservaCreada(int codigo) {
        Actor actor = theActorInTheSpotlight();
        actor.attemptsTo(BookingByID.delServicio(idReservaCreada().toString()));
        actor.attemptsTo(Ensure.that("el código al consultar la reserva creada", CodigoRespuestaHttp.fue()).isEqualTo(codigo));
    }

    /** Compara la reserva de la respuesta con la esperada como JSON canónico (el reporte muestra ambas). */
    private static void ensureReserva(ReservaRespuesta reserva, Booking esperada) {
        theActorInTheSpotlight().attemptsTo(Ensure.that(reserva).isEqualTo(aJson(esperada)));
    }

    private static Booking reservaCreada() {
        Booking reserva = theActorInTheSpotlight().recall(RESERVA_CREADA);
        if (reserva == null) {
            throw new IllegalStateException("No hay reserva creada en este escenario: falta el paso 'el tiene una reserva creada'");
        }
        return reserva;
    }

    private static Booking reservaEnviada() {
        Booking reserva = theActorInTheSpotlight().recall(RESERVA_ENVIADA);
        if (reserva == null) {
            throw new IllegalStateException("El actor no ha enviado ninguna reserva en este escenario");
        }
        return reserva;
    }

    private static Integer idReservaCreada() {
        Integer id = theActorInTheSpotlight().recall(BOOKING_ID);
        if (id == null) {
            throw new IllegalStateException("No hay reserva creada en este escenario: falta el paso 'el tiene una reserva creada'");
        }
        return id;
    }
}
