package com.loja.crud.modulos.usuario.controller;

import com.loja.crud.infra.cep.CepException;
import com.loja.crud.infra.cep.EnderecoCep;
import com.loja.crud.infra.cep.ViaCepClient;
import com.loja.crud.infra.http.HttpJson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.util.Map;

public class CepController implements HttpHandler {

    public static final String CAMINHO = "/api/cep";

    private final ViaCepClient viaCepClient = new ViaCepClient();

    public static void registrar(HttpServer server) {
        server.createContext(CAMINHO, new CepController());
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (!metodo(exchange, "GET")) {
                metodoNaoPermitido(exchange, "GET");
                return;
            }

            String[] partes = segmentos(exchange);

            if (partes.length != 4) {
                naoEncontrado(exchange);
                return;
            }

            EnderecoCep endereco = viaCepClient.consultar(partes[3]);
            HttpJson.escrever(exchange, 200, endereco);
        } catch (RuntimeException e) {
            tratarErro(exchange, e);
        }
    }

    private String[] segmentos(HttpExchange exchange) {
        String caminho = exchange.getRequestURI().getPath();

        if (caminho.length() > 1 && caminho.endsWith("/")) {
            caminho = caminho.substring(0, caminho.length() - 1);
        }

        return caminho.split("/");
    }

    private boolean metodo(HttpExchange exchange, String metodo) {
        return exchange.getRequestMethod().equalsIgnoreCase(metodo);
    }

    private void responderErro(HttpExchange exchange, int status, String mensagem) throws IOException {
        HttpJson.escrever(exchange, status, Map.of("mensagem", mensagem));
    }

    private void naoEncontrado(HttpExchange exchange) throws IOException {
        responderErro(exchange, 404, "Recurso nao encontrado");
    }

    private void metodoNaoPermitido(HttpExchange exchange, String permitidos) throws IOException {
        exchange.getResponseHeaders().set("Allow", permitidos);
        responderErro(exchange, 405, "Metodo nao permitido");
    }

    private void tratarErro(HttpExchange exchange, RuntimeException e) throws IOException {
        if (e instanceof IllegalArgumentException) {
            responderErro(exchange, 400, e.getMessage());
            return;
        }

        if (e instanceof CepException cepException) {
            if (cepException.isNaoEncontrado()) {
                responderErro(exchange, 404, "CEP nao encontrado");
                return;
            }

            System.err.println("CepException: " + cepException.getMessage());
            responderErro(exchange, 502, "CEP indisponivel");
            return;
        }

        System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        responderErro(exchange, 500, "Erro interno");
    }
}
