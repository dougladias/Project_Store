package com.loja.crud.modulos.compra.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class ItemCompra {

    private final int id;
    private final int compraId;
    private final int produtoId;
    private final String produtoNome;
    private int quantidade;
    private final BigDecimal precoUnitario;

    public ItemCompra(int compraId, int produtoId, String produtoNome, int quantidade, BigDecimal precoUnitario) {
        this(0, compraId, produtoId, produtoNome, quantidade, precoUnitario);
    }

    @JsonCreator
    public ItemCompra(
            @JsonProperty("id") int id,
            @JsonProperty("compraId") int compraId,
            @JsonProperty("produtoId") int produtoId,
            @JsonProperty("produtoNome") String produtoNome,
            @JsonProperty("quantidade") int quantidade,
            @JsonProperty("precoUnitario") BigDecimal precoUnitario) {
        if (id < 0) {
            throw new IllegalArgumentException("Id invalido");
        }
        if (compraId < 0) {
            throw new IllegalArgumentException("Compra invalida");
        }
        if (produtoId <= 0) {
            throw new IllegalArgumentException("Produto invalido");
        }
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade invalida");
        }
        if (precoUnitario == null || precoUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preco invalido");
        }

        this.id = id;
        this.compraId = compraId;
        this.produtoId = produtoId;
        this.produtoNome = produtoNome;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public int getId() {
        return id;
    }

    public int getCompraId() {
        return compraId;
    }

    public int getProdutoId() {
        return produtoId;
    }

    public String getProdutoNome() {
        return produtoNome;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public BigDecimal getPrecoUnitario() {
        return precoUnitario;
    }

    public BigDecimal getSubtotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }

    public void adicionarQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade invalida");
        }

        this.quantidade += quantidade;
    }

    public void definirQuantidade(int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade invalida");
        }

        this.quantidade = quantidade;
    }
}
