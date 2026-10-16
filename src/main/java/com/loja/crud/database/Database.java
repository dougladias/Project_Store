package com.loja.crud.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Database cuida da conexao com o banco loja
public class Database {
    private static final String URL = "jdbc:mariadb://localhost:3306/loja";
    private static final String USUARIO = "root";
    private static final String SENHA = "";

    private Connection conexao = null;

    public Connection getConnection() throws SQLException {
        if (conexao == null || conexao.isClosed()) {
            conexao = DriverManager.getConnection(URL, USUARIO, SENHA);
        }
        return conexao;
    }

    public void closeConnection() {
        if (conexao != null) {
            try {
                conexao.close();
                conexao = null;
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexao: " + e.getMessage());
            }
        }
    }
}


