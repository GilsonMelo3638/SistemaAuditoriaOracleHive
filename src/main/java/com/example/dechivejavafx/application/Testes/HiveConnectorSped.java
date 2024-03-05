package com.example.dechivejavafx.application.Testes;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HiveConnectorSped {
    private Connection connection;

    public HiveConnectorSped() {
        try {
            // Carrega o driver JDBC do Hive
            Class.forName("org.apache.hive.jdbc.HiveDriver");

            // Obtém os valores das variáveis de ambiente
            String jdbcUrl = System.getenv("HIVE_JDBC_URL");
            String user = System.getenv("HIVE_USERNAME");
            String password = System.getenv("HIVE_PASSWORD");

            // Estabelece a conexão
            this.connection = DriverManager.getConnection(jdbcUrl, user, password);

            System.out.println("Conexão estabelecida com sucesso.");
        } catch (Exception e) {
            System.err.println("Erro ao conectar: " + e.getMessage());
        }
    }

    public void executeQuery(String query) {
        try {
            if (connection != null) {
                // Cria uma instrução SQL
                Statement statement = connection.createStatement();

                // Executa a consulta
                ResultSet resultSet = statement.executeQuery(query);

                // Exibe os resultados
                ResultSetMetaData metaData = resultSet.getMetaData();
                int columnCount = metaData.getColumnCount();

                // Imprime os nomes das colunas
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(metaData.getColumnName(i) + "\t");
                }
                System.out.println();

                // Imprime os resultados
                while (resultSet.next()) {
                    // Processa cada coluna da linha do resultado
                    for (int i = 1; i <= columnCount; i++) {
                        System.out.print(resultSet.getString(i) + "\t"); // Imprime o valor da coluna
                    }
                    System.out.println(); // Pula para a próxima linha
                }

                // Fecha os recursos
                resultSet.close();
                statement.close();
            } else {
                System.err.println("Conexão não inicializada corretamente.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao executar a consulta: " + e.getMessage());
        }
    }

    public void disconnect() {
        try {
            // Fecha a conexão
            if (connection != null) {
                connection.close();
                System.out.println("Conexão encerrada.");
            }
        } catch (Exception e) {
            System.err.println("Erro ao desconectar: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // Cria uma instância da classe HiveConnectorSped
        HiveConnectorSped connector = new HiveConnectorSped();

        // Obtém a data atual e subtrai 30 dias
        LocalDateTime dataAtual = LocalDateTime.now().minusDays(360);
        String dataFormatada = dataAtual.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Constrói a nova query com a condição de data
        String novaQuery = "SELECT \n" +
                "    datahora_fin,\n" +
                "    id,\n" +
                "    id_base\n" +  // Removida a vírgula após o último campo
                "FROM seec_prd_declaracao_fiscal.tb_sped_reg_9900\n" +
                "WHERE id_base IN (SELECT id_base FROM seec_prd_declaracao_fiscal.tb_sped_reg_9900\n" +
                "                 EXCEPT\n" +
                "                 SELECT id_base FROM seec_prd_declaracao_fiscal.tb_sped_reg_0000)\n" +
                "AND datahora_fin >= '" + dataFormatada + "'";

        System.out.println("Query a ser executada: " + novaQuery);

        // Chamada do método executeQuery para executar a consulta
        connector.executeQuery(novaQuery);

        // Desconecta do banco de dados
        connector.disconnect();
    }
}
