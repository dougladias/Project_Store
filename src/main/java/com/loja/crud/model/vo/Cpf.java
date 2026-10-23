package com.loja.crud.model.vo;

public class Cpf {

    private static final int TAMANHO = 11;
    private static final int MODULO = 11;
    private static final int PRIMEIRO_DIGITO = 9;
    private static final int SEGUNDO_DIGITO = 10;

    private final String digitos;

    public Cpf(String valor) {
        if (valor == null) {
            throw new IllegalArgumentException("CPF invalido");
        }
        this.digitos = valor.replaceAll("\\D", "");
        if (!validarCPF()) {
            throw new IllegalArgumentException("CPF invalido");
        }
    }

    public boolean validarCPF() {
        if (digitos.length() != TAMANHO) {
            return false;
        }
        if (todosDigitosIguais()) {
            return false;
        }

        int primeiro = calcularDigitoVerificador(PRIMEIRO_DIGITO);
        int segundo = calcularDigitoVerificador(SEGUNDO_DIGITO);

        return primeiro == digitoNaPosicao(PRIMEIRO_DIGITO)
                && segundo == digitoNaPosicao(SEGUNDO_DIGITO);
    }

    public String getDigitos() {
        return digitos;
    }

    private boolean todosDigitosIguais() {
        return digitos.chars().distinct().count() == 1;
    }

    private int calcularDigitoVerificador(int posicao) {
        int soma = somarDigitos(posicao);
        int resto = soma % MODULO;

        if (resto < 2) {
            return 0;
        }

        return MODULO - resto;
    }

    private int somarDigitos(int posicao) {
        int soma = 0;

        for (int i = 0; i < posicao; i++) {
            int peso = posicao + 1 - i;
            soma += digitoNaPosicao(i) * peso;
        }

        return soma;
    }

    private int digitoNaPosicao(int posicao) {
        return Character.getNumericValue(digitos.charAt(posicao));
    }
}
