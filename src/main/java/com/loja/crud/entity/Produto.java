package com.loja.crud.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Produto {

    private int id;
    private String nome;
    private BigDecimal preco;
    private int quantidade;

    public Produto() {
    }

    @JsonCreator
    public Produto(
            @JsonProperty("id") int id,
            @JsonProperty("nome") String nome,
            @JsonProperty("preco") BigDecimal preco,
            @JsonProperty("quantidade") int quantidade) {
        setId(id);
        setNome(nome);
        setPreco(preco);
        setQuantidade(quantidade);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        if (id < 0) {
            throw new IllegalArgumentException("Id invalido");
        }
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome invalido");
        }
        this.nome = nome.trim();
    }

    public BigDecimal getPreco() {
        return preco;
    }

    public void setPreco(BigDecimal preco) {
        if (preco == null || preco.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Preco invalido");
        }
        this.preco = preco;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade invalida");
        }
        this.quantidade = quantidade;
    }
}
