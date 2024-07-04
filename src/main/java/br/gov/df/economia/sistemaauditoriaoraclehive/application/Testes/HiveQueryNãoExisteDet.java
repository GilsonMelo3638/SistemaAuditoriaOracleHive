package br.gov.df.economia.sistemaauditoriaoraclehive.application.Testes;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class HiveQueryNãoExisteDet {

    public static void main(String[] args) {
        executeHiveQueries();
    }

    public static void executeHiveQueries() {
        // Obtém os valores das variáveis de ambiente
        String jdbcURL = System.getenv("HIVE_JDBC_URL");
        String username = System.getenv("HIVE_USERNAME");
        String password = System.getenv("HIVE_PASSWORD");

        // Diretório para salvar os arquivos CSV
        String directory = "\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\";

        // Executar as duas consultas sequencialmente
        executeQueryAndSaveResult(jdbcURL, username, password, directory, "ExisteInfnfceNaoExisteNfce.csv",
                "SELECT i.* FROM seec_prd_documento_fiscal.tb_nfce_infnfce i " +
                        "LEFT JOIN (SELECT DISTINCT id FROM seec_prd_documento_fiscal.tb_nfce) distinct_ids " +
                        "ON i.id = distinct_ids.id " +
                        "LEFT JOIN seec_prd_documento_fiscal.tb_nfce d ON i.id = d.id WHERE d.id IS NULL");

        executeQueryAndSaveResult(jdbcURL, username, password, directory, "ExisteInfnfceNaoExistedetNfce.csv",
                "SELECT i.* FROM seec_prd_documento_fiscal.tb_nfce_infnfce i " +
                        "LEFT JOIN (SELECT DISTINCT id FROM seec_prd_documento_fiscal.tb_nfce_detnfce) distinct_ids " +
                        "ON i.id = distinct_ids.id " +
                        "LEFT JOIN seec_prd_documento_fiscal.tb_nfce_detnfce d ON i.id = d.id WHERE d.id IS NULL");
    }

    public static void executeQueryAndSaveResult(String jdbcURL, String username, String password, String directory,
                                                 String fileName, String hiveQuery) {
        String filePath = directory + fileName;
        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password);
             FileWriter writer = new FileWriter(filePath)) {

            // Imprime a consulta
            System.out.println("Query a ser executada: " + hiveQuery);

            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(hiveQuery)) {

                // Processar os resultados e escrever no arquivo CSV
                while (resultSet.next()) {
                    // Escrever no arquivo CSV
                    for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                        writer.append(resultSet.getString(i));
                        if (i < resultSet.getMetaData().getColumnCount()) {
                            writer.append(",");
                        }
                    }
                    writer.append("\n");
                }
            }

            System.out.println("Consulta concluída. Resultados salvos em: " + filePath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
