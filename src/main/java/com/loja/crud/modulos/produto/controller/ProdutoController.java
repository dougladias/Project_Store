package com.loja.crud.modulos.produto.controller;

import com.loja.crud.infra.http.HttpJson;
import com.loja.crud.modulos.produto.entity.Produto;
import com.loja.crud.modulos.produto.exception.ProdutoException;
import com.loja.crud.modulos.produto.service.ProdutoService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ProdutoController implements HttpHandler {

    public static final String CAMINHO = "/api/produtos";

    private final ProdutoService service = new ProdutoService();

    public static void registrar(HttpServer server) {
        server.createContext(CAMINHO, new ProdutoController());
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String[] partes = segmentos(exchange);

            if (partes.length == 3) {
                tratarColecao(exchange);
                return;
            }

            if (partes.length == 4) {
                tratarItem(exchange, id(partes[3]));
                return;
            }

            naoEncontrado(exchange);
        } catch (RuntimeException e) {
            tratarErro(exchange, e);
        }
    }

    private void tratarColecao(HttpExchange exchange) throws IOException {
        if (metodo(exchange, "GET")) {
            listar(exchange);
            return;
        }

        if (metodo(exchange, "POST")) {
            criar(exchange);
            return;
        }

        metodoNaoPermitido(exchange, "GET, POST");
    }

    private void tratarItem(HttpExchange exchange, int id) throws IOException {
        if (metodo(exchange, "GET")) {
            buscar(exchange, id);
            return;
        }

        if (metodo(exchange, "PUT")) {
            atualizar(exchange, id);
            return;
        }

        if (metodo(exchange, "DELETE")) {
            deletar(exchange, id);
            return;
        }

        metodoNaoPermitido(exchange, "GET, PUT, DELETE");
    }

    private void listar(HttpExchange exchange) throws IOException {
        List<Produto> produtos = service.listar();
        responder(exchange, 200, produtos);
    }

    private void buscar(HttpExchange exchange, int id) throws IOException {
        Produto produto = service.buscar(id);

        if (produto == null) {
            naoEncontrado(exchange, "Produto nao encontrado");
            return;
        }

        responder(exchange, 200, produto);
    }

    private void criar(HttpExchange exchange) throws IOException {
        Produto produto = HttpJson.ler(exchange, Produto.class);
        produto.setId(0);

        Produto criado = service.cadastrar(produto);

        if (criado == null) {
            responderErro(exchange, 400, "Nao foi possivel cadastrar o produto");
            return;
        }

        responderCriado(exchange, CAMINHO + "/" + criado.getId(), criado);
    }

    private void atualizar(HttpExchange exchange, int id) throws IOException {
        if (service.buscar(id) == null) {
            naoEncontrado(exchange, "Produto nao encontrado");
            return;
        }

        Produto produto = HttpJson.ler(exchange, Produto.class);
        produto.setId(id);

        if (!service.atualizar(produto)) {
            responderErro(exchange, 400, "Nao foi possivel atualizar o produto");
            return;
        }

        responder(exchange, 200, service.buscar(id));
    }

    private void deletar(HttpExchange exchange, int id) throws IOException {
        if (service.buscar(id) == null) {
            naoEncontrado(exchange, "Produto nao encontrado");
            return;
        }

        if (!service.deletar(id)) {
            responderErro(exchange, 400, "Nao foi possivel deletar o produto");
            return;
        }

        semConteudo(exchange, 204);
    }

    private String[] segmentos(HttpExchange exchange) {
        String caminho = exchange.getRequestURI().getPath();

        if (caminho.length() > 1 && caminho.endsWith("/")) {
            caminho = caminho.substring(0, caminho.length() - 1);
        }

        return caminho.split("/");
    }

    private int id(String valor) {
        try {
            int id = Integer.parseInt(valor);

            if (id <= 0) {
                throw new IllegalArgumentException("Id invalido");
            }

            return id;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Id invalido");
        }
    }

    private boolean metodo(HttpExchange exchange, String metodo) {
        return exchange.getRequestMethod().equalsIgnoreCase(metodo);
    }

    private void responder(HttpExchange exchange, int status, Object corpo) throws IOException {
        HttpJson.escrever(exchange, status, corpo);
    }

    private void responderCriado(HttpExchange exchange, String localizacao, Object corpo) throws IOException {
        exchange.getResponseHeaders().set("Location", localizacao);
        HttpJson.escrever(exchange, 201, corpo);
    }

    private void responderErro(HttpExchange exchange, int status, String mensagem) throws IOException {
        HttpJson.escrever(exchange, status, Map.of("mensagem", mensagem == null ? "Erro" : mensagem));
    }

    private void semConteudo(HttpExchange exchange, int status) throws IOException {
        HttpJson.escreverVazio(exchange, status);
    }

    private void naoEncontrado(HttpExchange exchange, String mensagem) throws IOException {
        responderErro(exchange, 404, mensagem);
    }

    private void naoEncontrado(HttpExchange exchange) throws IOException {
        naoEncontrado(exchange, "Recurso nao encontrado");
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

        if (e instanceof ProdutoException) {
            System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            responderErro(exchange, 500, "Erro ao acessar o banco de dados");
            return;
        }

        System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        responderErro(exchange, 500, "Erro interno");
    }
}
