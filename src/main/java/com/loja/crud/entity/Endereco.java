package com.loja.crud.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Endereco {

    private static final String FORMATO_CEP = "\\d{8}";

    private final int id;
    private final int usuarioId;
    private final String rua;
    private final String bairro;
    private final String numero;
    private final String cep;

    public Endereco(String rua, String bairro, String numero, String cep) {
        this(0, 0, rua, bairro, numero, cep);
    }

    @JsonCreator
    public Endereco(
            @JsonProperty("id") int id,
            @JsonProperty("usuarioId") int usuarioId,
            @JsonProperty("rua") String rua,
            @JsonProperty("bairro") String bairro,
            @JsonProperty("numero") String numero,
            @JsonProperty("cep") String cep) {
        if (id < 0 || usuarioId < 0) {
            throw new IllegalArgumentException("Id invalido");
        }
        if (rua == null || bairro == null || numero == null || cep == null) {
            throw new IllegalArgumentException("Endereco invalido");
        }

        this.id = id;
        this.usuarioId = usuarioId;
        this.rua = rua.trim();
        this.bairro = bairro.trim();
        this.numero = numero.trim();
        this.cep = cep.replaceAll("\\D", "");

        if (this.rua.isEmpty() || this.bairro.isEmpty() || this.numero.isEmpty() || !validarCEP()) {
            throw new IllegalArgumentException("Endereco invalido");
        }
    }

    public int getId() {
        return id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public String getRua() {
        return rua;
    }

    public String getBairro() {
        return bairro;
    }

    public String getNumero() {
        return numero;
    }

    @JsonProperty("cep")
    public String getCEP() {
        return cep;
    }

    public boolean validarCEP() {
        return cep.matches(FORMATO_CEP);
    }
}
