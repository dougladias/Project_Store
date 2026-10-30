package com.loja.crud.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;

public final class JsonUtil {

    private static final ObjectMapper MAPPER = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    private JsonUtil() {
    }

    public static <T> T ler(HttpExchange exchange, Class<T> tipo) throws IOException {
        try {
            T valor = MAPPER.readValue(exchange.getRequestBody(), tipo);

            if (valor == null) {
                throw new IllegalArgumentException("JSON invalido");
            }

            return valor;
        } catch (JsonProcessingException e) {
            IllegalArgumentException validacao = encontrarIllegalArgument(e);

            if (validacao != null) {
                throw validacao;
            }

            throw new IllegalArgumentException("JSON invalido", e);
        }
    }

    private static IllegalArgumentException encontrarIllegalArgument(Throwable erro) {
        Throwable causa = erro.getCause();

        while (causa != null) {
            if (causa instanceof IllegalArgumentException argumento) {
                return argumento;
            }

            causa = causa.getCause();
        }

        return null;
    }

    public static void escrever(HttpExchange exchange, int status, Object corpo) throws IOException {
        byte[] bytes = MAPPER.writeValueAsBytes(corpo);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream saida = exchange.getResponseBody()) {
            saida.write(bytes);
        }
    }

    public static void escreverVazio(HttpExchange exchange, int status) throws IOException {
        exchange.sendResponseHeaders(status, -1);
        exchange.close();
    }
}
