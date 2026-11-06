package com.loja.crud.modulos.produto.mapper;

import com.loja.crud.modulos.produto.entity.Produto;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProdutoMapper {

    public Produto map(ResultSet rs) throws SQLException {
        return new Produto(
                rs.getInt("id"),
                rs.getString("nome"),
                Produto.Unidade.valueOf(rs.getString("unidade")),
                rs.getBigDecimal("preco"),
                rs.getBigDecimal("quantidade"),
                rs.getString("imagem")
        );
    }
}
