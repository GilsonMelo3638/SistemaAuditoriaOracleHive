package com.example.dechivejavafx.application.Testes;

import com.example.dechivejavafx.Validacoes.TabelasSped;

import java.io.*;
import java.nio.file.*;
import java.sql.*;
import java.util.*;

public class HiveConnector9900OracleTabelasHive {
    private Connection connection;
    private boolean isFirstIteration = true; // Variável para controlar a primeira iteração

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

        // 1. Iterar sobre todos os valores da enumeração TabelasSped
        for (TabelasSped tabela : TabelasSped.values()) {
            Set<String> ids = loadIdsFromCSV(filePath, tabela);

            // Verificar se a lista de IDs está vazia
            if (ids.isEmpty()) {
                System.out.println("Nenhum ID encontrado para a tabela " + tabela.getFormattedName());
                continue; // Passar para a próxima tabela
            }

            // Modificar a query apenas se houver IDs
            String query = "SELECT DISTINCT r.id_base, '" + tabela.getFormattedName() + "' as tabela FROM seec_prd_declaracao_fiscal.tb_sped_reg_9900 r LEFT JOIN seec_prd_declaracao_fiscal.tb_sped_base b ON r.id_base = b.id WHERE r.id_base IN (" + String.join(",", Collections.nCopies(ids.size(), "?")) + ")";
            // Mensagem de log para verificar a consulta SQL
            System.out.println("Consulta SQL para " + tabela.getFormattedName() + ": " + query);

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                // Configurar os valores dos IDs
                int i = 1;
                for (String id : ids) {
                    statement.setString(i++, id);
                }

                try (ResultSet resultSet = statement.executeQuery()) {
                    List<String[]> results = new ArrayList<>();
                    while (resultSet.next()) {
                        String idBase = resultSet.getString(1); // Obter o id_base da primeira coluna
                        // Incluir o nome da tabela como parte dos resultados
                        results.add(new String[]{idBase, tabela.getFormattedName()});
                    }
                    // Mensagem de log para verificar os resultados da consulta
                    System.out.println("Resultados da consulta para " + tabela.getFormattedName() + ": " + results);

                    saveResultsToCSV(results, "X:\\Dados\\SPED\\ResultadoQuery.csv");
                }
            }
        }
    }

    public Set<String> loadIdsFromCSV(String filePath, TabelasSped tabela) throws IOException {
        Set<String> filteredIds = new HashSet<>();
        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                // 2. Substituir a verificação estática do valor "E310" pelo valor da variável
                if (columns.length >= 7 && columns[6].trim().equals(tabela.getFormattedName())) {
                    String id = columns[0].trim();
                    filteredIds.add(id);
                }
            }
        } catch (IOException e) {
            throw e;
        }

        System.out.println("IDs filtrados para " + tabela.getFormattedName() + ":");
        for (String id : filteredIds) {
            System.out.println(id);
        }

        return filteredIds;
    }

    public void saveResultsToCSV(List<String[]> results, String outputPath) throws IOException {
        // Verificar se é a primeira iteração e se o arquivo já existe
        if (isFirstIteration && Files.exists(Paths.get(outputPath))) {
            // Se for a primeira iteração e o arquivo já existe, não adicionar ao final, mas sim sobrescrever
            try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outputPath)))) {
                for (String[] result : results) {
                    pw.println(result[0] + "," + result[1]); // Escrever o id_base e o nome da tabela
                }
            }
            isFirstIteration = false; // Marcar que não é mais a primeira iteração
        } else {
            // Nas execuções subsequentes ou se o arquivo não existe, adicionar ao final do arquivo
            try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outputPath, true)))) {
                for (String[] result : results) {
                    pw.println(result[0] + "," + result[1]); // Escrever o id_base e o nome da tabela
                }
            }
        }
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
