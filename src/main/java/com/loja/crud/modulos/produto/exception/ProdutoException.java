package com.loja.crud.modulos.produto.exception;

public class ProdutoException extends RuntimeException {

    public ProdutoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
