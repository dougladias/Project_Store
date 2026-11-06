package com.loja.crud.infra.cep;

import com.fasterxml.jackson.databind.JsonNode;
import com.loja.crud.infra.http.HttpJson;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ViaCepClient {

    private static final String URL = "https://viacep.com.br/ws/%s/json/";
    private static final int TAMANHO_CEP = 8;
    private static final Duration TIMEOUT_CONEXAO = Duration.ofSeconds(3);
    private static final Duration TIMEOUT_REQUISICAO = Duration.ofSeconds(5);

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(TIMEOUT_CONEXAO)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    public EnderecoCep consultar(String cep) {
        String digitos = normalizar(cep);

        HttpRequest requisicao = HttpRequest.newBuilder()
                .uri(URI.create(URL.formatted(digitos)))
                .timeout(TIMEOUT_REQUISICAO)
                .header("Accept", "application/json")
                .GET()
                .build();

        HttpResponse<String> resposta;

        try {
            resposta = httpClient.send(requisicao, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw CepException.indisponivel("Consulta de CEP interrompida", e);
        } catch (IOException e) {
            throw CepException.indisponivel("Nao foi possivel consultar o ViaCEP", e);
        }

        if (resposta.statusCode() != 200) {
            throw CepException.indisponivel("ViaCEP respondeu com status " + resposta.statusCode());
        }

        ViaCepResponse dados = HttpJson.ler(resposta.body(), ViaCepResponse.class);

        if (possuiErro(dados)) {
            throw CepException.naoEncontrado(digitos);
        }

        return new EnderecoCep(
                texto(dados.cep()),
                texto(dados.logradouro()),
                texto(dados.bairro()),
                texto(dados.localidade()),
                texto(dados.uf()));
    }

    private String normalizar(String cep) {
        if (cep == null) {
            throw new IllegalArgumentException("CEP invalido");
        }

        String digitos = cep.replaceAll("\\D", "");

        if (digitos.length() != TAMANHO_CEP) {
            throw new IllegalArgumentException("CEP invalido");
        }

        return digitos;
    }

    private boolean possuiErro(ViaCepResponse dados) {
        JsonNode erro = dados.erro();
        return erro != null
                && !erro.isNull()
                && (erro.asBoolean(false) || "true".equalsIgnoreCase(erro.asText()));
    }

    private String texto(String valor) {
        return valor == null ? "" : valor;
    }

    private record ViaCepResponse(
            String cep,
            String logradouro,
            String bairro,
            String localidade,
            String uf,
            JsonNode erro) {
    }
}
