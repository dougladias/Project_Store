package com.loja.crud.modulos.compra.tables;

import com.loja.crud.infra.database.Database;
import com.loja.crud.modulos.compra.entity.Compra;
import com.loja.crud.modulos.compra.entity.ItemCompra;
import com.loja.crud.modulos.compra.exception.CompraException;
import com.loja.crud.modulos.compra.mapper.CompraMapper;
import com.loja.crud.modulos.compra.entity.FormaPagamento;
import com.loja.crud.modulos.compra.entity.StatusCompra;
import com.loja.crud.modulos.produto.entity.Produto;
import com.loja.crud.modulos.produto.tables.ProdutoTable;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CompraTable {

    private static final String COLUNAS = "c.id, c.usuario_id, u.nome AS usuario_nome, c.forma_pagamento, "
            + "c.valor_total, c.status, c.data_criacao, c.data_finalizacao";

    private final Database database = new Database();
    private final CompraMapper mapper = new CompraMapper();
    private final ItemCompraTable itemTable = new ItemCompraTable();
    private final ProdutoTable produtoTable = new ProdutoTable();

    public int inserir(int usuarioId) {
        if (usuarioId <= 0) {
            return 0;
        }

        String sql = "INSERT INTO compra (usuario_id, status, valor_total) VALUES (?, 'ABERTA', 0)";

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, usuarioId);

            if (stmt.executeUpdate() != 1) {
                return 0;
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            throw new CompraException("Erro ao criar compra", e);
        }
    }

    public Compra find(int id) {
        if (id <= 0) {
            return null;
        }

        String sql = Database.query("compra c")
                .select(COLUNAS)
                .innerJoin("usuario u", "u.id = c.usuario_id")
                .where("c.id = ?")
                .build();

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapper.map(rs) : null;
            }
        } catch (SQLException e) {
            throw new CompraException("Erro ao buscar compra", e);
        } catch (IllegalArgumentException e) {
            throw new CompraException("Compra com dados invalidos", e);
        }
    }

    public List<Compra> listar() {
        String sql = "SELECT " + COLUNAS + " FROM compra c "
                + "INNER JOIN usuario u ON u.id = c.usuario_id ORDER BY c.id";
        List<Compra> compras = new ArrayList<>();

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                compras.add(mapper.map(rs));
            }
        } catch (SQLException e) {
            throw new CompraException("Erro ao listar compras", e);
        } catch (IllegalArgumentException e) {
            throw new CompraException("Compra com dados invalidos", e);
        }

        return compras;
    }

    public Compra adicionarProduto(int compraId, int produtoId, int quantidade) {
        if (compraId <= 0 || produtoId <= 0 || quantidade <= 0) {
            throw new IllegalArgumentException("Dados do item invalidos");
        }

        try (Connection conexao = database.getConnection()) {
            conexao.setAutoCommit(false);

            try {
                StatusCompra status = statusParaAtualizacao(conexao, compraId);

                if (status == null) {
                    throw new IllegalArgumentException("Compra nao encontrada");
                }
                if (status != StatusCompra.ABERTA) {
                    throw new CompraException("Compra ja finalizada", null, true);
                }

                Produto produto = produtoTable.findParaAtualizacao(conexao, produtoId);

                if (produto == null) {
                    throw new IllegalArgumentException("Produto nao encontrado");
                }

                ItemCompra item = itemTable.find(conexao, compraId, produtoId);
                int novaQuantidade = item == null ? quantidade : item.getQuantidade() + quantidade;

                if (BigDecimal.valueOf(novaQuantidade).compareTo(produto.getQuantidade()) > 0) {
                    throw new CompraException(
                            "Estoque insuficiente para o produto " + produto.getNome(), null, true);
                }

                if (item == null) {
                    if (!itemTable.inserir(conexao, compraId, produtoId, quantidade, produto.getPreco())) {
                        throw new CompraException("Erro ao adicionar produto na compra");
                    }
                } else if (!itemTable.atualizarQuantidade(conexao, compraId, produtoId, novaQuantidade)) {
                    throw new CompraException("Erro ao atualizar item da compra");
                }

                atualizarTotal(conexao, compraId);
                conexao.commit();
            } catch (SQLException | RuntimeException e) {
                conexao.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new CompraException("Erro ao adicionar produto na compra", e);
        }

        return find(compraId);
    }

    public Compra definirQuantidade(int compraId, int produtoId, int quantidade) {
        if (compraId <= 0 || produtoId <= 0 || quantidade <= 0) {
            throw new IllegalArgumentException("Dados do item invalidos");
        }

        try (Connection conexao = database.getConnection()) {
            conexao.setAutoCommit(false);

            try {
                StatusCompra status = statusParaAtualizacao(conexao, compraId);

                if (status == null) {
                    throw new IllegalArgumentException("Compra nao encontrada");
                }
                if (status != StatusCompra.ABERTA) {
                    throw new CompraException("Compra ja finalizada", null, true);
                }

                Produto produto = produtoTable.findParaAtualizacao(conexao, produtoId);

                if (produto == null) {
                    throw new IllegalArgumentException("Produto nao encontrado");
                }

                ItemCompra item = itemTable.find(conexao, compraId, produtoId);

                if (item == null) {
                    throw new IllegalArgumentException("Produto nao esta na compra");
                }
                if (BigDecimal.valueOf(quantidade).compareTo(produto.getQuantidade()) > 0) {
                    throw new CompraException(
                            "Estoque insuficiente para o produto " + produto.getNome(), null, true);
                }

                if (!itemTable.atualizarQuantidade(conexao, compraId, produtoId, quantidade)) {
                    throw new CompraException("Erro ao atualizar item da compra");
                }

                atualizarTotal(conexao, compraId);
                conexao.commit();
            } catch (SQLException | RuntimeException e) {
                conexao.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new CompraException("Erro ao atualizar item da compra", e);
        }

        return find(compraId);
    }

    public boolean cancelar(int compraId) {
        if (compraId <= 0) {
            throw new IllegalArgumentException("Compra invalida");
        }

        String sql = "DELETE FROM compra WHERE id = ? AND status = 'ABERTA'";

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, compraId);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new CompraException("Erro ao cancelar compra", e);
        }
    }

    public Compra removerProduto(int compraId, int produtoId) {
        if (compraId <= 0 || produtoId <= 0) {
            throw new IllegalArgumentException("Dados do item invalidos");
        }

        try (Connection conexao = database.getConnection()) {
            conexao.setAutoCommit(false);

            try {
                StatusCompra status = statusParaAtualizacao(conexao, compraId);

                if (status == null) {
                    throw new IllegalArgumentException("Compra nao encontrada");
                }
                if (status != StatusCompra.ABERTA) {
                    throw new CompraException("Compra ja finalizada", null, true);
                }

                if (!itemTable.remover(conexao, compraId, produtoId)) {
                    throw new IllegalArgumentException("Produto nao esta na compra");
                }

                atualizarTotal(conexao, compraId);
                conexao.commit();
            } catch (SQLException | RuntimeException e) {
                conexao.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new CompraException("Erro ao remover produto da compra", e);
        }

        return find(compraId);
    }

    public Compra finalizar(int compraId, FormaPagamento formaPagamento) {
        if (compraId <= 0 || formaPagamento == null) {
            throw new IllegalArgumentException("Dados da finalizacao invalidos");
        }

        try (Connection conexao = database.getConnection()) {
            conexao.setAutoCommit(false);

            try {
                StatusCompra status = statusParaAtualizacao(conexao, compraId);

                if (status == null) {
                    throw new IllegalArgumentException("Compra nao encontrada");
                }
                if (status != StatusCompra.ABERTA) {
                    throw new CompraException("Compra ja finalizada", null, true);
                }

                List<ItemCompra> itens = itemTable.listarParaFinalizacao(conexao, compraId);

                if (itens.isEmpty()) {
                    throw new IllegalArgumentException("Compra sem itens");
                }

                BigDecimal total = BigDecimal.ZERO;

                for (ItemCompra item : itens) {
                    Produto produto = produtoTable.findParaAtualizacao(conexao, item.getProdutoId());

                    if (produto == null) {
                        throw new IllegalArgumentException("Produto nao encontrado");
                    }
                    if (BigDecimal.valueOf(item.getQuantidade()).compareTo(produto.getQuantidade()) > 0) {
                        throw new CompraException(
                                "Estoque insuficiente para o produto " + produto.getNome(), null, true);
                    }
                    if (!produtoTable.decrementarEstoque(
                            conexao, item.getProdutoId(), BigDecimal.valueOf(item.getQuantidade()))) {
                        throw new CompraException(
                                "Estoque insuficiente para o produto " + produto.getNome(), null, true);
                    }

                    total = total.add(item.getSubtotal());
                }

                String sql = "UPDATE compra SET forma_pagamento = ?, valor_total = ?, "
                        + "status = 'FINALIZADA', data_finalizacao = NOW() WHERE id = ?";

                try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
                    stmt.setString(1, formaPagamento.name());
                    stmt.setBigDecimal(2, total);
                    stmt.setInt(3, compraId);

                    if (stmt.executeUpdate() != 1) {
                        throw new SQLException("Compra nao atualizada");
                    }
                }

                conexao.commit();
            } catch (SQLException | RuntimeException e) {
                conexao.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new CompraException("Erro ao finalizar compra", e);
        }

        return find(compraId);
    }

    private StatusCompra statusParaAtualizacao(Connection conexao, int compraId) throws SQLException {
        String sql = "SELECT status FROM compra WHERE id = ? FOR UPDATE";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, compraId);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? StatusCompra.from(rs.getString("status")) : null;
            }
        }
    }

    private void atualizarTotal(Connection conexao, int compraId) throws SQLException {
        BigDecimal total = itemTable.calcularTotal(conexao, compraId);

        try (PreparedStatement stmt = conexao.prepareStatement("UPDATE compra SET valor_total = ? WHERE id = ?")) {
            stmt.setBigDecimal(1, total);
            stmt.setInt(2, compraId);

            if (stmt.executeUpdate() != 1) {
                throw new SQLException("Compra nao atualizada");
            }
        }
    }
}
