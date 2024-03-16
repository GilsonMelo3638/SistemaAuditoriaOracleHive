package com.example.dechivejavafx.db;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HiveSpedDatabaseOperations {
    private Connection connection;

    public HiveSpedDatabaseOperations(String jdbcUrl, String user, String password) {
        try {
            // Carrega o driver JDBC do Hive
            Class.forName("org.apache.hive.jdbc.HiveDriver");

            // Estabelece a conexão
            this.connection = DriverManager.getConnection(jdbcUrl, user, password);

            System.out.println("Conexão estabelecida com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao conectar: " + e.getMessage());
        }
    }

    public ResultSet executeSpedReg9900Query(LocalDateTime startDate) {
        String query = buildSpedReg9900Query(startDate);

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            // Define os parâmetros da consulta
            preparedStatement.setString(1, startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            System.out.println("Query executada: " + query);

            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            System.err.println("Erro ao executar a consulta: " + e.getMessage());
            return null;
        }
    }

    public void disconnect() {
        try {
            // Fecha a conexão
            if (connection != null) {
                connection.close();
                System.out.println("Conexão encerrada.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao desconectar: " + e.getMessage());
        }
    }

    private String buildSpedReg9900Query(LocalDateTime startDate) {
        return "SELECT datahora_fin, id_base, linha, qtd_reg_blc, reg, reg_blc " +
                "FROM seec_prd_declaracao_fiscal.tb_sped_reg_9900 " +
                "WHERE datahora_fin >= ?";
    }
}
