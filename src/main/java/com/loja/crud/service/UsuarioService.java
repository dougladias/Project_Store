package com.loja.crud.service;

import com.loja.crud.tables.UsuarioTable;
import com.loja.crud.exception.DataAccessException;
import com.loja.crud.entity.Usuario;

import java.util.List;

public class UsuarioService {

    private final UsuarioTable usuarioTable = new UsuarioTable();

    public boolean cadastrar(Usuario usuario) {
        if (usuario == null) {
            return false;
        }

        try {
            return usuarioTable.inserir(usuario);
        } catch (DataAccessException e) {
            return false;
        }
    }

    public List<Usuario> listar() {
        return usuarioTable.listar();
    }

    public boolean atualizar(Usuario usuario) {
        if (usuario == null || usuario.getId() <= 0) {
            return false;
        }

        try {
            return usuarioTable.atualizar(usuario);
        } catch (DataAccessException e) {
            return false;
        }
    }

    public boolean deletar(int id) {
        if (id <= 0) {
            return false;
        }

        try {
            return usuarioTable.deletar(id);
        } catch (DataAccessException e) {
            return false;
        }
    }
}
