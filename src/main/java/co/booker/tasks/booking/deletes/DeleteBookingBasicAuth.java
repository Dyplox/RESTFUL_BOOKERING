package co.booker.tasks.booking.deletes;

import co.booker.utils.Autenticacion;
import co.booker.utils.Configuracion;
import net.serenitybdd.annotations.Step;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Delete;

import static co.booker.utils.endpoints.uris.BOOKING;
import static net.serenitybdd.screenplay.Tasks.instrumented;

/**
 * Tarea que elimina una reserva autenticándose con Basic Auth ({@code DELETE /booking/{id}}).
 *
 * <p>La cabecera {@code Authorization: Basic ...} se genera en tiempo de ejecución a partir del
 * usuario y la contraseña. La API responde {@code 201} si la elimina, {@code 403} si las
 * credenciales son incorrectas y {@code 405} si la reserva no existe.</p>
 */
public class DeleteBookingBasicAuth implements Task {

    private final String id;
    private final String usuario;
    private final String clave;

    /**
     * Constructor usado por Serenity al instrumentar la tarea; usar las factorías estáticas.
     *
     * @param id      identificador de la reserva a eliminar
     * @param usuario usuario de Basic Auth
     * @param clave   contraseña de Basic Auth
     */
    public DeleteBookingBasicAuth(String id, String usuario, String clave) {
        this.id = id;
        this.usuario = usuario;
        this.clave = clave;
    }

    /**
     * Envía la petición de borrado en nombre del actor.
     *
     * @param actor actor con la habilidad {@code CallAnApi}
     * @param <T>   tipo del actor
     */
    @Override
    @Step("{0} elimina la reserva #id con Basic Auth (usuario #usuario)")
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(Delete.from(BOOKING + id)
                .with(peticion -> peticion.header("Authorization", Autenticacion.basic(usuario, clave))));
    }

    /**
     * Crea la tarea con las credenciales del entorno activo ({@link Configuracion}).
     *
     * @param id identificador de la reserva a eliminar
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static DeleteBookingBasicAuth delServicio(String id) {
        return conCredenciales(id, Configuracion.usuario(), Configuracion.clave());
    }

    /**
     * Crea la tarea con credenciales a medida.
     *
     * @param id      identificador de la reserva a eliminar
     * @param usuario usuario de Basic Auth
     * @param clave   contraseña de Basic Auth
     * @return tarea lista para {@code actor.attemptsTo(...)}
     */
    public static DeleteBookingBasicAuth conCredenciales(String id, String usuario, String clave) {
        return instrumented(DeleteBookingBasicAuth.class, id, usuario, clave);
    }
}
