package co.booker.unitarias;

import co.booker.utils.Configuracion;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/** Pruebas de {@link Configuracion} con el entorno por defecto de {@code serenity.conf}. */
class ConfiguracionTest {

    @Test
    void laUrlBaseDelEntornoPorDefectoEsLaApiPublicaYTerminaEnBarra() {
        assertEquals("https://restful-booker.herokuapp.com/", Configuracion.urlBase());
    }

    @Test
    void lasCredencialesSeLeenDeLaConfiguracion() {
        String usuarioEsperado = System.getenv().getOrDefault("RESTFUL_BOOKER_USUARIO", "admin");
        assertEquals(usuarioEsperado, Configuracion.usuario());
        assertFalse(Configuracion.clave().isBlank());
        assertFalse(Configuracion.clave().startsWith("#"), "la clave no debe ser un valor HOCON sin resolver");
    }
}
