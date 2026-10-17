package com.loja.crud.dao;

import com.loja.crud.database.Database;
import com.loja.crud.model.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Conversa com o banco, faz o CRUD do produto (inserir, listar, atualizar, deletar)
public class ProdutoDAO {

    private Database database = new Database();

    public void inserir(Produto produto) {
        String sql = "INSERT INTO produto (nome, preco, quantidade) VALUES (?, ?, ?)";

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setInt(3, produto.getQuantidade());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao inserir produto: " + e.getMessage());
        }
    }

    public List<Produto> listar() {
        String sql = "SELECT * FROM produto";
        Map<Integer, Produto> memoria = new HashMap<>();
        List<Produto> produtos = new ArrayList<>();

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Produto produto = new Produto();
                produto.setId(rs.getInt("id"));
                produto.setNome(rs.getString("nome"));
                produto.setPreco(rs.getDouble("preco"));
                produto.setQuantidade(rs.getInt("quantidade"));
                memoria.put(produto.getId(), produto);
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar produtos: " + e.getMessage());
        }

        int contador = 0;
        for (Map.Entry<Integer, Produto> entry : memoria.entrySet()) {
            contador++;
            Produto produto = entry.getValue();
            produto.setId(entry.getKey());
            produtos.add(produto);
        }

        produtos.sort((a, b) -> a.getId() - b.getId());

        System.out.println("Total de produtos listados: " + contador);

        return produtos;
    }

    public void atualizar(Produto produto) {
        String sql = "UPDATE produto SET nome = ?, preco = ?, quantidade = ? WHERE id = ?";

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setString(1, produto.getNome());
            stmt.setDouble(2, produto.getPreco());
            stmt.setInt(3, produto.getQuantidade());
            stmt.setInt(4, produto.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar produto: " + e.getMessage());
        }
    }

    public void deletar(int id) {
        String sql = "DELETE FROM produto WHERE id = ?";

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Erro ao deletar produto: " + e.getMessage());
        }
    }
}
