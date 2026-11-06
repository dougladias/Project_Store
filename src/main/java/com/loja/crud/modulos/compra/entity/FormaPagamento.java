package com.loja.crud.modulos.compra.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

public enum FormaPagamento {

    PIX,
    CARTAO_CREDITO,
    CARTAO_DEBITO,
    BOLETO,
    DINHEIRO;

    @JsonCreator
    public static FormaPagamento from(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Forma de pagamento invalida");
        }

        try {
            return FormaPagamento.valueOf(valor.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Forma de pagamento invalida");
        }
    }

    @JsonValue
    public String getValor() {
        return name();
    }
}
