package com.loja.crud.mapper;

import com.loja.crud.entity.Produto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProdutoMapper {

    public Produto map(ResultSet rs) throws SQLException {
        return new Produto(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getBigDecimal("preco"),
                rs.getInt("quantidade")
        );
    }
}
