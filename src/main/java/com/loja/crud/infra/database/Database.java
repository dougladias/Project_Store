package com.loja.crud.infra.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private final String url;
    private final String usuario;
    private final String senha;

    public Database() {
        this.url = configuracao("DB_URL", "jdbc:mysql://localhost:3306/loja");
        this.usuario = configuracao("DB_USER", "root");
        this.senha = configuracao("DB_PASSWORD", "");
    }

    public static QueryBuilder query(String from) {
        return new QueryBuilder(from);
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

    public static class QueryBuilder {

        private final String from;
        private final List<String> colunas = new ArrayList<>();
        private final List<String> joins = new ArrayList<>();
        private final List<String> condicoes = new ArrayList<>();
        private final List<String> conectores = new ArrayList<>();

        private QueryBuilder(String from) {
            if (from == null || from.isBlank()) {
                throw new IllegalArgumentException("Tabela invalida");
            }
            this.from = from.trim();
        }

        public QueryBuilder select(String... colunas) {
            if (colunas == null || colunas.length == 0) {
                throw new IllegalArgumentException("Colunas invalidas");
            }

            for (String coluna : colunas) {
                if (coluna == null || coluna.isBlank()) {
                    throw new IllegalArgumentException("Coluna invalida");
                }
                this.colunas.add(coluna.trim());
            }

            return this;
        }

        public QueryBuilder innerJoin(String tabela, String condicao) {
            if (tabela == null || tabela.isBlank() || condicao == null || condicao.isBlank()) {
                throw new IllegalArgumentException("Join invalido");
            }

            joins.add("INNER JOIN " + tabela.trim() + " ON " + condicao.trim());
            return this;
        }

        public QueryBuilder where(String condicao) {
            if (!condicoes.isEmpty()) {
                throw new IllegalStateException("WHERE ja definido");
            }

            adicionarCondicao("", condicao);
            return this;
        }

        public QueryBuilder and(String condicao) {
            adicionarCondicao("AND", condicao);
            return this;
        }

        public QueryBuilder or(String condicao) {
            adicionarCondicao("OR", condicao);
            return this;
        }

        public String build() {
            StringBuilder sql = new StringBuilder("SELECT ");

            sql.append(colunas.isEmpty() ? "*" : String.join(", ", colunas));
            sql.append(" FROM ").append(from);

            for (String join : joins) {
                sql.append(' ').append(join);
            }

            if (!condicoes.isEmpty()) {
                sql.append(" WHERE ");

                for (int i = 0; i < condicoes.size(); i++) {
                    if (i > 0) {
                        sql.append(' ').append(conectores.get(i)).append(' ');
                    }
                    sql.append(condicoes.get(i));
                }
            }

            return sql.toString();
        }

        private void adicionarCondicao(String conector, String condicao) {
            if (condicao == null || condicao.isBlank()) {
                throw new IllegalArgumentException("Condicao invalida");
            }
            if (condicoes.isEmpty() && !conector.isEmpty()) {
                throw new IllegalStateException(conector + " exige um WHERE definido antes");
            }

            condicoes.add(condicao.trim());
            conectores.add(conector);
        }
    }
}
