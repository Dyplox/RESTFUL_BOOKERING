package co.booker.stepdefinitions;

import co.booker.questions.TiempoRespuestaHttp;
import co.booker.tasks.Ping;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Entonces;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;
import static org.hamcrest.Matchers.lessThan;

public class PingDefinition {

    @Cuando("el realiza el llamado al endpoint de HealthCheck")
    public void elRealizaElLlamadoAlEndpointDeHealthCheck() {
        theActorInTheSpotlight().attemptsTo(Ping.delServicio());
    }


    @Entonces("el deberia ver un tiempo menor a {int}")
    public void elDeberiaVerUnTiempoMenorA(Integer time) {
        theActorInTheSpotlight().should(
                seeThat("El tiempo de respuesta", TiempoRespuestaHttp.fue(), lessThan(time.longValue()))
        );
    }
}