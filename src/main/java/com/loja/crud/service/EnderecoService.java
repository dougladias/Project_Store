package com.loja.crud.service;

import com.loja.crud.entity.Endereco;
import com.loja.crud.tables.EnderecoTable;

import java.util.List;

public class EnderecoService {

    private final EnderecoTable enderecoTable = new EnderecoTable();

    public Endereco cadastrar(Endereco endereco) {
        if (endereco == null || endereco.getUsuarioId() <= 0) {
            return null;
        }

        int id = enderecoTable.inserir(endereco.getUsuarioId(), endereco);
        return id > 0 ? enderecoTable.find(id) : null;
    }

    public List<Endereco> listar() {
        return enderecoTable.listar();
    }

    public Endereco buscar(int id) {
        if (id <= 0) {
            return null;
        }

        return enderecoTable.find(id);
    }

    public Endereco buscarPorUsuario(int usuarioId) {
        if (usuarioId <= 0) {
            return null;
        }

        return enderecoTable.findByUsuario(usuarioId);
    }

    public boolean atualizar(int id, Endereco endereco) {
        if (id <= 0 || endereco == null) {
            return false;
        }

        return enderecoTable.atualizar(id, endereco);
    }
}
