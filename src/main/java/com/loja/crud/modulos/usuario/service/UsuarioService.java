package com.loja.crud.modulos.usuario.service;

import com.loja.crud.modulos.usuario.entity.Usuario;
import com.loja.crud.modulos.usuario.tables.UsuarioTable;

import java.util.List;

public class UsuarioService {

    private final UsuarioTable usuarioTable = new UsuarioTable();

    public Usuario cadastrar(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        int id = usuarioTable.inserir(usuario);
        return id > 0 ? usuarioTable.find(id) : null;
    }

    public List<Usuario> listar() {
        return usuarioTable.listar();
    }

    public Usuario buscar(int id) {
        if (id <= 0) {
            return null;
        }

        return usuarioTable.find(id);
    }

    public boolean existe(int id) {
        return id > 0 && usuarioTable.existe(id);
    }

    public boolean atualizar(Usuario usuario) {
        if (usuario == null || usuario.getId() <= 0) {
            return false;
        }

        return usuarioTable.atualizar(usuario);
    }

    public boolean deletar(int id) {
        if (id <= 0) {
            return false;
        }

        return usuarioTable.deletar(id);
    }
}
