package com.loja.crud.modulos.usuario.mapper;

import com.loja.crud.modulos.usuario.entity.Endereco;

import java.sql.ResultSet;
import java.sql.SQLException;

public class EnderecoMapper {

    public Endereco map(ResultSet rs) throws SQLException {
        return new Endereco(
                rs.getInt("endereco_id"),
                rs.getInt("endereco_usuario_id"),
                rs.getString("rua"),
                rs.getString("bairro"),
                rs.getString("numero"),
                rs.getString("cep")
        );
    }
}
