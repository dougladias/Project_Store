package com.loja.crud.modulos.usuario.tables;

import com.loja.crud.infra.database.Database;
import com.loja.crud.modulos.usuario.entity.Endereco;
import com.loja.crud.modulos.usuario.exception.EnderecoException;
import com.loja.crud.modulos.usuario.mapper.EnderecoMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EnderecoTable {

    private static final String COLUNAS = "id AS endereco_id, usuario_id AS endereco_usuario_id, rua, bairro, numero, cep";

    private final Database database = new Database();
    private final EnderecoMapper mapper = new EnderecoMapper();

    public int inserir(int usuarioId, Endereco endereco) {
        if (usuarioId <= 0 || endereco == null) {
            return 0;
        }

        String sql = "INSERT INTO endereco (rua, bairro, numero, cep, usuario_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherEndereco(stmt, endereco);
            stmt.setInt(5, usuarioId);

            if (stmt.executeUpdate() != 1) {
                return 0;
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        } catch (SQLException e) {
            boolean ehConflito = ehConflito(e);
            throw new EnderecoException(ehConflito ? "Usuario ja possui endereco" : "Erro ao inserir endereco", e, ehConflito);
        }
    }

    public List<Endereco> listar() {
        String sql = "SELECT " + COLUNAS + " FROM endereco ORDER BY id";
        List<Endereco> enderecos = new ArrayList<>();

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                try {
                    enderecos.add(mapper.map(rs));
                } catch (IllegalArgumentException e) {
                    throw new EnderecoException("Endereco com dados invalidos", e);
                }
            }
        } catch (SQLException e) {
            throw new EnderecoException("Erro ao listar enderecos", e);
        }

        return enderecos;
    }

    public Endereco find(int id) {
        if (id <= 0) {
            return null;
        }

        String sql = Database.query("endereco")
                .select(COLUNAS)
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
                    throw new EnderecoException("Endereco com dados invalidos", e);
                }
            }
        } catch (SQLException e) {
            throw new EnderecoException("Erro ao buscar endereco", e);
        }
    }

    public Endereco get_id(int id) {
        return find(id);
    }

    public Endereco findByUsuario(int usuarioId) {
        if (usuarioId <= 0) {
            return null;
        }

        String sql = Database.query("endereco")
                .select(COLUNAS)
                .where("usuario_id = ?")
                .build();

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, usuarioId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                try {
                    return mapper.map(rs);
                } catch (IllegalArgumentException e) {
                    throw new EnderecoException("Endereco com dados invalidos", e);
                }
            }
        } catch (SQLException e) {
            throw new EnderecoException("Erro ao buscar endereco do usuario", e);
        }
    }

    public boolean atualizar(int id, Endereco endereco) {
        if (id <= 0 || endereco == null) {
            return false;
        }

        String sql = "UPDATE endereco SET rua = ?, bairro = ?, numero = ?, cep = ? WHERE id = ?";

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            preencherEndereco(stmt, endereco);
            stmt.setInt(5, id);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new EnderecoException("Erro ao atualizar endereco", e);
        }
    }

    private boolean ehConflito(SQLException e) {
        return "23000".equals(e.getSQLState()) || e.getErrorCode() == 1062;
    }

    private void preencherEndereco(PreparedStatement stmt, Endereco endereco) throws SQLException {
        stmt.setString(1, endereco.getRua());
        stmt.setString(2, endereco.getBairro());
        stmt.setString(3, endereco.getNumero());
        stmt.setString(4, endereco.getCEP());
    }
}
