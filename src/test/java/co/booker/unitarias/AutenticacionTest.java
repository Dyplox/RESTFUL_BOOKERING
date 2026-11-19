package co.booker.unitarias;

import co.booker.utils.Autenticacion;
import io.restassured.RestAssured;
import io.restassured.specification.QueryableRequestSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.SpecificationQuerier;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Pruebas unitarias de {@link Autenticacion}. */
class AutenticacionTest {

    @Test
    void basicCodificaUsuarioYClaveEnBase64() {
        assertEquals("Basic YWRtaW46cGFzc3dvcmQxMjM=", Autenticacion.basic("admin", "password123"));
    }

    @Test
    void basicSoportaCaracteresEspecialesYUtf8() {
        String cabecera = Autenticacion.basic("señor", "c:la$ve");
        String decodificado = new String(
                Base64.getDecoder().decode(cabecera.substring("Basic ".length())), StandardCharsets.UTF_8);
        assertEquals("señor:c:la$ve", decodificado);
    }

    @Test
    void conTokenAnadeLaCookieCuandoHayToken() {
        QueryableRequestSpecification spec = consultar(Autenticacion.conToken(RestAssured.given(), "abc123"));
        assertTrue(spec.getCookies().hasCookieWithName("token"));
        assertEquals("abc123", spec.getCookies().getValue("token"));
    }

    @Test
    void conTokenNoAnadeCookieCuandoNoHayToken() {
        QueryableRequestSpecification spec = consultar(Autenticacion.conToken(RestAssured.given(), null));
        assertFalse(spec.getCookies().hasCookieWithName("token"));
    }

    private static QueryableRequestSpecification consultar(RequestSpecification spec) {
        return SpecificationQuerier.query(spec);
    }
}
