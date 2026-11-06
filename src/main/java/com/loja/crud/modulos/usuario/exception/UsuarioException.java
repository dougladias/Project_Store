package com.loja.crud.modulos.usuario.exception;

public class UsuarioException extends RuntimeException {

    private final boolean conflito;

    public UsuarioException(String mensagem, Throwable causa) {
        this(mensagem, causa, false);
    }

    public UsuarioException(String mensagem, Throwable causa, boolean conflito) {
        super(mensagem, causa);
        this.conflito = conflito;
    }

    public boolean isConflito() {
        return conflito;
    }
}
