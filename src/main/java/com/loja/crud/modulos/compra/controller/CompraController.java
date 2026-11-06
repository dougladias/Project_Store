package com.loja.crud.modulos.compra.controller;

import com.loja.crud.infra.http.HttpJson;
import com.loja.crud.modulos.compra.entity.Compra;
import com.loja.crud.modulos.compra.entity.FormaPagamento;
import com.loja.crud.modulos.compra.exception.CompraException;
import com.loja.crud.modulos.compra.service.CompraService;
import com.loja.crud.modulos.produto.entity.Produto;
import com.loja.crud.modulos.produto.exception.ProdutoException;
import com.loja.crud.modulos.produto.service.ProdutoService;
import com.loja.crud.modulos.usuario.exception.UsuarioException;
import com.loja.crud.modulos.usuario.service.UsuarioService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CompraController implements HttpHandler {

    public static final String CAMINHO = "/api/compras";

    private final CompraService service = new CompraService();
    private final ProdutoService produtoService = new ProdutoService();
    private final UsuarioService usuarioService = new UsuarioService();

    public static void registrar(HttpServer server) {
        server.createContext(CAMINHO, new CompraController());
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String[] partes = segmentos(exchange);

            if (partes.length < 3 || !partes[2].equals("compras")) {
                naoEncontrado(exchange);
                return;
            }

            if (partes.length == 3) {
                tratarColecao(exchange);
                return;
            }

            if (partes.length == 4) {
                tratarItem(exchange, id(partes[3]));
                return;
            }

            if (partes.length == 5 && partes[4].equals("itens")) {
                tratarItens(exchange, id(partes[3]));
                return;
            }

            if (partes.length == 5 && partes[4].equals("finalizar")) {
                tratarFinalizar(exchange, id(partes[3]));
                return;
            }

            if (partes.length == 6 && partes[4].equals("itens")) {
                tratarRemocaoItem(exchange, id(partes[3]), id(partes[5]));
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

        if (metodo(exchange, "DELETE")) {
            cancelar(exchange, id);
            return;
        }

        metodoNaoPermitido(exchange, "GET, DELETE");
    }

    private void tratarItens(HttpExchange exchange, int compraId) throws IOException {
        if (metodo(exchange, "POST")) {
            adicionarItem(exchange, compraId);
            return;
        }

        metodoNaoPermitido(exchange, "POST");
    }

    private void tratarFinalizar(HttpExchange exchange, int compraId) throws IOException {
        if (metodo(exchange, "POST")) {
            finalizar(exchange, compraId);
            return;
        }

        metodoNaoPermitido(exchange, "POST");
    }

    private void tratarRemocaoItem(HttpExchange exchange, int compraId, int produtoId) throws IOException {
        if (metodo(exchange, "DELETE")) {
            removerItem(exchange, compraId, produtoId);
            return;
        }

        if (metodo(exchange, "PUT")) {
            definirQuantidade(exchange, compraId, produtoId);
            return;
        }

        metodoNaoPermitido(exchange, "DELETE, PUT");
    }

    private void listar(HttpExchange exchange) throws IOException {
        List<Compra> compras = service.listar();
        responder(exchange, 200, compras);
    }

    private void buscar(HttpExchange exchange, int id) throws IOException {
        Compra compra = service.buscar(id);

        if (compra == null) {
            naoEncontrado(exchange, "Compra nao encontrada");
            return;
        }

        responder(exchange, 200, compra);
    }

    private void criar(HttpExchange exchange) throws IOException {
        AbrirCompraRequest requisicao = HttpJson.ler(exchange, AbrirCompraRequest.class);

        if (requisicao.usuarioId() <= 0) {
            throw new IllegalArgumentException("Usuario invalido");
        }
        if (!usuarioService.existe(requisicao.usuarioId())) {
            naoEncontrado(exchange, "Usuario nao encontrado");
            return;
        }

        Compra criada = service.abrirCompra(requisicao.usuarioId());

        if (criada == null) {
            responderErro(exchange, 400, "Nao foi possivel criar a compra");
            return;
        }

        responderCriado(exchange, CAMINHO + "/" + criada.getId(), criada);
    }

    private void adicionarItem(HttpExchange exchange, int compraId) throws IOException {
        AdicionarItemRequest requisicao = HttpJson.ler(exchange, AdicionarItemRequest.class);

        if (requisicao.produtoId() <= 0) {
            throw new IllegalArgumentException("Produto invalido");
        }
        if (requisicao.quantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade invalida");
        }
        if (service.buscar(compraId) == null) {
            naoEncontrado(exchange, "Compra nao encontrada");
            return;
        }

        Produto produto = produtoService.buscar(requisicao.produtoId());

        if (produto == null) {
            naoEncontrado(exchange, "Produto nao encontrado");
            return;
        }

        Compra atualizada = service.adicionarProduto(compraId, requisicao.produtoId(), requisicao.quantidade());

        if (atualizada == null) {
            naoEncontrado(exchange, "Compra nao encontrada");
            return;
        }

        responder(exchange, 200, atualizada);
    }

    private void removerItem(HttpExchange exchange, int compraId, int produtoId) throws IOException {
        Compra compra = service.buscar(compraId);

        if (compra == null) {
            naoEncontrado(exchange, "Compra nao encontrada");
            return;
        }

        boolean possuiProduto = compra.getItens().stream()
                .anyMatch(item -> item.getProdutoId() == produtoId);

        if (!possuiProduto) {
            naoEncontrado(exchange, "Produto nao esta na compra");
            return;
        }

        Compra atualizada = service.removerProduto(compraId, produtoId);

        if (atualizada == null) {
            naoEncontrado(exchange, "Compra nao encontrada");
            return;
        }

        responder(exchange, 200, atualizada);
    }

    private void definirQuantidade(HttpExchange exchange, int compraId, int produtoId) throws IOException {
        DefinirQuantidadeRequest requisicao = HttpJson.ler(exchange, DefinirQuantidadeRequest.class);

        if (requisicao.quantidade() <= 0) {
            throw new IllegalArgumentException("Quantidade invalida");
        }
        if (service.buscar(compraId) == null) {
            naoEncontrado(exchange, "Compra nao encontrada");
            return;
        }

        Compra atualizada = service.definirQuantidadeItem(compraId, produtoId, requisicao.quantidade());

        if (atualizada == null) {
            naoEncontrado(exchange, "Compra nao encontrada");
            return;
        }

        responder(exchange, 200, atualizada);
    }

    private void cancelar(HttpExchange exchange, int compraId) throws IOException {
        if (service.buscar(compraId) == null) {
            naoEncontrado(exchange, "Compra nao encontrada");
            return;
        }

        if (!service.cancelarCompra(compraId)) {
            responderErro(exchange, 400, "Nao foi possivel cancelar a compra");
            return;
        }

        semConteudo(exchange, 204);
    }

    private void finalizar(HttpExchange exchange, int compraId) throws IOException {
        if (service.buscar(compraId) == null) {
            naoEncontrado(exchange, "Compra nao encontrada");
            return;
        }

        FinalizarCompraRequest requisicao = HttpJson.ler(exchange, FinalizarCompraRequest.class);

        if (requisicao.formaPagamento() == null) {
            throw new IllegalArgumentException("Forma de pagamento obrigatoria");
        }

        Compra finalizada = service.finalizarCompra(compraId, requisicao.formaPagamento());

        if (finalizada == null) {
            naoEncontrado(exchange, "Compra nao encontrada");
            return;
        }

        responder(exchange, 200, finalizada);
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

        if (e instanceof CompraException compraException && compraException.isConflito()) {
            responderErro(exchange, 409, compraException.getMessage());
            return;
        }

        if (e instanceof CompraException || e instanceof ProdutoException || e instanceof UsuarioException) {
            System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
            responderErro(exchange, 500, "Erro ao acessar o banco de dados");
            return;
        }

        System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        responderErro(exchange, 500, "Erro interno");
    }

    public record AbrirCompraRequest(int usuarioId) {
    }

    public record AdicionarItemRequest(int produtoId, int quantidade) {
    }

    public record DefinirQuantidadeRequest(int quantidade) {
    }

    public record FinalizarCompraRequest(FormaPagamento formaPagamento) {
    }
}
