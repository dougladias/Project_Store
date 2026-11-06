package com.loja.crud.modulos.compra.tables;

import com.loja.crud.infra.database.Database;
import com.loja.crud.modulos.compra.entity.ItemCompra;
import com.loja.crud.modulos.compra.exception.CompraException;
import com.loja.crud.modulos.compra.mapper.ItemCompraMapper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ItemCompraTable {

    private static final String COLUNAS = "ci.id, ci.compra_id, ci.produto_id, p.nome AS produto_nome, "
            + "ci.quantidade, ci.preco_unitario";
    private static final String FROM = "compra_item ci INNER JOIN produto p ON p.id = ci.produto_id";

    private final Database database = new Database();
    private final ItemCompraMapper mapper = new ItemCompraMapper();

    public List<ItemCompra> listarPorCompra(int compraId) {
        if (compraId <= 0) {
            return List.of();
        }

        try (Connection conexao = database.getConnection()) {
            return listarPorCompra(conexao, compraId);
        } catch (SQLException e) {
            throw new CompraException("Erro ao listar itens da compra", e);
        }
    }

    public List<ItemCompra> listarTodos() {
        String sql = "SELECT " + COLUNAS + " FROM " + FROM + " ORDER BY ci.compra_id, ci.id";
        List<ItemCompra> itens = new ArrayList<>();

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                itens.add(mapper.map(rs));
            }
        } catch (SQLException e) {
            throw new CompraException("Erro ao listar itens das compras", e);
        }

        return itens;
    }

    public ItemCompra find(int compraId, int produtoId) {
        if (compraId <= 0 || produtoId <= 0) {
            return null;
        }

        try (Connection conexao = database.getConnection()) {
            return find(conexao, compraId, produtoId);
        } catch (SQLException e) {
            throw new CompraException("Erro ao buscar item da compra", e);
        }
    }

    List<ItemCompra> listarPorCompra(Connection conexao, int compraId) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM " + FROM + " WHERE ci.compra_id = ? ORDER BY ci.id";
        List<ItemCompra> itens = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, compraId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    itens.add(mapper.map(rs));
                }
            }
        }

        return itens;
    }

    List<ItemCompra> listarParaFinalizacao(Connection conexao, int compraId) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM " + FROM + " WHERE ci.compra_id = ? ORDER BY ci.produto_id FOR UPDATE";
        List<ItemCompra> itens = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, compraId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    itens.add(mapper.map(rs));
                }
            }
        }

        return itens;
    }

    ItemCompra find(Connection conexao, int compraId, int produtoId) throws SQLException {
        String sql = "SELECT " + COLUNAS + " FROM " + FROM
                + " WHERE ci.compra_id = ? AND ci.produto_id = ? FOR UPDATE";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, compraId);
            stmt.setInt(2, produtoId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapper.map(rs) : null;
            }
        }
    }

    boolean inserir(Connection conexao, int compraId, int produtoId, int quantidade, BigDecimal precoUnitario)
            throws SQLException {
        String sql = "INSERT INTO compra_item (compra_id, produto_id, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, compraId);
            stmt.setInt(2, produtoId);
            stmt.setInt(3, quantidade);
            stmt.setBigDecimal(4, precoUnitario);
            return stmt.executeUpdate() == 1;
        }
    }

    boolean atualizarQuantidade(Connection conexao, int compraId, int produtoId, int quantidade) throws SQLException {
        String sql = "UPDATE compra_item SET quantidade = ? WHERE compra_id = ? AND produto_id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, quantidade);
            stmt.setInt(2, compraId);
            stmt.setInt(3, produtoId);
            return stmt.executeUpdate() == 1;
        }
    }

    boolean remover(Connection conexao, int compraId, int produtoId) throws SQLException {
        String sql = "DELETE FROM compra_item WHERE compra_id = ? AND produto_id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, compraId);
            stmt.setInt(2, produtoId);
            return stmt.executeUpdate() == 1;
        }
    }

    BigDecimal calcularTotal(Connection conexao, int compraId) throws SQLException {
        String sql = "SELECT COALESCE(SUM(quantidade * preco_unitario), 0) FROM compra_item WHERE compra_id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, compraId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
        }
    }
}
