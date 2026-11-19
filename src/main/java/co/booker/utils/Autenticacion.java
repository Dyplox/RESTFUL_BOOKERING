package co.booker.utils;

import io.restassured.specification.RequestSpecification;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Utilidades para autenticar peticiones contra la API. No instanciable. */
public final class Autenticacion {

    private Autenticacion() {
        throw new IllegalStateException("No se debe instanciar la clase");
    }

    /**
     * Añade la cookie {@code token} a la petición solo si hay token.
     *
     * <p>Si el actor no se autenticó ({@code token == null}), la petición sale sin cookie; así los
     * escenarios negativos prueban de verdad una llamada sin credenciales.</p>
     *
     * @param spec  especificación de la petición
     * @param token token guardado por el actor, o {@code null}
     * @return la misma especificación, con la cookie si había token
     */
    public static RequestSpecification conToken(RequestSpecification spec, String token) {
        return token == null ? spec : spec.cookie("token", token);
    }

    /**
     * Construye el valor de la cabecera {@code Authorization} para Basic Auth.
     *
     * <p>Se genera a mano porque {@code auth().preemptive().basic(...)} de RestAssured no funciona
     * sobre la petición decorada de Serenity ({@code ClassCastException}).</p>
     *
     * @param usuario usuario
     * @param clave   contraseña
     * @return {@code "Basic " + base64(usuario:clave)}
     */
    public static String basic(String usuario, String clave) {
        String credenciales = usuario + ":" + clave;
        return "Basic " + Base64.getEncoder().encodeToString(credenciales.getBytes(StandardCharsets.UTF_8));
    }
}
