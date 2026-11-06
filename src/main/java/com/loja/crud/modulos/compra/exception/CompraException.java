package com.loja.crud.modulos.compra.exception;

public class CompraException extends RuntimeException {

    private final boolean conflito;

    public CompraException(String mensagem) {
        this(mensagem, null, false);
    }

    public CompraException(String mensagem, Throwable causa) {
        this(mensagem, causa, false);
    }

    public CompraException(String mensagem, Throwable causa, boolean conflito) {
        super(mensagem, causa);
        this.conflito = conflito;
    }

    public boolean isConflito() {
        return conflito;
    }
}
