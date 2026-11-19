package co.booker.utils;

import net.serenitybdd.model.environment.EnvironmentSpecificConfiguration;
import net.thucydides.model.environment.SystemEnvironmentVariables;

/**
 * Acceso a la configuración por entorno de {@code serenity.conf}.
 *
 * <p>El entorno se elige con la propiedad de sistema {@code environment} (desde Gradle:
 * {@code -Penv=docker}); sin ella se usa el bloque {@code default}. Las credenciales pueden
 * sobrescribirse con las variables de entorno {@code RESTFUL_BOOKER_USUARIO} y
 * {@code RESTFUL_BOOKER_CLAVE}, que se leen aquí porque Serenity no resuelve {@code ${?VAR}}.</p>
 */
public final class Configuracion {

    private Configuracion() {
        throw new IllegalStateException("No se debe instanciar la clase");
    }

    /** @return URL base de la API del entorno activo, terminada en {@code /} */
    public static String urlBase() {
        String url = obligatoria("restapi.baseurl");
        return url.endsWith("/") ? url : url + "/";
    }

    /** @return usuario de la API: variable {@code RESTFUL_BOOKER_USUARIO} o, si no existe, la configuración */
    public static String usuario() {
        return variableDeEntorno("RESTFUL_BOOKER_USUARIO", "restapi.usuario");
    }

    /** @return contraseña de la API: variable {@code RESTFUL_BOOKER_CLAVE} o, si no existe, la configuración */
    public static String clave() {
        return variableDeEntorno("RESTFUL_BOOKER_CLAVE", "restapi.clave");
    }

    private static String variableDeEntorno(String variable, String propiedad) {
        String valor = System.getenv(variable);
        return valor != null && !valor.isBlank() ? valor : obligatoria(propiedad);
    }

    private static String obligatoria(String propiedad) {
        return EnvironmentSpecificConfiguration
                .from(SystemEnvironmentVariables.currentEnvironmentVariables())
                .getOptionalProperty(propiedad)
                .orElseThrow(() -> new IllegalStateException(
                        "Falta la propiedad '" + propiedad + "' en serenity.conf para el entorno activo"));
    }
}
