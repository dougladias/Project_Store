package com.loja.crud.modulos.usuario.controller;

import com.loja.crud.infra.http.HttpJson;
import com.loja.crud.modulos.usuario.entity.Endereco;
import com.loja.crud.modulos.usuario.entity.Usuario;
import com.loja.crud.modulos.usuario.exception.EnderecoException;
import com.loja.crud.modulos.usuario.exception.UsuarioException;
import com.loja.crud.modulos.usuario.service.EnderecoService;
import com.loja.crud.modulos.usuario.service.UsuarioService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class UsuarioController implements HttpHandler {

    public static final String CAMINHO = "/api/usuarios";

    private final UsuarioService service = new UsuarioService();
    private final EnderecoService enderecoService = new EnderecoService();

    public static void registrar(HttpServer server) {
        server.createContext(CAMINHO, new UsuarioController());
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

            if (partes.length == 5 && partes[4].equals("endereco")) {
                tratarEndereco(exchange, id(partes[3]));
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

    private void tratarEndereco(HttpExchange exchange, int usuarioId) throws IOException {
        if (metodo(exchange, "GET")) {
            buscarEndereco(exchange, usuarioId);
            return;
        }

        if (metodo(exchange, "PUT")) {
            atualizarEndereco(exchange, usuarioId);
            return;
        }

        metodoNaoPermitido(exchange, "GET, PUT");
    }

    private void listar(HttpExchange exchange) throws IOException {
        List<Usuario> usuarios = service.listar();
        responder(exchange, 200, usuarios);
    }

    private void buscar(HttpExchange exchange, int id) throws IOException {
        Usuario usuario = service.buscar(id);

        if (usuario == null) {
            naoEncontrado(exchange, "Usuario nao encontrado");
            return;
        }

        responder(exchange, 200, usuario);
    }

    private void criar(HttpExchange exchange) throws IOException {
        Usuario usuario = HttpJson.ler(exchange, Usuario.class);
        Usuario criado = service.cadastrar(usuario.comId(0));

        if (criado == null) {
            responderErro(exchange, 400, "Nao foi possivel cadastrar o usuario");
            return;
        }

        responderCriado(exchange, CAMINHO + "/" + criado.getId(), criado);
    }

    private void atualizar(HttpExchange exchange, int id) throws IOException {
        if (service.buscar(id) == null) {
            naoEncontrado(exchange, "Usuario nao encontrado");
            return;
        }

        Usuario usuario = HttpJson.ler(exchange, Usuario.class);

        if (!service.atualizar(usuario.comId(id))) {
            responderErro(exchange, 400, "Nao foi possivel atualizar o usuario");
            return;
        }

        responder(exchange, 200, service.buscar(id));
    }

    private void deletar(HttpExchange exchange, int id) throws IOException {
        if (service.buscar(id) == null) {
            naoEncontrado(exchange, "Usuario nao encontrado");
            return;
        }

        if (!service.deletar(id)) {
            responderErro(exchange, 400, "Nao foi possivel deletar o usuario");
            return;
        }

        semConteudo(exchange, 204);
    }

    private void buscarEndereco(HttpExchange exchange, int usuarioId) throws IOException {
        Endereco endereco = enderecoService.buscarPorUsuario(usuarioId);

        if (endereco == null) {
            naoEncontrado(exchange, "Endereco nao encontrado");
            return;
        }

        responder(exchange, 200, endereco);
    }

    private void atualizarEndereco(HttpExchange exchange, int usuarioId) throws IOException {
        Endereco endereco = enderecoService.buscarPorUsuario(usuarioId);

        if (endereco == null) {
            naoEncontrado(exchange, "Endereco nao encontrado");
            return;
        }

        Endereco novoEndereco = HttpJson.ler(exchange, Endereco.class);

        if (!enderecoService.atualizar(endereco.getId(), novoEndereco)) {
            responderErro(exchange, 400, "Nao foi possivel atualizar o endereco");
            return;
        }

        responder(exchange, 200, enderecoService.buscar(endereco.getId()));
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

        if (e instanceof UsuarioException usuarioException && usuarioException.isConflito()) {
            responderErro(exchange, 409, usuarioException.getMessage());
            return;
        }

        if (e instanceof UsuarioException || e instanceof EnderecoException) {
            System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            responderErro(exchange, 500, "Erro ao acessar o banco de dados");
            return;
        }

        System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        responderErro(exchange, 500, "Erro interno");
    }
}
