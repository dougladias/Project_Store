package com.loja.crud.mapper;

import com.loja.crud.entity.Usuario;
import com.loja.crud.entity.Usuario.Cpf;
import com.loja.crud.entity.Usuario.Email;
import com.loja.crud.entity.Usuario.Telefone;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class UsuarioMapper implements RowMapper<Usuario> {

    private final EnderecoMapper enderecoMapper = new EnderecoMapper();

    @Override
    public Usuario map(ResultSet rs) throws SQLException {
        Date dataSql = rs.getDate("data_nascimento");
        LocalDate dataNascimento = dataSql == null ? null : dataSql.toLocalDate();

        return new Usuario(
                rs.getInt("id"),
                rs.getString("nome"),
                new Cpf(rs.getString("cpf")),
                new Email(rs.getString("email")),
                new Telefone(rs.getString("telefone")),
                dataNascimento,
                enderecoMapper.map(rs)
        );
    }
}
