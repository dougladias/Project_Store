package com.loja.crud.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

// Database cuida da conexao com o banco loja
public class Database {
    private String url = "jdbc:mysql://localhost:3306/loja";
    private String usuario = "root";
    private String senha = "";

    private Connection conexao;

    public Database() {
        try {
            this.conexao = DriverManager.getConnection(this.url, this.usuario, this.senha);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        if (this.conexao == null || this.conexao.isClosed()) {
            this.conexao = DriverManager.getConnection(this.url, this.usuario, this.senha);
        }
        return this.conexao;
    }

    public void closeConnection() {
        if (this.conexao != null) {
            try {
                this.conexao.close();
                this.conexao = null;
            } catch (SQLException e) {
                System.err.println("Erro ao fechar a conexao: " + e.getMessage());
            }
        }
    }
}
