package com.loja.crud.service;

import com.loja.crud.dao.UsuarioDAO;
import com.loja.crud.model.Usuario;

import java.util.List;

public class UsuarioService {

    private final UsuarioDAO dao = new UsuarioDAO();

    public boolean cadastrar(Usuario usuario) {
        if (usuario == null) {
            return false;
        }
        return dao.inserir(usuario);
    }

    public List<Usuario> listar() {
        return dao.listar();
    }

    public boolean atualizar(Usuario usuario) {
        if (usuario == null || usuario.getId() <= 0) {
            return false;
        }
        return dao.atualizar(usuario);
    }

    public boolean deletar(int id) {
        if (id <= 0) {
            return false;
        }
        return dao.deletar(id);
    }
}
