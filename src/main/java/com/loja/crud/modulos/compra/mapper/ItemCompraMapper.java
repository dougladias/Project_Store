package com.loja.crud.modulos.compra.mapper;

import com.loja.crud.modulos.compra.entity.ItemCompra;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ItemCompraMapper {

    public ItemCompra map(ResultSet rs) throws SQLException {
        return new ItemCompra(
                rs.getInt("id"),
                rs.getInt("compra_id"),
                rs.getInt("produto_id"),
                rs.getString("produto_nome"),
                rs.getInt("quantidade"),
                rs.getBigDecimal("preco_unitario")
        );
    }
}
