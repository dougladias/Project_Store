package com.loja.crud.exception;

public class DataAccessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DataAccessException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
