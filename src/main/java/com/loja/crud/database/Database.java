package com.loja.crud.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {

    private final String url;
    private final String usuario;
    private final String senha;

    public Database() {
        this.url = configuracao("DB_URL", "jdbc:mysql://localhost:3306/loja");
        this.usuario = configuracao("DB_USER", "root");
        this.senha = configuracao("DB_PASSWORD", "");
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, usuario, senha);
    }

    private String configuracao(String chave, String padrao) {
        String valor = System.getProperty(chave);
        if (valor == null || valor.isBlank()) {
            valor = System.getenv(chave);
        }
        return valor == null || valor.isBlank() ? padrao : valor;
    }
}
