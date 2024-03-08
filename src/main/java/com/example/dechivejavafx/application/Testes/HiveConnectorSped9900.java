package com.example.dechivejavafx.application.Testes;

import com.example.dechivejavafx.db.HiveSpedDatabaseOperations;
import com.example.dechivejavafx.gui.util.CSVUtils;

import java.time.LocalDateTime;

public class HiveConnectorSped9900 {
    private HiveSpedDatabaseOperations databaseOperations;

    public HiveConnectorSped9900(String jdbcUrl, String user, String password) {
        this.databaseOperations = new HiveSpedDatabaseOperations(jdbcUrl, user, password);
    }

    public void executeQueryAndSaveToCSV(LocalDateTime startDate, String filePath) {
        try {
            // Executa a consulta utilizando a classe HiveSpedDatabaseOperations
            CSVUtils.saveSpedHiveToCsv(databaseOperations.executeSpedReg9900Query(startDate), filePath);
        } catch (Exception e) {
            System.err.println("Erro ao executar a consulta e salvar no arquivo CSV: " + e.getMessage());
        }
    }

    public void disconnect() {
        databaseOperations.disconnect();
    }

    public static void main(String[] args) {
        // Parâmetros de conexão
        String jdbcUrl = System.getenv("HIVE_JDBC_URL");
        String user = System.getenv("HIVE_USERNAME");
        String password = System.getenv("HIVE_PASSWORD");

        // Cria uma instância da classe HiveConnectorSped9900
        HiveConnectorSped9900 connector = new HiveConnectorSped9900(jdbcUrl, user, password);

        // Constrói a nova query usando a classe HiveSpedDatabaseOperations
        LocalDateTime dataAtual = LocalDateTime.now().minusDays(15);

        // Define o caminho do arquivo CSV
        String filePath = "X:\\Dados\\SPED\\HIVE_SPED_9900.csv";

        // Chamada do método executeQueryAndSaveToCSV para executar a consulta e salvar no arquivo CSV
        connector.executeQueryAndSaveToCSV(dataAtual, filePath);

        // Desconecta do banco de dados
        connector.disconnect();
    }
}