package com.loja.crud.service;

import com.loja.crud.dao.ProdutoDAO;
import com.loja.crud.model.Produto;

import java.util.List;

// Regras do produto, valida os dados antes de mandar pro DAO
public class ProdutoService {

    private ProdutoDAO dao = new ProdutoDAO();

    public boolean cadastrar(String nome, double preco, int quantidade) {
        if (nome.trim().isEmpty() || preco <= 0 || quantidade < 0) {
            return false;
        }

        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setPreco(preco);
        produto.setQuantidade(quantidade);
        dao.inserir(produto);
        return true;
    }

    public List<Produto> listar() {
        return dao.listar();
    }

    public boolean atualizar(int id, String nome, double preco, int quantidade) {
        if (nome.trim().isEmpty() || preco <= 0 || quantidade < 0) {
            return false;
        }

        Produto produto = new Produto();
        produto.setId(id);
        produto.setNome(nome);
        produto.setPreco(preco);
        produto.setQuantidade(quantidade);
        dao.atualizar(produto);
        return true;
    }

    public void deletar(int id) {
        dao.deletar(id);
    }
}
