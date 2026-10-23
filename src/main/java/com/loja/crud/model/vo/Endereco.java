package com.loja.crud.model.vo;

public class Endereco {

    private static final String FORMATO_CEP = "\\d{8}";

    private final String rua;
    private final String bairro;
    private final String numero;
    private final String cep;

    public Endereco(String rua, String bairro, String numero, String cep) {
        if (rua == null || bairro == null || numero == null || cep == null) {
            throw new IllegalArgumentException("Endereco invalido");
        }

        this.rua = rua.trim();
        this.bairro = bairro.trim();
        this.numero = numero.trim();
        this.cep = cep.replaceAll("\\D", "");

        if (this.rua.isEmpty() || this.bairro.isEmpty() || this.numero.isEmpty() || !validarCEP()) {
            throw new IllegalArgumentException("Endereco invalido");
        }
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

    public String getCEP() {
        return cep;
    }

    public boolean validarCEP() {
        return cep.matches(FORMATO_CEP);
    }
}
