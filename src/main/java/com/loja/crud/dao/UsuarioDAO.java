package com.loja.crud.dao;

import com.loja.crud.database.Database;
import com.loja.crud.model.Usuario;
import com.loja.crud.model.vo.Cpf;
import com.loja.crud.model.vo.Email;
import com.loja.crud.model.vo.Endereco;
import com.loja.crud.model.vo.Telefone;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    private final Database database = new Database();

    public boolean inserir(Usuario usuario) {
        String sql = "INSERT INTO usuario (nome, cpf, email, telefone, data_nascimento, rua, bairro, numero, cep) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            preencherParametros(stmt, usuario);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Erro ao inserir usuario: " + e.getMessage());
            return false;
        }
    }

    public List<Usuario> listar() {
        String sql = "SELECT id, nome, cpf, email, telefone, data_nascimento, rua, bairro, numero, cep FROM usuario ORDER BY id";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                try {
                    usuarios.add(montarUsuario(rs));
                } catch (IllegalArgumentException e) {
                    System.out.println("Usuario com dados invalidos ignorado: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            System.out.println("Erro ao listar usuarios: " + e.getMessage());
        }

        return usuarios;
    }

    public boolean atualizar(Usuario usuario) {
        String sql = "UPDATE usuario SET nome = ?, cpf = ?, email = ?, telefone = ?, data_nascimento = ?, rua = ?, bairro = ?, numero = ?, cep = ? WHERE id = ?";

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            preencherParametros(stmt, usuario);
            stmt.setInt(10, usuario.getId());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Erro ao atualizar usuario: " + e.getMessage());
            return false;
        }
    }

    public boolean deletar(int id) {
        String sql = "DELETE FROM usuario WHERE id = ?";

        try (Connection conexao = database.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

            stmt.setInt(1, id);
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            System.out.println("Erro ao deletar usuario: " + e.getMessage());
            return false;
        }
    }

    private void preencherParametros(PreparedStatement stmt, Usuario usuario) throws SQLException {
        Cpf cpf = usuario.getCpf();
        Email email = usuario.getEmail();
        Telefone telefone = usuario.getTelefone();
        Endereco endereco = usuario.getEndereco();
        LocalDate dataNascimento = usuario.getDataNascimento();

        stmt.setString(1, usuario.getNome());
        stmt.setString(2, cpf.getDigitos());
        stmt.setString(3, email.getValor());
        stmt.setString(4, telefone.getDigitos());
        stmt.setDate(5, Date.valueOf(dataNascimento));
        stmt.setString(6, endereco.getRua());
        stmt.setString(7, endereco.getBairro());
        stmt.setString(8, endereco.getNumero());
        stmt.setString(9, endereco.getCEP());
    }

    private Usuario montarUsuario(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String nome = rs.getString("nome");
        String cpf = rs.getString("cpf");
        String email = rs.getString("email");
        String telefone = rs.getString("telefone");
        Date dataSql = rs.getDate("data_nascimento");
        LocalDate dataNascimento = dataSql.toLocalDate();
        String rua = rs.getString("rua");
        String bairro = rs.getString("bairro");
        String numero = rs.getString("numero");
        String cep = rs.getString("cep");

        Cpf cpfValidado = new Cpf(cpf);
        Email emailValidado = new Email(email);
        Telefone telefoneValidado = new Telefone(telefone);
        Endereco endereco = new Endereco(rua, bairro, numero, cep);

        return new Usuario(id, nome, cpfValidado, emailValidado, telefoneValidado, dataNascimento, endereco);
    }
}
