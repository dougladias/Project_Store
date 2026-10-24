package com.loja.crud.service;

import com.loja.crud.tables.ProdutoTable;
import com.loja.crud.exception.DataAccessException;
import com.loja.crud.entity.Produto;

import java.math.BigDecimal;
import java.util.List;

public class ProdutoService {

    private final ProdutoTable produtoTable = new ProdutoTable();

    public boolean cadastrar(String nome, BigDecimal preco, int quantidade) {
        if (nome == null || preco == null || quantidade < 0) {
            return false;
        }

        try {
            Produto produto = new Produto();
            produto.setNome(nome);
            produto.setPreco(preco);
            produto.setQuantidade(quantidade);
            return produtoTable.inserir(produto);
        } catch (IllegalArgumentException | DataAccessException e) {
            return false;
        }
    }

    public List<Produto> listar() {
        return produtoTable.listar();
    }

    public boolean atualizar(int id, String nome, BigDecimal preco, int quantidade) {
        if (id <= 0 || nome == null || preco == null || quantidade < 0) {
            return false;
        }

        try {
            Produto produto = new Produto();
            produto.setId(id);
            produto.setNome(nome);
            produto.setPreco(preco);
            produto.setQuantidade(quantidade);
            return produtoTable.atualizar(produto);
        } catch (IllegalArgumentException | DataAccessException e) {
            return false;
        }
    }

    public boolean deletar(int id) {
        if (id <= 0) {
            return false;
        }

        try {
            return produtoTable.deletar(id);
        } catch (DataAccessException e) {
            return false;
        }
    }
}
