package com.loja.crud.exception;

public class EnderecoException extends RuntimeException {

    private final boolean conflito;

    public EnderecoException(String mensagem, Throwable causa) {
        this(mensagem, causa, false);
    }

    public EnderecoException(String mensagem, Throwable causa, boolean conflito) {
        super(mensagem, causa);
        this.conflito = conflito;
    }

    public boolean isConflito() {
        return conflito;
    }
}
