package co.booker.stepdefinitions;

import co.booker.questions.TiempoRespuestaHttp;
import co.booker.tasks.Ping;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.lessThan;

/** Step definitions del módulo ping y del paso común de tiempo de respuesta. */
public class PingDefinition {

    /** Llama al health check de la API. */
    @Cuando("el realiza el llamado al endpoint de HealthCheck")
    public void elRealizaElLlamadoAlEndpointDeHealthCheck() {
        theActorInTheSpotlight().attemptsTo(Ping.delServicio());
    }


    /**
     * Verifica que la última respuesta tardó menos del límite dado.
     *
     * @param time límite en milisegundos (exclusivo)
     */
    @Entonces("el deberia ver un tiempo menor a {int}")
    public void elDeberiaVerUnTiempoMenorA(Integer time) {
        theActorInTheSpotlight().should(
                seeThat("El tiempo de respuesta", TiempoRespuestaHttp.fue(), lessThan(time.longValue()))
        );
    }
}