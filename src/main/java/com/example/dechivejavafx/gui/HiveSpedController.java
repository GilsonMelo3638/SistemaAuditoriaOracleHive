package com.example.dechivejavafx.gui;


import com.example.dechivejavafx.db.HiveSpedDatabaseOperations;
import com.example.dechivejavafx.gui.util.CSVUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class HiveSpedController {
    private static HiveSpedDatabaseOperations databaseOperations;
    private String jdbcUrl;
    private String user;
    private String password;
    private LocalDateTime startDate;
    private String filePath;

    public HiveSpedController() {
        this.jdbcUrl = System.getenv("HIVE_JDBC_URL");
        this.user = System.getenv("HIVE_USERNAME");
        this.password = System.getenv("HIVE_PASSWORD");
        this.databaseOperations = new HiveSpedDatabaseOperations(jdbcUrl, user, password);

        // Define startDate e filePath
        this.startDate = LocalDateTime.now().minusDays(15);
        this.filePath = "X:\\Dados\\SPED\\HIVE_SPED_9900.csv";
    }

    public void executeQueryAndSaveToCSV() {
        try {
            // Verifique se databaseOperations não é nulo
            if (databaseOperations != null) {
                // Executa a consulta utilizando a classe HiveSpedDatabaseOperations
                ResultSet resultSet = databaseOperations.executeSpedReg9900Query(startDate);

                // Chama o método da classe CSVUtils para salvar no CSV
                CSVUtils.saveSpedHiveToCsv(resultSet, filePath);

                // Fecha os recursos
                resultSet.close();
            } else {
                System.err.println("Erro: databaseOperations não foi inicializado.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao executar a consulta: " + e.getMessage());
        }
    }

    public void disconnect() {
        if (databaseOperations != null) {
            databaseOperations.disconnect();
        }
    }

    public void gerarHive9900Csv() {
        // Lógica que você quer executar
        executeQueryAndSaveToCSV();
        disconnect();
    }
}
