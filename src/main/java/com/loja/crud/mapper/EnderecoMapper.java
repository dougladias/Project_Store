package com.loja.crud.mapper;

import com.loja.crud.entity.Usuario.Endereco;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EnderecoMapper implements RowMapper<Endereco> {

    @Override
    public Endereco map(ResultSet rs) throws SQLException {
        return new Endereco(
                rs.getString("rua"),
                rs.getString("bairro"),
                rs.getString("numero"),
                rs.getString("cep")
        );
    }
}
