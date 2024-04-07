package com.example.dechivejavafx.db;

import com.example.dechivejavafx.Validacoes.TabelasSped;
import com.example.dechivejavafx.gui.util.CSVUtils;
import com.example.dechivejavafx.gui.util.Utils;

import java.io.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

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

    /**
     * Executa uma consulta para obter dados do registro 9900 do SPED a partir de uma data de início.
     *
     * @param startDate A data de início para a consulta.
     * @return O resultado da consulta como um conjunto de resultados (ResultSet).
     */
    public ResultSet executeSpedReg9900Query(LocalDateTime startDate) {
        // Constrói a consulta SQL usando o método queryHiveSpedReg9900 para obter a consulta específica do registro 9900
        String query = queryHiveSpedReg9900(startDate);

        try {
            // Prepara uma declaração PreparedStatement usando a conexão atual e a consulta SQL construída
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            // Define os parâmetros da consulta, neste caso, a data de início formatada como uma string no formato "yyyy-MM-dd HH:mm:ss"
            preparedStatement.setString(1, startDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            // Imprime a consulta executada para depuração
            System.out.println("Query executada: " + query);

            // Executa a consulta preparada e retorna o conjunto de resultados
            return preparedStatement.executeQuery();
        } catch (SQLException e) {
            // Em caso de erro durante a execução da consulta, imprime uma mensagem de erro
            System.err.println("Erro ao executar a consulta: " + e.getMessage());
            return null; // Retorna null em caso de erro
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

            // Truncar o arquivo CSV existente antes de iniciar a interação
            truncateCSVFile(nomeArquivo);

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

    private static void truncateCSVFile(String fileName) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            // Arquivo está vazio agora
        } catch (IOException e) {
            System.err.println("Erro ao truncar o arquivo CSV: " + e.getMessage());
        }
    }
    /**
     * Executa uma consulta SPED e anexa os resultados a um arquivo CSV.
     *
     * @param query A consulta a ser executada.
     * @param fileName O nome do arquivo CSV onde os resultados serão anexados.
     */
    private static void executeSpedQueryAndAppendToCSV(String query, String fileName) {
        try {
            // Verifica se a conexão está inicializada corretamente
            if (connection != null) {
                // Cria uma declaração SQL
                Statement statement = connection.createStatement();

                // Executa a consulta
                ResultSet resultSet = statement.executeQuery(query);

                // Abre um FileWriter para escrever no arquivo CSV (usando append)
                FileWriter csvWriter = new FileWriter(fileName, true);

                // Escreve os resultados no arquivo CSV
                while (resultSet.next()) {
                    int columnCount = resultSet.getMetaData().getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        csvWriter.append(resultSet.getString(i));
                        // Adiciona uma vírgula entre os valores, exceto para o último valor
                        if (i < columnCount) {
                            csvWriter.append(",");
                        }
                    }
                    // Adiciona uma nova linha após cada linha de resultados
                    csvWriter.append("\n");
                }

                // Fecha os recursos (ResultSet, Statement, FileWriter)
                resultSet.close();
                statement.close();
                csvWriter.close();

                // Imprime uma mensagem de sucesso
                System.out.println("Resultados da consulta foram adicionados a: " + fileName);
            } else {
                // Imprime uma mensagem de erro se a conexão não estiver inicializada corretamente
                System.err.println("Conexão não inicializada corretamente.");
            }
        } catch (Exception e) {
            // Em caso de exceção, imprime uma mensagem de erro
            System.err.println("Erro ao executar a consulta: " + e.getMessage());
        }
    }

    /**
     * Executa uma consulta com junção (join) e grava os resultados no arquivo de saída especificado.
     *
     * @param filePath O caminho do arquivo de entrada contendo os dados a serem consultados.
     * @param outputFilePath9900OracleTabelasHive O caminho do arquivo de saída onde os resultados serão gravados.
     * @throws SQLException Em caso de erro de SQL durante a execução da consulta.
     * @throws IOException Em caso de erro de E/S durante a gravação dos resultados no arquivo de saída.
     */
    public void executeQueryWithJoinAndWriteResults(String filePath, String outputFilePath9900OracleTabelasHive) throws SQLException, IOException {
        // Verifica se a conexão está inicializada corretamente
        if (connection == null) {
            System.err.println("Conexão não inicializada corretamente.");
            return;
        }

        // Abre um BufferedWriter para escrever no arquivo de saída
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFilePath9900OracleTabelasHive))) {
            // Itera sobre cada tabela Sped
            for (TabelasSped tabela : TabelasSped.values()) {
                // Carrega os IDs distintos do arquivo CSV fornecido para a tabela atual
                Set<String> ids = CSVUtils.loadDistinctIdsFromCSV(filePath, tabela);

                // Verifica se não há IDs para a tabela atual
                if (ids.isEmpty()) {
                    System.out.println("Nenhum ID encontrado para a tabela " + tabela.getFormattedName());
                    continue;
                }

                // Divide os IDs em lotes de tamanho especificado (10000 neste caso)
                List<List<String>> idBatches = Utils.partitionIdsIntoBatches(ids, 10000);

                // Itera sobre cada lote de IDs
                for (List<String> idBatch : idBatches) {
                    // Constrói a consulta SQL usando os IDs do lote atual
                    String query = String.format(
                            "SELECT DISTINCT id_base FROM seec_prd_declaracao_fiscal.tb_sped_reg_%s r WHERE r.id_base IN (%s)",
                            tabela.getFormattedName(),
                            String.join(",", Collections.nCopies(idBatch.size(), "?"))
                    );
                    System.out.println("Query a ser executada: " + query);

                    // Prepara a declaração PreparedStatement com a consulta SQL construída
                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        int i = 1;
                        // Define os parâmetros da consulta com os IDs do lote atual
                        for (String id : idBatch) {
                            statement.setString(i++, id);
                        }

                        // Executa a consulta
                        try (ResultSet resultSet = statement.executeQuery()) {
                            // Cria um conjunto para armazenar os IDs encontrados na consulta
                            Set<String> foundIds = new HashSet<>();
                            // Itera sobre os resultados da consulta
                            while (resultSet.next()) {
                                // Adiciona o ID encontrado ao conjunto
                                foundIds.add(resultSet.getString(1));
                            }

                            // Identifica os IDs não encontrados na consulta
                            Set<String> notFoundIds = new HashSet<>(idBatch);
                            notFoundIds.removeAll(foundIds);
                            // Escreve os IDs não encontrados no arquivo de saída, junto com o nome da tabela
                            for (String id : notFoundIds) {
                                writer.write(id + ", " + tabela.getFormattedName() + "\n");
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            // Em caso de erro de E/S durante a gravação dos resultados, imprime uma mensagem de erro
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
        }
    }

}
