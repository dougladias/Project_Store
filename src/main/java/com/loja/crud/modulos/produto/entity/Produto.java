package com.loja.crud.modulos.produto.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Produto {

    private static final int TAMANHO_MAXIMO_IMAGEM = 10 * 1024 * 1024;
    private static final int TAMANHO_MAXIMO_BASE64 = 15 * 1024 * 1024;
    private static final Pattern IMAGEM_BASE64 = Pattern.compile(
            "^data:image/(png|jpe?g|webp|gif);base64,([A-Za-z0-9+/=]+)$",
            Pattern.CASE_INSENSITIVE);

    private int id;
    private String nome;
    private Unidade unidade;
    private BigDecimal preco;
    private BigDecimal quantidade;
    private String imagem;

    public Produto() {
    }

    @JsonCreator
    public Produto(
            @JsonProperty("id") int id,
            @JsonProperty("nome") String nome,
            @JsonProperty("unidade") Unidade unidade,
            @JsonProperty("preco") BigDecimal preco,
            @JsonProperty("quantidade") BigDecimal quantidade,
            @JsonProperty("imagem") String imagem) {
        setId(id);
        setNome(nome);
        setUnidade(unidade);
        setPreco(preco);
        setQuantidade(quantidade);
        setImagem(imagem);
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

    public Unidade getUnidade() {
        return unidade;
    }

    public void setUnidade(Unidade unidade) {
        if (unidade == null) {
            throw new IllegalArgumentException("Unidade invalida");
        }
        this.unidade = unidade;
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

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        if (quantidade == null || quantidade.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Quantidade invalida");
        }
        this.quantidade = quantidade;
    }

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        if (imagem == null || imagem.isBlank()) {
            this.imagem = null;
            return;
        }

        String valor = imagem.trim();

        if (valor.length() > TAMANHO_MAXIMO_BASE64) {
            throw new IllegalArgumentException("Imagem muito grande");
        }

        Matcher matcher = IMAGEM_BASE64.matcher(valor);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Imagem invalida");
        }

        byte[] bytes;

        try {
            bytes = Base64.getDecoder().decode(matcher.group(2));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Imagem invalida", e);
        }

        if (bytes.length > TAMANHO_MAXIMO_IMAGEM) {
            throw new IllegalArgumentException("Imagem muito grande");
        }

        this.imagem = valor;
    }

    public enum Unidade {
        UN,
        KG,
        G,
        L,
        ML
    }
}
