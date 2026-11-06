package com.loja.crud.modulos.compra.service;

import com.loja.crud.modulos.compra.entity.Compra;
import com.loja.crud.modulos.compra.entity.ItemCompra;
import com.loja.crud.modulos.compra.exception.CompraException;
import com.loja.crud.modulos.compra.tables.CompraTable;
import com.loja.crud.modulos.compra.tables.ItemCompraTable;
import com.loja.crud.modulos.compra.entity.FormaPagamento;
import com.loja.crud.modulos.compra.entity.StatusCompra;
import com.loja.crud.modulos.produto.entity.Produto;
import com.loja.crud.modulos.produto.service.ProdutoService;
import com.loja.crud.modulos.usuario.service.UsuarioService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompraService {

    private final CompraTable compraTable = new CompraTable();
    private final ItemCompraTable itemTable = new ItemCompraTable();
    private final ProdutoService produtoService = new ProdutoService();
    private final UsuarioService usuarioService = new UsuarioService();

    public Compra abrirCompra(int usuarioId) {
        if (usuarioId <= 0 || !usuarioService.existe(usuarioId)) {
            return null;
        }

        int id = compraTable.inserir(usuarioId);
        return id > 0 ? buscar(id) : null;
    }

    public Compra buscar(int id) {
        if (id <= 0) {
            return null;
        }

        Compra compra = compraTable.find(id);

        if (compra == null) {
            return null;
        }

        return compra.comItens(itemTable.listarPorCompra(id));
    }

    public List<Compra> listar() {
        List<Compra> compras = compraTable.listar();

        if (compras.isEmpty()) {
            return compras;
        }

        Map<Integer, List<ItemCompra>> itensPorCompra = new HashMap<>();

        for (ItemCompra item : itemTable.listarTodos()) {
            itensPorCompra.computeIfAbsent(item.getCompraId(), chave -> new ArrayList<>()).add(item);
        }

        List<Compra> resultado = new ArrayList<>(compras.size());

        for (Compra compra : compras) {
            resultado.add(compra.comItens(itensPorCompra.getOrDefault(compra.getId(), List.of())));
        }

        return resultado;
    }

    public Compra adicionarProduto(int compraId, int produtoId, int quantidade) {
        if (compraId <= 0 || produtoId <= 0 || quantidade <= 0) {
            throw new IllegalArgumentException("Dados do item invalidos");
        }

        Compra compra = buscar(compraId);

        if (compra == null) {
            return null;
        }
        if (compra.getStatus() != StatusCompra.ABERTA) {
            throw new CompraException("Compra ja finalizada", null, true);
        }

        Produto produto = produtoService.buscar(produtoId);

        if (produto == null) {
            throw new IllegalArgumentException("Produto nao encontrado");
        }
        int quantidadeTotal = quantidadeNoCarrinho(compra, produtoId) + quantidade;
        if (BigDecimal.valueOf(quantidadeTotal).compareTo(produto.getQuantidade()) > 0) {
            throw new CompraException("Estoque insuficiente para o produto " + produto.getNome(), null, true);
        }

        compra.adicionarProduto(produto, quantidade);

        Compra atualizada = compraTable.adicionarProduto(compraId, produtoId, quantidade);
        return atualizada == null ? null : buscar(compraId);
    }

    public Compra definirQuantidadeItem(int compraId, int produtoId, int quantidade) {
        if (compraId <= 0 || produtoId <= 0 || quantidade <= 0) {
            throw new IllegalArgumentException("Dados do item invalidos");
        }

        Compra compra = buscar(compraId);

        if (compra == null) {
            return null;
        }
        if (compra.getStatus() != StatusCompra.ABERTA) {
            throw new CompraException("Compra ja finalizada", null, true);
        }

        Produto produto = produtoService.buscar(produtoId);

        if (produto == null) {
            throw new IllegalArgumentException("Produto nao encontrado");
        }
        if (BigDecimal.valueOf(quantidade).compareTo(produto.getQuantidade()) > 0) {
            throw new CompraException("Estoque insuficiente para o produto " + produto.getNome(), null, true);
        }

        compra.definirQuantidade(produto, quantidade);

        Compra atualizada = compraTable.definirQuantidade(compraId, produtoId, quantidade);
        return atualizada == null ? null : buscar(compraId);
    }

    public boolean cancelarCompra(int compraId) {
        if (compraId <= 0) {
            throw new IllegalArgumentException("Compra invalida");
        }

        Compra compra = buscar(compraId);

        if (compra == null) {
            return false;
        }
        if (compra.getStatus() != StatusCompra.ABERTA) {
            throw new CompraException("Compra ja finalizada", null, true);
        }

        return compraTable.cancelar(compraId);
    }

    public Compra removerProduto(int compraId, int produtoId) {
        if (compraId <= 0 || produtoId <= 0) {
            throw new IllegalArgumentException("Dados do item invalidos");
        }

        Compra compra = buscar(compraId);

        if (compra == null) {
            return null;
        }
        if (compra.getStatus() != StatusCompra.ABERTA) {
            throw new CompraException("Compra ja finalizada", null, true);
        }

        compra.removerProduto(produtoId);

        Compra atualizada = compraTable.removerProduto(compraId, produtoId);
        return atualizada == null ? null : buscar(compraId);
    }

    public Compra finalizarCompra(int compraId, FormaPagamento formaPagamento) {
        if (compraId <= 0 || formaPagamento == null) {
            throw new IllegalArgumentException("Dados da finalizacao invalidos");
        }

        Compra compra = buscar(compraId);

        if (compra == null) {
            return null;
        }
        if (compra.getStatus() != StatusCompra.ABERTA) {
            throw new CompraException("Compra ja finalizada", null, true);
        }

        compra.finalizar(formaPagamento);

        Compra finalizada = compraTable.finalizar(compraId, formaPagamento);
        return finalizada == null ? null : buscar(compraId);
    }

    private int quantidadeNoCarrinho(Compra compra, int produtoId) {
        return compra.getItens().stream()
                .filter(item -> item.getProdutoId() == produtoId)
                .mapToInt(ItemCompra::getQuantidade)
                .sum();
    }
}
