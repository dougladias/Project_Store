package com.loja.crud.infra.cep;

public class CepException extends RuntimeException {

    private final boolean naoEncontrado;

    private CepException(String mensagem, boolean naoEncontrado, Throwable causa) {
        super(mensagem, causa);
        this.naoEncontrado = naoEncontrado;
    }

    public static CepException naoEncontrado(String cep) {
        return new CepException("CEP nao encontrado: " + cep, true, null);
    }

    public static CepException indisponivel(String mensagem) {
        return new CepException(mensagem, false, null);
    }

    public static CepException indisponivel(String mensagem, Throwable causa) {
        return new CepException(mensagem, false, causa);
    }

    public boolean isNaoEncontrado() {
        return naoEncontrado;
    }
}
