package com.example.dechivejavafx.application.Testes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class HiveQueryExecutorZookeeper {

    public static void main(String[] args) {
        executeHiveQuery();
    }

    public static void executeHiveQuery() {
        // Obtém os valores das variáveis de ambiente
        String jdbcURL = System.getenv("HIVE_JDBC_URL");
        String username = System.getenv("HIVE_USERNAME");
        String password = System.getenv("HIVE_PASSWORD");

        // Consulta Hive
        String hiveQuery = "SELECT id_base, datahora_fin\n" +
                "FROM seec_prd_declaracao_fiscal.tb_sped_reg_0000 where id_base = '941703331'";

        // Bloco try-with-resources para garantir que as conexões sejam fechadas corretamente
        try (
                Connection connection = DriverManager.getConnection(jdbcURL, username, password);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(hiveQuery)
        ) {
            // Processar os resultados
            while (resultSet.next()) {
                // Supondo que `datahora_fin` seja um campo de data/hora
                java.sql.Timestamp timestamp = resultSet.getTimestamp("datahora_fin");
                System.out.println("Resultado: " + timestamp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
