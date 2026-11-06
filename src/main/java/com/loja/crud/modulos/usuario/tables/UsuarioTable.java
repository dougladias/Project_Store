package com.loja.crud.modulos.usuario.tables;

import com.loja.crud.infra.database.Database;
import com.loja.crud.modulos.usuario.entity.Endereco;
import com.loja.crud.modulos.usuario.entity.Usuario;
import com.loja.crud.modulos.usuario.exception.UsuarioException;
import com.loja.crud.modulos.usuario.mapper.UsuarioMapper;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioTable {

    private static final String COLUNAS = "u.id, u.nome, u.cpf, u.email, u.telefone, u.data_nascimento, "
            + "e.id AS endereco_id, e.usuario_id AS endereco_usuario_id, e.rua, e.bairro, e.numero, e.cep";

    private final Database database = new Database();
    private final UsuarioMapper mapper = new UsuarioMapper();

    public int inserir(Usuario usuario) {
        String sqlEndereco = "INSERT INTO endereco (rua, bairro, numero, cep, usuario_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = database.getConnection()) {
            conexao.setAutoCommit(false);

            try {
                int usuarioId = inserirUsuario(conexao, usuario);
                if (usuarioId <= 0) {
                    conexao.rollback();
                    return 0;
                }

                try (PreparedStatement stmt = conexao.prepareStatement(sqlEndereco)) {
                    preencherEndereco(stmt, usuario.getEndereco());
                    stmt.setInt(5, usuarioId);

                    if (stmt.executeUpdate() != 1) {
                        conexao.rollback();
                        return 0;
                    }
                }

                conexao.commit();
                return usuarioId;
            } catch (SQLException e) {
                conexao.rollback();
                throw e;
            }
        } catch (SQLException e) {
            boolean ehConflito = ehConflito(e);
            throw new UsuarioException(ehConflito ? "CPF ja cadastrado" : "Erro ao inserir usuario", e, ehConflito);
        }
    }

    public List<Usuario> listar() {
        String sql = "SELECT " + COLUNAS + " FROM usuario u INNER JOIN endereco e ON e.usuario_id = u.id ORDER BY u.nome";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                try {
                    usuarios.add(mapper.map(rs));
                } catch (IllegalArgumentException e) {
                    throw new UsuarioException("Usuario com dados invalidos", e);
                }
            }
        } catch (SQLException e) {
            throw new UsuarioException("Erro ao listar usuarios", e);
        }

        return usuarios;
    }

    public Usuario find(int id) {
        if (id <= 0) {
            return null;
        }

        String sql = Database.query("usuario u")
                .select(COLUNAS)
                .innerJoin("endereco e", "e.usuario_id = u.id")
                .where("u.id = ?")
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
                    throw new UsuarioException("Usuario com dados invalidos", e);
                }
            }
        } catch (SQLException e) {
            throw new UsuarioException("Erro ao buscar usuario", e);
        }
    }

    public Usuario get_id(int id) {
        return find(id);
    }

    public boolean existe(int id) {
        if (id <= 0) {
            return false;
        }

        String sql = "SELECT 1 FROM usuario WHERE id = ?";

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new UsuarioException("Erro ao verificar usuario", e);
        }
    }

    public boolean atualizar(Usuario usuario) {
        String sqlUsuario = "UPDATE usuario SET nome = ?, cpf = ?, email = ?, telefone = ?, data_nascimento = ? WHERE id = ?";
        String sqlEndereco = "UPDATE endereco SET rua = ?, bairro = ?, numero = ?, cep = ? WHERE usuario_id = ?";

        try (Connection conexao = database.getConnection()) {
            conexao.setAutoCommit(false);

            try (PreparedStatement stmtUsuario = conexao.prepareStatement(sqlUsuario)) {
                preencherUsuario(stmtUsuario, usuario);
                stmtUsuario.setInt(6, usuario.getId());

                if (stmtUsuario.executeUpdate() != 1) {
                    conexao.rollback();
                    return false;
                }

                try (PreparedStatement stmtEndereco = conexao.prepareStatement(sqlEndereco)) {
                    preencherEndereco(stmtEndereco, usuario.getEndereco());
                    stmtEndereco.setInt(5, usuario.getId());

                    if (stmtEndereco.executeUpdate() == 0) {
                        if (!inserirEndereco(conexao, usuario.getId(), usuario.getEndereco())) {
                            conexao.rollback();
                            return false;
                        }
                    }
                }

                conexao.commit();
                return true;
            } catch (SQLException e) {
                conexao.rollback();
                throw e;
            }
        } catch (SQLException e) {
            boolean ehConflito = ehConflito(e);
            throw new UsuarioException(ehConflito ? "CPF ja cadastrado" : "Erro ao atualizar usuario", e, ehConflito);
        }
    }

    public boolean deletar(int id) {
        String sql = "DELETE FROM usuario WHERE id = ?";

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            throw new UsuarioException("Erro ao deletar usuario", e);
        }
    }

    private boolean ehConflito(SQLException e) {
        return "23000".equals(e.getSQLState()) || e.getErrorCode() == 1062;
    }

    private int inserirUsuario(Connection conexao, Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (nome, cpf, email, telefone, data_nascimento) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preencherUsuario(stmt, usuario);

            if (stmt.executeUpdate() != 1) {
                return 0;
            }

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : 0;
            }
        }
    }

    private boolean inserirEndereco(Connection conexao, int usuarioId, Endereco endereco) throws SQLException {
        String sql = "INSERT INTO endereco (rua, bairro, numero, cep, usuario_id) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            preencherEndereco(stmt, endereco);
            stmt.setInt(5, usuarioId);
            return stmt.executeUpdate() == 1;
        }
    }

    private void preencherUsuario(PreparedStatement stmt, Usuario usuario) throws SQLException {
        stmt.setString(1, usuario.getNome());
        stmt.setString(2, usuario.getCpf().getDigitos());
        stmt.setString(3, usuario.getEmail().getValor());
        stmt.setString(4, usuario.getTelefone().getDigitos());
        stmt.setDate(5, Date.valueOf(usuario.getDataNascimento()));
    }

    private void preencherEndereco(PreparedStatement stmt, Endereco endereco) throws SQLException {
        stmt.setString(1, endereco.getRua());
        stmt.setString(2, endereco.getBairro());
        stmt.setString(3, endereco.getNumero());
        stmt.setString(4, endereco.getCEP());
    }
}
