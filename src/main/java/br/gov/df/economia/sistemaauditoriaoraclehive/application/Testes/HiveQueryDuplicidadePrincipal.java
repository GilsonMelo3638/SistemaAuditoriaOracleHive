package br.gov.df.economia.sistemaauditoriaoraclehive.application.Testes;

import br.gov.df.economia.sistemaauditoriaoraclehive.gui.util.Configuracao;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

public class HiveQueryDuplicidadePrincipal {

    public static void main(String[] args) {
        executeHiveQuery();
    }

    public static void executeHiveQuery() {
        // Obtém os valores das variáveis de ambiente
        String jdbcURL = System.getenv("HIVE_JDBC_URL");
        String username = System.getenv("HIVE_USERNAME");
        String password = System.getenv("HIVE_PASSWORD");

        // Lista de tabelas com seus respectivos nomes
        List<String> tables = Arrays.asList(
                "tb_nfe_infnfe",
                "tb_nf3e_infnf3e",
                "tb_nfce_infnfce",
                "tb_cte_infcte",
                "tb_mdfe_infmdfe"
        );

        // Diretório e nome do arquivo CSV
        String directory = "\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\";
        String fileName = "DuplicidadeId.csv";
        String filePath = directory + fileName;

        // Bloco try-with-resources para garantir que as conexões sejam fechadas corretamente
        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password);
             FileWriter writer = new FileWriter(filePath)) {

            for (String table : tables) {
                // Consulta Hive com a adição da coluna "tabela" para indicar a origem
                String hiveQuery = "SELECT DISTINCT arquivo, '" + table + "' AS tabela FROM ( " +
                        "    SELECT arquivo, id FROM seec_prd_documento_fiscal." + table + " " +
                        "    WHERE arquivo >= "+ Configuracao.inicioDuplicidade +
                        "    GROUP BY arquivo, id " +
                        "    HAVING COUNT(*) > 1 " +
                        ") AS subquery";

                // Imprime os valores
                System.out.println("Query a ser executada para a tabela " + table + ": " + hiveQuery);

                try (Statement statement = connection.createStatement();
                     ResultSet resultSet = statement.executeQuery(hiveQuery)) {

                    // Processar os resultados e escrever no arquivo CSV
                    while (resultSet.next()) {
                        // Obtém os valores do resultado
                        String tabela = resultSet.getString("tabela");
                        String arquivo = resultSet.getString("arquivo");

                        // Escrever no arquivo CSV
                        writer.append(arquivo);
                        writer.append(",");
                        writer.append(tabela);
                        writer.append("\n");
                        // Imprimir no console
                        System.out.println("Tabela: " + tabela + ", Arquivo: " + arquivo);
                    }
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
