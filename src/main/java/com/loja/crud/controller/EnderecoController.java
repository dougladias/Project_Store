package com.loja.crud.controller;

import com.loja.crud.entity.Endereco;
import com.loja.crud.exception.EnderecoException;
import com.loja.crud.exception.UsuarioException;
import com.loja.crud.service.EnderecoService;
import com.loja.crud.service.UsuarioService;
import com.loja.crud.utils.JsonUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class EnderecoController implements HttpHandler {

    public static final String CAMINHO = "/api/enderecos";

    private final EnderecoService service = new EnderecoService();
    private final UsuarioService usuarioService = new UsuarioService();

    public static void registrar(HttpServer server) {
        server.createContext(CAMINHO, new EnderecoController());
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

        metodoNaoPermitido(exchange, "GET, PUT");
    }

    private void listar(HttpExchange exchange) throws IOException {
        List<Endereco> enderecos = service.listar();
        responder(exchange, 200, enderecos);
    }

    private void buscar(HttpExchange exchange, int id) throws IOException {
        Endereco endereco = service.buscar(id);

        if (endereco == null) {
            naoEncontrado(exchange, "Endereco nao encontrado");
            return;
        }

        responder(exchange, 200, endereco);
    }

    private void criar(HttpExchange exchange) throws IOException {
        Endereco endereco = JsonUtil.ler(exchange, Endereco.class);
        int usuarioId = endereco.getUsuarioId();

        if (usuarioId <= 0) {
            throw new IllegalArgumentException("Usuario invalido");
        }

        if (!usuarioService.existe(usuarioId)) {
            naoEncontrado(exchange, "Usuario nao encontrado");
            return;
        }

        if (service.buscarPorUsuario(usuarioId) != null) {
            responderErro(exchange, 409, "Usuario ja possui endereco");
            return;
        }

        Endereco criado = service.cadastrar(endereco);

        if (criado == null) {
            responderErro(exchange, 400, "Nao foi possivel cadastrar o endereco");
            return;
        }

        responderCriado(exchange, CAMINHO + "/" + criado.getId(), criado);
    }

    private void atualizar(HttpExchange exchange, int id) throws IOException {
        if (service.buscar(id) == null) {
            naoEncontrado(exchange, "Endereco nao encontrado");
            return;
        }

        Endereco endereco = JsonUtil.ler(exchange, Endereco.class);

        if (!service.atualizar(id, endereco)) {
            responderErro(exchange, 400, "Nao foi possivel atualizar o endereco");
            return;
        }

        responder(exchange, 200, service.buscar(id));
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
        JsonUtil.escrever(exchange, status, corpo);
    }

    private void responderCriado(HttpExchange exchange, String localizacao, Object corpo) throws IOException {
        exchange.getResponseHeaders().set("Location", localizacao);
        JsonUtil.escrever(exchange, 201, corpo);
    }

    private void responderErro(HttpExchange exchange, int status, String mensagem) throws IOException {
        JsonUtil.escrever(exchange, status, Map.of("mensagem", mensagem == null ? "Erro" : mensagem));
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

        if (e instanceof EnderecoException enderecoException && enderecoException.isConflito()) {
            responderErro(exchange, 409, enderecoException.getMessage());
            return;
        }

        if (e instanceof EnderecoException || e instanceof UsuarioException) {
            System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            responderErro(exchange, 500, "Erro ao acessar o banco de dados");
            return;
        }

        System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        responderErro(exchange, 500, "Erro interno");
    }
}
