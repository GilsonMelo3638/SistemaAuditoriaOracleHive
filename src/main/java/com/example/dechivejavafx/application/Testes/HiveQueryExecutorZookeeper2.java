package com.example.dechivejavafx.application.Testes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HiveQueryExecutorZookeeper2 {

    public static void main(String[] args) {
        executeHiveQuery("tb_nfe_detnfe", "tb_nfe_infnfe");
        executeHiveQuery("tb_nfe", "tb_nfe_infnfe");
        executeHiveQuery("tb_nfce_detnfce", "tb_nfce_infnfce");
        executeHiveQuery("tb_nfce", "tb_nfce_infnfce");
    }

    public static void executeHiveQuery(String detTable, String infTable) {
        // Obtém os valores das variáveis de ambiente
        String jdbcURL = System.getenv("HIVE_JDBC_URL");
        String username = System.getenv("HIVE_USERNAME");
        String password = System.getenv("HIVE_PASSWORD");

        // Data dos últimos 10 dias no formato yyyymmdd
        LocalDate hoje = LocalDate.now();
        LocalDate varquivo = hoje.minusDays(90);
        DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("yyyyMMdd");
        String varquivoString = varquivo.format(formatoData);

        // Consulta Hive
        String hiveQuery = "SELECT\n" +
                "    i.arquivo AS arquivo,\n" +
                "    COUNT(DISTINCT i.nsuchave) AS quantidade_nsuchave\n" +
                "FROM\n" +
                "    seec_prd_documento_fiscal." + infTable + " i\n" +
                "LEFT JOIN\n" +
                "    seec_prd_documento_fiscal." + detTable + " d ON i.arquivo = d.arquivo AND i.nsuchave = d.nsuchave\n" +
                "WHERE\n" +
                "    SUBSTR(i.arquivo, 1, 8) >= '" + varquivoString + "'\n" +
                "    AND d.arquivo IS NULL\n" +
                "GROUP BY\n" +
                "    i.arquivo ORDER BY i.arquivo";

        // Imprimir a query
        System.out.println("Query Hive: " + hiveQuery);

        // Bloco try-with-resources para garantir que as conexões sejam fechadas corretamente
        try (
                Connection connection = DriverManager.getConnection(jdbcURL, username, password);
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(hiveQuery)
        ) {
            // Processar os resultados
            while (resultSet.next()) {
                String arquivo = resultSet.getString("arquivo");
                int quantidade_nsuchave = resultSet.getInt("quantidade_nsuchave");
                String tabelaDetalhe = detTable;  // Adiciona a string identificadora diretamente
                System.out.println("Arquivo: " + arquivo + ", Quantidade de nsuchave: " + quantidade_nsuchave + ", Tabela de Detalhe: " + tabelaDetalhe);
            }
            System.out.println("Verificação Hive concluída com sucesso na tabela: " + detTable);
        } catch (Exception e) {
            System.err.println("Erro ao executar a consulta Hive: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
