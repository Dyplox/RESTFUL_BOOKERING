package co.booker.utils;

import io.restassured.RestAssured;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

import java.io.IOException;
import java.util.Set;

/**
 * Filtro de RestAssured que reintenta una petición cuando falla la <b>infraestructura</b>, no la API.
 *
 * <p>Solo reintenta métodos idempotentes ({@code GET}, {@code HEAD}, {@code OPTIONS}, {@code PUT},
 * {@code DELETE}); un {@code POST} nunca se repite para no crear reservas duplicadas. Considera
 * transitorios los códigos {@code 502}, {@code 503} y {@code 504} (típicos de un proxy o de un
 * servidor que despierta) y los errores de E/S como una conexión rechazada o un timeout. Un
 * {@code 500} es un error de la aplicación y se devuelve tal cual, igual que cualquier otra respuesta:
 * las aserciones de las pruebas nunca se ocultan.</p>
 */
public class ReintentoInfraestructura implements Filter {

    private static final Set<String> IDEMPOTENTES = Set.of("GET", "HEAD", "OPTIONS", "PUT", "DELETE");
    private static final Set<Integer> TRANSITORIOS = Set.of(502, 503, 504);

    private final int maxIntentos;
    private final long esperaInicialMs;

    /**
     * @param maxIntentos     número total de intentos (1 = sin reintentos)
     * @param esperaInicialMs espera antes del primer reintento; se duplica en cada intento
     */
    public ReintentoInfraestructura(int maxIntentos, long esperaInicialMs) {
        if (maxIntentos < 1) {
            throw new IllegalArgumentException("maxIntentos debe ser al menos 1");
        }
        this.maxIntentos = maxIntentos;
        this.esperaInicialMs = esperaInicialMs;
    }

    @Override
    public Response filter(FilterableRequestSpecification peticion,
                           FilterableResponseSpecification respuesta,
                           FilterContext contexto) {
        if (!IDEMPOTENTES.contains(peticion.getMethod())) {
            return contexto.next(peticion, respuesta);
        }
        long espera = esperaInicialMs;
        for (int intento = 1; ; intento++) {
            boolean ultimo = intento == maxIntentos;
            try {
                // El FilterContext solo se puede recorrer una vez: los reintentos se reenvían con
                // send(), sobre una copia de la petición sin filtros (para no reintentar en bucle).
                Response resultado = intento == 1
                        ? contexto.next(peticion, respuesta)
                        : contexto.send(RestAssured.given().spec(peticion).noFilters());
                if (ultimo || !TRANSITORIOS.contains(resultado.statusCode())) {
                    return resultado;
                }
            } catch (Exception error) {
                if (ultimo || !esErrorDeConexion(error)) {
                    throw error;
                }
            }
            dormir(espera);
            espera *= 2;
        }
    }

    private static boolean esErrorDeConexion(Throwable error) {
        for (Throwable causa = error; causa != null; causa = causa.getCause()) {
            if (causa instanceof IOException) {
                return true;
            }
        }
        return false;
    }

    private static void dormir(long milisegundos) {
        try {
            Thread.sleep(milisegundos);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Reintento interrumpido", e);
        }
    }
}
