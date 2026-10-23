package com.loja.crud.model;

import com.loja.crud.model.vo.Cpf;
import com.loja.crud.model.vo.Email;
import com.loja.crud.model.vo.Endereco;
import com.loja.crud.model.vo.Telefone;

import java.time.LocalDate;

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

    public Usuario(int id, String nome, Cpf cpf, Email email, Telefone telefone, LocalDate dataNascimento, Endereco endereco) {
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
}
