package co.booker.utils;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

/** Serialización JSON compartida (Gson). Omite los campos {@code null}. No instanciable. */
public final class Json {

    private static final Gson GSON = new Gson();

    private Json() {
        throw new IllegalStateException("No se debe instanciar la clase");
    }

    /**
     * @param objeto objeto a serializar
     * @return JSON sin los campos {@code null}
     */
    public static String aJson(Object objeto) {
        return GSON.toJson(objeto);
    }

    /**
     * @param json JSON de entrada
     * @param tipo clase destino
     * @param <T>  tipo destino
     * @return objeto deserializado
     */
    public static <T> T desde(String json, Class<T> tipo) {
        return GSON.fromJson(json, tipo);
    }

    /**
     * Deserializa un campo de primer nivel de un objeto JSON.
     *
     * @param json  JSON de entrada (un objeto)
     * @param campo nombre del campo a extraer
     * @param tipo  clase destino
     * @param <T>   tipo destino
     * @return el campo deserializado, o {@code null} si no existe
     */
    public static <T> T desde(String json, String campo, Class<T> tipo) {
        return GSON.fromJson(JsonParser.parseString(json).getAsJsonObject().get(campo), tipo);
    }
}
