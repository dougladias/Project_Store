package com.loja.crud.model.vo;

public class Telefone {

    private static final int TAMANHO_DDD = 2;
    private static final int TAMANHO_MINIMO = 10;
    private static final int TAMANHO_MAXIMO = 11;
    private static final int DDD_MINIMO = 11;
    private static final int DDD_MAXIMO = 99;

    private final String digitos;

    public Telefone(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Telefone invalido");
        }
        this.digitos = valor.replaceAll("\\D", "");
        if (!validarTelefone()) {
            throw new IllegalArgumentException("Telefone invalido");
        }
    }

    public boolean validarTelefone() {
        if (digitos.length() < TAMANHO_MINIMO || digitos.length() > TAMANHO_MAXIMO) {
            return false;
        }

        int ddd = Integer.parseInt(getDDD());
        return ddd >= DDD_MINIMO && ddd <= DDD_MAXIMO;
    }

    public String getDDD() {
        return digitos.substring(0, TAMANHO_DDD);
    }

    public String getDigitos() {
        return digitos;
    }
}
