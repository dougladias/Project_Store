package com.loja.crud.modulos.usuario.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class Usuario {

    private final int id;
    private final String nome;
    private final Cpf cpf;
    private final Email email;
    private final Telefone telefone;
    private final LocalDate dataNascimento;
    private final Endereco endereco;

    public Usuario(String nome, Cpf cpf, Email email, Telefone telefone, LocalDate dataNascimento, Endereco endereco) {
        this(0, nome, cpf, email, telefone, dataNascimento, endereco);
    }

    @JsonCreator
    public Usuario(
            @JsonProperty("id") int id,
            @JsonProperty("nome") String nome,
            @JsonProperty("cpf") Cpf cpf,
            @JsonProperty("email") Email email,
            @JsonProperty("telefone") Telefone telefone,
            @JsonProperty("dataNascimento") LocalDate dataNascimento,
            @JsonProperty("endereco") Endereco endereco) {
        if (id < 0) {
            throw new IllegalArgumentException("Id invalido");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome invalido");
        }
        if (cpf == null || email == null || telefone == null || dataNascimento == null || endereco == null) {
            throw new IllegalArgumentException("Dados do usuario invalidos");
        }
        if (dataNascimento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de nascimento invalida");
        }

        this.id = id;
        this.nome = nome.trim();
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.dataNascimento = dataNascimento;
        this.endereco = endereco;
    }

    public int getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public Cpf getCpf() {
        return cpf;
    }

    public Email getEmail() {
        return email;
    }

    public Telefone getTelefone() {
        return telefone;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public Usuario comId(int id) {
        return new Usuario(id, nome, cpf, email, telefone, dataNascimento, endereco);
    }

    public static class Cpf {

        private static final int TAMANHO = 11;
        private static final int MODULO = 11;
        private static final int PRIMEIRO_DIGITO = 9;
        private static final int SEGUNDO_DIGITO = 10;

        private final String digitos;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
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

        @JsonValue
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

    public static class Email {

        private static final Pattern FORMATO = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

        private final String valor;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public Email(String valor) {
            if (valor == null) {
                throw new IllegalArgumentException("Email invalido");
            }
            this.valor = valor.trim().toLowerCase();
            if (!validarEmail()) {
                throw new IllegalArgumentException("Email invalido");
            }
        }

        public boolean validarEmail() {
            return FORMATO.matcher(valor).matches();
        }

        @JsonValue
        public String getValor() {
            return valor;
        }
    }

    public static class Telefone {

        private static final int TAMANHO_DDD = 2;
        private static final int TAMANHO_MINIMO = 10;
        private static final int TAMANHO_MAXIMO = 11;
        private static final int DDD_MINIMO = 11;
        private static final int DDD_MAXIMO = 99;

        private final String digitos;

        @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
        public Telefone(String valor) {
            if (valor == null) {
                throw new IllegalArgumentException("Telefone invalido");
            }
            this.digitos = valor.replaceAll("\\D", "");
            if (!validarTelefone()) {
                throw new IllegalArgumentException("Telefone invalido");
            }
        }

        public boolean validarTelefone() {
            if (digitos.length() < TAMANHO_MINIMO || digitos.length() > TAMANHO_MAXIMO) {
                return false;
            }

            int ddd = Integer.parseInt(getDDD());
            return ddd >= DDD_MINIMO && ddd <= DDD_MAXIMO;
        }

        public String getDDD() {
            return digitos.substring(0, TAMANHO_DDD);
        }

        @JsonValue
        public String getDigitos() {
            return digitos;
        }
    }
}
