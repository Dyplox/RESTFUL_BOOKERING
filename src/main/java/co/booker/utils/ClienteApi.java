package co.booker.utils;

import io.restassured.builder.RequestSpecBuilder;
import net.serenitybdd.rest.SerenityRest;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Configuración global del cliente HTTP, aplicada una sola vez por ejecución.
 *
 * <ul>
 *   <li><b>Especificación base</b>: {@code Content-Type} y {@code Accept} JSON y validación HTTPS
 *       relajada. Las tareas solo indican lo que cambia (cookie, body, otro {@code Accept}…).</li>
 *   <li><b>{@link ReintentoInfraestructura}</b>: 3 intentos con espera exponencial desde 500 ms.</li>
 * </ul>
 */
public final class ClienteApi {

    private static final String JSON = "application/json";
    private static final AtomicBoolean CONFIGURADO = new AtomicBoolean(false);

    private ClienteApi() {
        throw new IllegalStateException("No se debe instanciar la clase");
    }

    /** Aplica la configuración global; las llamadas siguientes no hacen nada. */
    public static void configurar() {
        if (!CONFIGURADO.compareAndSet(false, true)) {
            return;
        }
        SerenityRest.setDefaultRequestSpecification(new RequestSpecBuilder()
                .setContentType(JSON)
                // Valor literal: ContentType.JSON enviaría "application/json, application/javascript,
                // text/javascript, text/json" y Restful Booker responde 418 a cualquier Accept no exacto.
                .setAccept(JSON)
                .setRelaxedHTTPSValidation()
                .build());
        SerenityRest.filters(new ReintentoInfraestructura(3, 500));
    }
}
