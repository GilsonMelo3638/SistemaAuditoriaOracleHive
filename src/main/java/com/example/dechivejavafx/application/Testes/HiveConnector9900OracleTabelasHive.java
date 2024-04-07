package com.example.dechivejavafx.application.Testes;

import com.example.dechivejavafx.Validacoes.TabelasSped;

import java.io.*;
import java.sql.*;
import java.util.*;

public class HiveConnector9900OracleTabelasHive {
    private final int batchSize = 10000; // Definição do tamanho do lote
    private Connection connection;

    public HiveConnector9900OracleTabelasHive() {
        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
            String jdbcUrl = System.getenv("HIVE_JDBC_URL");
            String user = System.getenv("HIVE_USERNAME");
            String password = System.getenv("HIVE_PASSWORD");
            this.connection = DriverManager.getConnection(jdbcUrl, user, password);
            System.out.println("Conexão estabelecida com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao conectar: " + e.getMessage());
        }
    }

    public void executeQueryWithJoin(String filePath) throws SQLException, IOException {
        if (connection == null) {
            System.err.println("Conexão não inicializada corretamente.");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("X:\\Dados\\SPED\\ResultadoQuery.csv"))) {

            for (TabelasSped tabela : TabelasSped.values()) {
                Set<String> ids = loadDistinctIdsFromCSV(filePath, tabela);

                if (ids.isEmpty()) {
                    System.out.println("Nenhum ID encontrado para a tabela " + tabela.getFormattedName());
                    continue;
                }

                List<List<String>> idBatches = new ArrayList<>();
                List<String> currentBatch = new ArrayList<>();
                for (String id : ids) {
                    currentBatch.add(id);
                    if (currentBatch.size() == batchSize) {
                        idBatches.add(new ArrayList<>(currentBatch));
                        currentBatch.clear();
                    }
                }
                if (!currentBatch.isEmpty()) {
                    idBatches.add(new ArrayList<>(currentBatch));
                }

                for (List<String> idBatch : idBatches) {
                    String query = "SELECT DISTINCT id_base FROM seec_prd_declaracao_fiscal.tb_sped_reg_" + tabela.getFormattedName() + " r WHERE r.id_base IN (" + String.join(",", Collections.nCopies(idBatch.size(), "?")) + ")";

                    try (PreparedStatement statement = connection.prepareStatement(query)) {
                        int i = 1;
                        for (String id : idBatch) {
                            statement.setString(i++, id);
                        }

                        try (ResultSet resultSet = statement.executeQuery()) {
                            Set<String> foundIds = new HashSet<>();
                            while (resultSet.next()) {
                                String idBase = resultSet.getString(1);
                                foundIds.add(idBase);
                            }

                            Set<String> notFoundIds = new HashSet<>(idBatch);
                            notFoundIds.removeAll(foundIds);
                            for (String id : notFoundIds) {
                                // Escrever no arquivo invertendo a ordem das colunas e sem o cabeçalho
                                writer.write(id + ", " + tabela.getFormattedName() + "\n");
                            }
                        }
                    }
                    System.err.println("Query a ser executada: " + query);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
        }
    }


    public Set<String> loadDistinctIdsFromCSV(String filePath, TabelasSped tabela) throws IOException {
        Set<String> distinctIds = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                if (columns.length >= 7 && columns[6].trim().equals(tabela.getFormattedName())) {
                    String id = columns[0].trim();
                    distinctIds.add(id);
                }
            }
        } catch (IOException e) {
            throw e;
        }
        return distinctIds;
    }

    public void disconnect() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Conexão encerrada.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao desconectar: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        HiveConnector9900OracleTabelasHive connector = new HiveConnector9900OracleTabelasHive();
        try {
            connector.executeQueryWithJoin("X:\\Dados\\SPED\\ORACLE_SPED_9900.csv");
        } catch (Exception e) {
            System.err.println("Erro durante a execução: " + e.getMessage());
        }
        connector.disconnect();
    }
}
