package co.booker.unitarias;

import co.booker.utils.ReintentoInfraestructura;
import com.sun.net.httpserver.HttpServer;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Pruebas de {@link ReintentoInfraestructura} contra un servidor HTTP local que devuelve una
 * secuencia de códigos programada.
 */
class ReintentoInfraestructuraTest {

    private HttpServer servidor;
    private final Deque<Integer> codigos = new ArrayDeque<>();
    private final AtomicInteger llamadas = new AtomicInteger();
    private final List<String> peticionesRecibidas = new java.util.concurrent.CopyOnWriteArrayList<>();

    @BeforeEach
    void arrancarServidor() throws IOException {
        servidor = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        servidor.createContext("/", intercambio -> {
            llamadas.incrementAndGet();
            peticionesRecibidas.add(intercambio.getRequestMethod() + " " + intercambio.getRequestURI()
                    + " cookie=" + intercambio.getRequestHeaders().getFirst("Cookie")
                    + " tipo=" + intercambio.getRequestHeaders().getFirst("Content-Type")
                    + " body=" + new String(intercambio.getRequestBody().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8));
            int codigo = codigos.size() > 1 ? codigos.poll() : codigos.peek();
            intercambio.sendResponseHeaders(codigo, -1);
            intercambio.close();
        });
        servidor.start();
    }

    @AfterEach
    void pararServidor() {
        servidor.stop(0);
    }

    private void programar(Integer... secuencia) {
        codigos.addAll(List.of(secuencia));
    }

    private Response llamar(String metodo) {
        return RestAssured.given()
                .baseUri("http://localhost:" + servidor.getAddress().getPort())
                .filter(new ReintentoInfraestructura(3, 1))
                .request(metodo, "/recurso");
    }

    @Test
    void reintentaUnGetHastaQueDejaDeFallarLaInfraestructura() {
        programar(503, 502, 200);
        assertEquals(200, llamar("GET").statusCode());
        assertEquals(3, llamadas.get());
    }

    @Test
    void devuelveElUltimoFalloSiSeAgotanLosIntentos() {
        programar(503);
        assertEquals(503, llamar("GET").statusCode());
        assertEquals(3, llamadas.get());
    }

    @Test
    void reintentaPutYDeletePorqueSonIdempotentes() {
        programar(504, 200);
        assertEquals(200, llamar("PUT").statusCode());
        codigos.clear();
        programar(504, 201);
        assertEquals(201, llamar("DELETE").statusCode());
        assertEquals(4, llamadas.get());
    }

    @Test
    void elReintentoEnviaExactamenteLaMismaPeticion() {
        programar(503, 200);
        Response respuesta = RestAssured.given()
                .baseUri("http://localhost:" + servidor.getAddress().getPort())
                .filter(new ReintentoInfraestructura(3, 1))
                .cookie("token", "abc123")
                .contentType("application/json")
                .queryParam("q", "1")
                .body("{\"firstname\":\"Jim\"}")
                .put("/booking/7");
        assertEquals(200, respuesta.statusCode());
        assertEquals(2, peticionesRecibidas.size());
        assertEquals(peticionesRecibidas.get(0), peticionesRecibidas.get(1));
        org.junit.jupiter.api.Assertions.assertTrue(peticionesRecibidas.get(1).contains("cookie=token=abc123"), peticionesRecibidas.get(1));
        org.junit.jupiter.api.Assertions.assertTrue(peticionesRecibidas.get(1).contains("body={\"firstname\":\"Jim\"}"), peticionesRecibidas.get(1));
    }

    @Test
    void nuncaReintentaUnPostParaNoDuplicarDatos() {
        programar(503, 200);
        assertEquals(503, llamar("POST").statusCode());
        assertEquals(1, llamadas.get());
    }

    @Test
    void noReintentaErroresDeLaAplicacionNiRespuestasNormales() {
        for (int codigo : new int[]{500, 404, 403, 400}) {
            codigos.clear();
            llamadas.set(0);
            programar(codigo, 200);
            assertEquals(codigo, llamar("GET").statusCode());
            assertEquals(1, llamadas.get(), "código " + codigo);
        }
    }

    @Test
    void reintentaErroresDeConexionYTerminaLanzandoElError() {
        servidor.stop(0);
        long inicio = System.nanoTime();
        assertThrows(Exception.class, () -> llamar("GET"));
        // 3 intentos con esperas de 1 y 2 ms entre ellos
        assertEquals(true, (System.nanoTime() - inicio) / 1_000_000 >= 3);
    }

    @Test
    void rechazaUnNumeroDeIntentosInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new ReintentoInfraestructura(0, 1));
    }
}
