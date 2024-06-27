package com.example.dechivejavafx.db;

import com.example.dechivejavafx.gui.util.Configuracao;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class OracleDecDatabaseOperations {

    private String jdbcURL;
    private String username;
    private String password;

    public OracleDecDatabaseOperations() {
        this.jdbcURL = System.getenv("SPRING_DATASOURCE_URL");
        this.username = System.getenv("SPRING_DATASOURCE_USERNAME");
        this.password = System.getenv("SPRING_DATASOURCE_PASSWORD");
    }

    public void executeQueryAndSaveToCSV() {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            System.out.println("Driver Oracle JDBC registrado com sucesso!");
        } catch (ClassNotFoundException e) {
            DatabaseExceptions.handleException("Erro ao registrar o driver Oracle JDBC", e);
        }

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            System.out.println("Conexão com o Oracle estabelecida com sucesso!");

            // Calcula a data de 20 dias atrás
            LocalDate twentyDaysAgo = LocalDate.now().minusDays(Configuracao.dias);
            String formattedDate = twentyDaysAgo.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // Executa a primeira consulta e salva no arquivo CSV
            String query1 = "SELECT /*+first_rows(100) parallel(8) */ " +
                    "TO_CHAR(f.dhproc, 'YYYYMMDD') AS ARQUIVO, " +
                    "COUNT(*) AS total " +
                    "FROM dec_dfe_nf3e f " +
                    "WHERE f.dhproc >= TO_DATE('" + formattedDate + "', 'YYYY-MM-DD') " +
                    "AND existsNode(f.XML_DOCUMENTO, '//nf3e:gGrContrat', 'xmlns:nf3e=\"http://www.portalfiscal.inf.br/nf3e\"') = 1 " +
                    "GROUP BY TO_CHAR(f.dhproc, 'YYYYMMDD')";
            String filePath1 = "\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\gGrContratArquivos.csv";
            System.out.println("Executando consulta 1: " + query1);
            executeAndSaveQuery(connection, query1, filePath1);

            // Executa a segunda consulta e salva no arquivo CSV
            String query2 = "SELECT /*+first_rows(100) parallel(8) */ " +
                    "TO_CHAR(f.dhproc, 'YYYYMMDD') AS ARQUIVO, " +
                    "COUNT(*) AS total " +
                    "FROM dec_dfe_nf3e f " +
                    "WHERE f.dhproc >= TO_DATE('" + formattedDate + "', 'YYYY-MM-DD') " +
                    "AND existsNode(f.XML_DOCUMENTO, '//nf3e:gGrandFat', 'xmlns:nf3e=\"http://www.portalfiscal.inf.br/nf3e\"') = 1 " +
                    "GROUP BY TO_CHAR(f.dhproc, 'YYYYMMDD')";
            String filePath2 = "\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\gGrandFatArquivos.csv";
            System.out.println("Executando consulta 2: " + query2);
            executeAndSaveQuery(connection, query2, filePath2);

        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }

    private void executeAndSaveQuery(Connection connection, String query, String filePath) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery();
             FileWriter csvWriter = new FileWriter(filePath)) {

            // Escreve o cabeçalho do CSV
            csvWriter.append("ARQUIVO,TOTAL\n");

            // Processa os resultados e escreve no arquivo CSV
            while (resultSet.next()) {
                String arquivo = resultSet.getString("ARQUIVO");
                int total = resultSet.getInt("total");

                // Escreve uma linha no CSV
                csvWriter.append(arquivo + "," + total + "\n");
            }

            System.out.println("Dados salvos com sucesso no arquivo CSV: " + filePath);

        } catch (SQLException e) {
            System.err.println("Erro ao executar a consulta: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo CSV: " + e.getMessage());
        }
    }
}
