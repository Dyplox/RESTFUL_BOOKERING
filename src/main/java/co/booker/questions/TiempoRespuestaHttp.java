package co.booker.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

public class TiempoRespuestaHttp implements Question<Long> {

    public static Question<Long> fue() {
        return new TiempoRespuestaHttp();
    }

    @Override
    public Long answeredBy(Actor actor) {
        return SerenityRest.lastResponse().getTimeIn(MILLISECONDS);
    }
}