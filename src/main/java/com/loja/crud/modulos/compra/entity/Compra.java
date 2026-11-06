package com.loja.crud.modulos.compra.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.loja.crud.modulos.produto.entity.Produto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Compra {

    private final int id;
    private final int usuarioId;
    private final String usuarioNome;
    private FormaPagamento formaPagamento;
    private BigDecimal valorTotal;
    private StatusCompra status;
    private final LocalDateTime dataCriacao;
    private LocalDateTime dataFinalizacao;
    private final List<ItemCompra> itens;

    public Compra(int usuarioId) {
        this(0, usuarioId, null, null, BigDecimal.ZERO, StatusCompra.ABERTA, null, null, new ArrayList<>());
    }

    @JsonCreator
    public Compra(
            @JsonProperty("id") int id,
            @JsonProperty("usuarioId") int usuarioId,
            @JsonProperty("usuarioNome") String usuarioNome,
            @JsonProperty("formaPagamento") FormaPagamento formaPagamento,
            @JsonProperty("valorTotal") BigDecimal valorTotal,
            @JsonProperty("status") StatusCompra status,
            @JsonProperty("dataCriacao") LocalDateTime dataCriacao,
            @JsonProperty("dataFinalizacao") LocalDateTime dataFinalizacao,
            @JsonProperty("itens") List<ItemCompra> itens) {
        if (id < 0) {
            throw new IllegalArgumentException("Id invalido");
        }
        if (usuarioId <= 0) {
            throw new IllegalArgumentException("Usuario invalido");
        }
        if (valorTotal != null && valorTotal.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Valor da compra invalido");
        }

        StatusCompra statusFinal = status == null ? StatusCompra.ABERTA : status;

        if (statusFinal == StatusCompra.FINALIZADA && formaPagamento == null) {
            throw new IllegalArgumentException("Forma de pagamento obrigatoria");
        }

        this.id = id;
        this.usuarioId = usuarioId;
        this.usuarioNome = usuarioNome;
        this.formaPagamento = formaPagamento;
        this.valorTotal = valorTotal == null ? BigDecimal.ZERO : valorTotal;
        this.status = statusFinal;
        this.dataCriacao = dataCriacao;
        this.dataFinalizacao = dataFinalizacao;
        this.itens = itens == null ? new ArrayList<>() : new ArrayList<>(itens);
    }

    public int getId() {
        return id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public StatusCompra getStatus() {
        return status;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataFinalizacao() {
        return dataFinalizacao;
    }

    public List<ItemCompra> getItens() {
        return Collections.unmodifiableList(itens);
    }

    @JsonIgnore
    public boolean isAberta() {
        return status == StatusCompra.ABERTA;
    }

    public void adicionarProduto(Produto produto, int quantidade) {
        if (!isAberta()) {
            throw new IllegalArgumentException("Compra nao esta aberta");
        }
        if (produto == null || produto.getId() <= 0) {
            throw new IllegalArgumentException("Produto invalido");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade invalida");
        }
        if (BigDecimal.valueOf(quantidade).compareTo(produto.getQuantidade()) > 0) {
            throw new IllegalArgumentException("Estoque insuficiente");
        }

        for (ItemCompra item : itens) {
            if (item.getProdutoId() == produto.getId()) {
                if (BigDecimal.valueOf(item.getQuantidade() + quantidade).compareTo(produto.getQuantidade()) > 0) {
                    throw new IllegalArgumentException("Estoque insuficiente");
                }

                item.adicionarQuantidade(quantidade);
                recalcularTotal();
                return;
            }
        }

        itens.add(new ItemCompra(id, produto.getId(), produto.getNome(), quantidade, produto.getPreco()));
        recalcularTotal();
    }

    public void definirQuantidade(Produto produto, int quantidade) {
        if (!isAberta()) {
            throw new IllegalArgumentException("Compra nao esta aberta");
        }
        if (produto == null || produto.getId() <= 0) {
            throw new IllegalArgumentException("Produto invalido");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade invalida");
        }
        if (BigDecimal.valueOf(quantidade).compareTo(produto.getQuantidade()) > 0) {
            throw new IllegalArgumentException("Estoque insuficiente");
        }

        for (ItemCompra item : itens) {
            if (item.getProdutoId() == produto.getId()) {
                item.definirQuantidade(quantidade);
                recalcularTotal();
                return;
            }
        }

        throw new IllegalArgumentException("Produto nao esta na compra");
    }

    public void removerProduto(int produtoId) {
        if (!isAberta()) {
            throw new IllegalArgumentException("Compra nao esta aberta");
        }
        if (produtoId <= 0) {
            throw new IllegalArgumentException("Produto invalido");
        }

        boolean removido = itens.removeIf(item -> item.getProdutoId() == produtoId);

        if (!removido) {
            throw new IllegalArgumentException("Produto nao esta na compra");
        }

        recalcularTotal();
    }

    public void finalizar(FormaPagamento formaPagamento) {
        if (!isAberta()) {
            throw new IllegalArgumentException("Compra ja finalizada");
        }
        if (formaPagamento == null) {
            throw new IllegalArgumentException("Forma de pagamento obrigatoria");
        }
        if (itens.isEmpty()) {
            throw new IllegalArgumentException("Compra sem itens");
        }

        this.formaPagamento = formaPagamento;
        this.status = StatusCompra.FINALIZADA;
        this.dataFinalizacao = LocalDateTime.now();
        recalcularTotal();
    }

    public Compra comItens(List<ItemCompra> itens) {
        return new Compra(id, usuarioId, usuarioNome, formaPagamento, valorTotal, status, dataCriacao, dataFinalizacao, itens);
    }

    private void recalcularTotal() {
        this.valorTotal = itens.stream()
                .map(ItemCompra::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
