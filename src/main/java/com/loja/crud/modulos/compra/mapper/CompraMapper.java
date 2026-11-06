package com.loja.crud.modulos.compra.mapper;

import com.loja.crud.modulos.compra.entity.Compra;
import com.loja.crud.modulos.compra.entity.ItemCompra;
import com.loja.crud.modulos.compra.entity.FormaPagamento;
import com.loja.crud.modulos.compra.entity.StatusCompra;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

public class CompraMapper {

    public Compra map(ResultSet rs) throws SQLException {
        return map(rs, List.of());
    }

    public Compra map(ResultSet rs, List<ItemCompra> itens) throws SQLException {
        Timestamp criacao = rs.getTimestamp("data_criacao");
        Timestamp finalizacao = rs.getTimestamp("data_finalizacao");
        String pagamento = rs.getString("forma_pagamento");

        return new Compra(
                rs.getInt("id"),
                rs.getInt("usuario_id"),
                rs.getString("usuario_nome"),
                pagamento == null ? null : FormaPagamento.from(pagamento),
                rs.getBigDecimal("valor_total"),
                StatusCompra.from(rs.getString("status")),
                criacao == null ? null : criacao.toLocalDateTime(),
                finalizacao == null ? null : finalizacao.toLocalDateTime(),
                itens
        );
    }
}
