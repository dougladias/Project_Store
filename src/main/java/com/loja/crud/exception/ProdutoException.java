package com.loja.crud.exception;

public class ProdutoException extends RuntimeException {

    public ProdutoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
