package com.loja.crud.tables;

import com.loja.crud.exception.ProdutoException;
import com.loja.crud.database.Database;
import com.loja.crud.mapper.ProdutoMapper;
import com.loja.crud.entity.Produto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProdutoTable {

    private final Database database = new Database();
    private final ProdutoMapper mapper = new ProdutoMapper();

    public int inserir(Produto produto) {
        String sql = "INSERT INTO produto (nome, preco, quantidade) VALUES (?, ?, ?)";

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherParametros(stmt, produto);

            if (stmt.executeUpdate() != 1) {
                return 0;
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new ProdutoException("Erro ao inserir produto", e);
        }
    }

    public List<Produto> listar() {
        String sql = "SELECT id, nome, preco, quantidade FROM produto ORDER BY id";
        List<Produto> produtos = new ArrayList<>();

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                try {
                    produtos.add(mapper.map(rs));
                } catch (IllegalArgumentException e) {
                    throw new ProdutoException("Produto com dados invalidos", e);
                }
            }
        } catch (SQLException e) {
            throw new ProdutoException("Erro ao listar produtos", e);
        }

        return produtos;
    }

    public Produto find(int id) {
        if (id <= 0) {
            return null;
        }

        String sql = Database.query("produto")
                .select("id, nome, preco, quantidade")
                .where("id = ?")
                .build();

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                try {
                    return mapper.map(rs);
                } catch (IllegalArgumentException e) {
                    throw new ProdutoException("Produto com dados invalidos", e);
                }
            }
        } catch (SQLException e) {
            throw new ProdutoException("Erro ao buscar produto", e);
        }
    }

    public Produto get_id(int id) {
        return find(id);
    }

    public boolean atualizar(Produto produto) {
        String sql = "UPDATE produto SET nome = ?, preco = ?, quantidade = ? WHERE id = ?";

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            preencherParametros(stmt, produto);
            stmt.setInt(4, produto.getId());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new ProdutoException("Erro ao atualizar produto", e);
        }
    }

    public boolean deletar(int id) {
        String sql = "DELETE FROM produto WHERE id = ?";

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new ProdutoException("Erro ao deletar produto", e);
        }
    }

    private void preencherParametros(PreparedStatement stmt, Produto produto) throws SQLException {
        stmt.setString(1, produto.getNome());
        stmt.setBigDecimal(2, produto.getPreco());
        stmt.setInt(3, produto.getQuantidade());
    }
}
