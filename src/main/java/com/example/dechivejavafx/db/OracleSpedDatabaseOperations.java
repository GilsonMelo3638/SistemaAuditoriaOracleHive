package com.example.dechivejavafx.db;

import com.example.dechivejavafx.gui.util.Configuracao;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class OracleSpedDatabaseOperations {

    private String jdbcURL;
    private String username;
    private String password;

    public OracleSpedDatabaseOperations() {
        this.jdbcURL = System.getenv("SPED_JDBC_URL");
        this.username = System.getenv("SPED_JDBC_USER");
        this.password = System.getenv("SPED_JDBC_PASSWORD");
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

            // Obtém a data atual e subtrai 15 dias
            LocalDateTime dataAtual = LocalDateTime.now().minusDays(Configuracao.diasSped);

            // Obtém a data de dois dias atrás à meia-noite
            LocalDateTime dataRecuo = LocalDateTime.now().minusDays(Configuracao.diasSpedRecuo).withHour(0).withMinute(0).withSecond(0);

            // Formata as datas conforme o padrão esperado pelo Oracle
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String dataFormatada = dataAtual.format(formatter);
            String dataRecuoFormatada = dataRecuo.format(formatter);


            // Query SQL com LEFT JOIN usando ID como correspondência entre as tabelas
            String query = "SELECT b.ID, b.datahora_processamento, b.status_processamento, " +
                    "    r.id_base, r.linha, r.qtd_reg_blc, r.reg, r.reg_blc " +
                    "FROM ADMSPED.SPED_base b " +
                    "LEFT JOIN ADMSPED.SPED_REG_9900 r " +
                    "ON b.ID = r.ID_BASE " +
                    "WHERE (b.status_processamento IN ('10', '12', '13', '14')) " +
                    "AND b.datahora_processamento >= TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS') " +
                    "AND b.datahora_processamento <= TO_TIMESTAMP(?, 'YYYY-MM-DD HH24:MI:SS')";

            // Nome do arquivo CSV e caminho
            String filePath = "X:\\Dados\\SPED\\ORACLE_SPED_9900.csv";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                // Configura os parâmetros da consulta
                preparedStatement.setString(1, dataFormatada);
                preparedStatement.setString(2, dataRecuoFormatada);

                // Executa a consulta e processa os resultados
                try (ResultSet resultSet = preparedStatement.executeQuery();
                     FileWriter csvWriter = new FileWriter(filePath)) {

                    // Escreve o cabeçalho do CSV
                    csvWriter.append("id_base,Data Processamento,Status de Processamento,Linha,Quantidade Reg Blc,Reg,Reg Blc\n");

                    // Processa os resultados e escreve no arquivo CSV
                    while (resultSet.next()) {
                        int idBase = resultSet.getInt("id_base");
                        String datahoraProcessamento = resultSet.getString("datahora_processamento");
                        String statusProcessamento = resultSet.getString("status_processamento");
                        String linha = resultSet.getString("linha");
                        int qtdRegBlc = resultSet.getInt("qtd_reg_blc");
                        String reg = resultSet.getString("reg");
                        String regBlc = resultSet.getString("reg_blc");

                        // Escreve uma linha no CSV
                        csvWriter.append(idBase + "," + datahoraProcessamento + "," + statusProcessamento + ","
                                + linha + "," + qtdRegBlc + "," + reg + "," + regBlc + "\n");
                    }

                    System.out.println("Dados salvos com sucesso no arquivo CSV: " + filePath);

                } catch (IOException e) {
                    System.err.println("Erro ao escrever no arquivo CSV: " + e.getMessage());
                }

            } catch (SQLException e) {
                System.err.println("Erro ao executar a consulta: " + e.getMessage());
            }

        } catch (SQLException e) {
            System.err.println("Erro ao conectar ao banco de dados: " + e.getMessage());
        }
    }
}
