package com.loja.crud.model.vo;

import java.util.regex.Pattern;

public class Email {

    private static final Pattern FORMATO = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    private final String valor;

    public Email(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("Email invalido");
        }
        this.valor = valor.trim().toLowerCase();
        if (!validarEmail()) {
            throw new IllegalArgumentException("Email invalido");
        }
    }

    public boolean validarEmail() {
        return FORMATO.matcher(valor).matches();
    }

    public String getValor() {
        return valor;
    }
}
