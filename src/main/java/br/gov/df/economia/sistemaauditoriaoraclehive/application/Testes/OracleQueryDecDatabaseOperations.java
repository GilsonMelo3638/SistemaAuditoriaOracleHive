package br.gov.df.economia.sistemaauditoriaoraclehive.application.Testes;


import br.gov.df.economia.sistemaauditoriaoraclehive.db.OracleDecDatabaseOperations;

public class OracleQueryDecDatabaseOperations {

    public static void main(String[] args) {
        OracleDecDatabaseOperations dbOperations = new OracleDecDatabaseOperations();
        dbOperations.executeQueryAndSaveToCSV();
    }
}