package com.loja.crud.modulos.compra.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum StatusCompra {

    ABERTA,
    FINALIZADA;

    @JsonCreator
    public static StatusCompra from(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Status da compra invalido");
        }

        try {
            return StatusCompra.valueOf(valor.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status da compra invalido");
        }
    }

    @JsonValue
    public String getValor() {
        return name();
    }
}
