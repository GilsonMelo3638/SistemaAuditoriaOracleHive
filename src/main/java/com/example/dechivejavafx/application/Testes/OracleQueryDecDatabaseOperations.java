package com.example.dechivejavafx.application.Testes;


import com.example.dechivejavafx.db.OracleDecDatabaseOperations;

public class OracleQueryDecDatabaseOperations {

    public static void main(String[] args) {
        OracleDecDatabaseOperations dbOperations = new OracleDecDatabaseOperations();
        dbOperations.executeQueryAndSaveToCSV();
    }
}