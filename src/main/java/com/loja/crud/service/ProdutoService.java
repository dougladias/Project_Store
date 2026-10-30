package com.loja.crud.service;

import com.loja.crud.entity.Produto;
import com.loja.crud.tables.ProdutoTable;

import java.util.List;

public class ProdutoService {

    private final ProdutoTable produtoTable = new ProdutoTable();

    public Produto cadastrar(Produto produto) {
        if (produto == null) {
            return null;
        }

        int id = produtoTable.inserir(produto);
        return id > 0 ? produtoTable.find(id) : null;
    }

    public List<Produto> listar() {
        return produtoTable.listar();
    }

    public Produto buscar(int id) {
        if (id <= 0) {
            return null;
        }

        return produtoTable.find(id);
    }

    public boolean atualizar(Produto produto) {
        if (produto == null || produto.getId() <= 0) {
            return false;
        }

        return produtoTable.atualizar(produto);
    }

    public boolean deletar(int id) {
        if (id <= 0) {
            return false;
        }

        return produtoTable.deletar(id);
    }
}
