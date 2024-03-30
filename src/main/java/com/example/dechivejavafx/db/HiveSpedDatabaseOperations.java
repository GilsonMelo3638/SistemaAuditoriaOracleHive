package com.example.dechivejavafx.db;

import com.example.dechivejavafx.Validacoes.TabelasSped;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.FileWriter;

public class HiveSpedDatabaseOperations {
    private static Connection connection;

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
        String query = queryHiveSpedReg9900(startDate);

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

    private String queryHiveSpedReg9900(LocalDateTime startDate) {
        return "SELECT datahora_fin, id_base, linha, qtd_reg_blc, reg, reg_blc " +
                "FROM seec_prd_declaracao_fiscal.tb_sped_reg_9900 " +
                "WHERE datahora_fin >= ?";
    }

    public static void divergencia9900TabelasSpedAndWriteToCSV(LocalDateTime startDate, String fileName) {
        try {
            // Diretório e nome do arquivo para salvar os resultados CSV
            String diretorio = "X:\\Dados\\SPED\\";
            String nomeArquivo = diretorio + "divergencia9900TabelasSped.csv";

            // Loop sobre os elementos da classe enumerada TabelasSped
            for (TabelasSped regBlc : TabelasSped.values()) {
                // Constrói a nova query com a condição de data e o join entre as tabelas
                String novaQuery = "SELECT \n" +
                        "    tb_sped_base.id AS id_base,\n" +
                        "    tb_sped_base.datahora_processamento,\n" +
                        "    tb_sped_base.status_processamento,\n" +
                        "    tb_sped_reg_9900.datahora_fin,\n" +
                        "    tb_sped_reg_9900.reg,\n" +
                        "    tb_sped_reg_9900.reg_blc\n" +
                        "FROM seec_prd_declaracao_fiscal.tb_sped_reg_9900\n" +
                        "JOIN seec_prd_declaracao_fiscal.tb_sped_base ON tb_sped_base.id = tb_sped_reg_9900.id_base\n" +
                        "WHERE tb_sped_reg_9900.reg_blc = '" + regBlc.getFormattedName() + "'\n" +
                        "AND tb_sped_reg_9900.datahora_fin >= '" + startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "'\n" +
                        "AND NOT EXISTS (\n" +
                        "    SELECT 1\n" +
                        "    FROM seec_prd_declaracao_fiscal.tb_sped_reg_" + regBlc.getFormattedName().toLowerCase() + "\n" +
                        "    WHERE tb_sped_reg_" + regBlc.getFormattedName().toLowerCase() + ".id_base = tb_sped_reg_9900.id_base\n" +
                        "    AND tb_sped_reg_" + regBlc.getFormattedName().toLowerCase() + ".datahora_fin >= '" + startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "'\n" +
                        ")";

                System.out.println("Query a ser executada: " + novaQuery);

                // Chamada do método executeQueryAndAppendToCSV para executar a consulta e adicionar os resultados ao arquivo CSV
                executeSpedQueryAndAppendToCSV(novaQuery, nomeArquivo);
            }
        } catch (Exception e) {
            System.err.println("Erro ao executar a consulta e gravar resultados no arquivo CSV: " + e.getMessage());
        }
    }
    private static void executeSpedQueryAndAppendToCSV(String query, String fileName) {
        try {
            if (connection != null) {
                // Cria uma instrução SQL
                Statement statement = connection.createStatement();

                // Executa a consulta
                ResultSet resultSet = statement.executeQuery(query);

                // Cria um FileWriter para escrever no arquivo CSV (usando append)
                FileWriter csvWriter = new FileWriter(fileName, true);

                // Escreve os resultados
                while (resultSet.next()) {
                    int columnCount = resultSet.getMetaData().getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        csvWriter.append(resultSet.getString(i));
                        if (i < columnCount) {
                            csvWriter.append(",");
                        }
                    }
                    csvWriter.append("\n");
                }

                // Fecha os recursos
                resultSet.close();
                statement.close();
                csvWriter.close();

                System.out.println("Resultados da consulta foram adicionados a: " + fileName);
            } else {
                System.err.println("Conexão não inicializada corretamente.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao executar a consulta: " + e.getMessage());
        }
    }

}
