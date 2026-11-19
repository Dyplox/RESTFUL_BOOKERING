package co.booker.stepdefinitions;

import co.booker.tasks.Ping;
import io.cucumber.java.es.Cuando;

import static net.serenitybdd.screenplay.actors.OnStage.theActorInTheSpotlight;

/** Step definitions del módulo ping (health check). */
public class PingDefinition {

    /** Llama al health check de la API ({@code GET /ping}). */
    @Cuando("el consulta el health check")
    public void elConsultaElHealthCheck() {
        theActorInTheSpotlight().attemptsTo(Ping.delServicio());
    }
}
