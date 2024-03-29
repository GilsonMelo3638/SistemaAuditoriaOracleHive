package com.example.dechivejavafx.application.Testes.Sped;

import com.example.dechivejavafx.db.OracleSpedDatabaseOperations;

public class OracleConnectionSped {
    private static OracleSpedDatabaseOperations oracleSpedDatabaseOperations;

    public static void main(String[] args) {
        // Inicializa a instância de OracleSpedDatabaseOperations
        oracleSpedDatabaseOperations = new OracleSpedDatabaseOperations();

        // Chama o método após a inicialização
        oracleSpedDatabaseOperations.executeQueryAndSaveToCSV();
    }
}
