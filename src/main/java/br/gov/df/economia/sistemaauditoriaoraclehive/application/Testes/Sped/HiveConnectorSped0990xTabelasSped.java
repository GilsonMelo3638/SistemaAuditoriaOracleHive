package br.gov.df.economia.sistemaauditoriaoraclehive.application.Testes.Sped;

import java.io.FileWriter;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HiveConnectorSped0990xTabelasSped {
    private Connection connection;

    public HiveConnectorSped0990xTabelasSped() {
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

    public void executeQueryAndAppendToCSV(String query, String fileName) {
        try {
            if (connection != null) {
                // Cria uma instrução SQL
                Statement statement = connection.createStatement();

                // Executa a consulta
                ResultSet resultSet = statement.executeQuery(query);

                // Cria um FileWriter para escrever no arquivo CSV (usando append)
                FileWriter csvWriter = new FileWriter(fileName, true);

                // Escreve os resultados
                while (resultSet.next()) {
                    int columnCount = resultSet.getMetaData().getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        csvWriter.append(resultSet.getString(i));
                        if (i < columnCount) {
                            csvWriter.append(",");
                        }
                    }
                    csvWriter.append("\n");
                }

                // Fecha os recursos
                resultSet.close();
                statement.close();
                csvWriter.close();

                System.out.println("Resultados da consulta foram adicionados a: " + fileName);
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
        HiveConnectorSped0990xTabelasSped connector = new HiveConnectorSped0990xTabelasSped();

        // Obtém a data atual e subtrai 30 dias
        LocalDateTime dataAtual = LocalDateTime.now().minusDays(60);
        String dataFormatada = dataAtual.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        // Array de reg_blc
        String[] regBlcArray = {"0000" , "9900", "9999", "0150", "0200", "'0460","B020", "B025", "B420", "B440",
                "B460", "B470", "C100", "C113", "C170", "C190", "C191", "C195", "C197", "C390", "C500", "C590",
                "C595", "D100", "D101", "D190", "D195", "D197", "D500", "D510", "D530", "D590", "D695", "D696",
                "E110", "E111", "E112", "E113", "E115", "E116", "E200", "E210", "E220", "E230", "E240", "E250",
                "E300", "E310", "E311", "E316", "E500", "E510", "E520", "E530", "E531"};

        // Diretório e nome do arquivo para salvar os resultados CSV
        String diretorio = "X:\\Dados\\SPED\\";
        String nomeArquivo = diretorio + "divergencia9900TabelasSped.csv";

// Loop sobre o array de reg_blc
        for (String regBlc : regBlcArray) {
            // Constrói a nova query com a condição de data e o join entre as tabelas
            String novaQuery = "SELECT \n" +
                    "    tb_sped_base.id AS id_base,\n" +
                    "    tb_sped_base.datahora_processamento,\n" +
                    "    tb_sped_base.status_processamento,\n" +
                    "    tb_sped_reg_9900.datahora_fin,\n" +
                    "    tb_sped_reg_9900.id_base,\n" +
                    "    tb_sped_reg_9900.reg,\n" +
                    "    tb_sped_reg_9900.reg_blc\n" +
                    "FROM seec_prd_declaracao_fiscal.tb_sped_reg_9900\n" +
                    "JOIN seec_prd_declaracao_fiscal.tb_sped_base ON tb_sped_base.id = tb_sped_reg_9900.id_base\n" +
                    "WHERE tb_sped_reg_9900.reg_blc = '" + regBlc + "'\n" +
                    "AND tb_sped_reg_9900.datahora_fin >= '" + dataFormatada + "'\n" +
                    "AND NOT EXISTS (\n" +
                    "    SELECT 1\n" +
                    "    FROM seec_prd_declaracao_fiscal.tb_sped_reg_" + regBlc.toLowerCase() + "\n" +
                    "    WHERE tb_sped_reg_" + regBlc.toLowerCase() + ".id_base = tb_sped_reg_9900.id_base\n" +
                    "    AND tb_sped_reg_" + regBlc.toLowerCase() + ".datahora_fin >= '" + dataFormatada + "'\n" +
                    ")";

            System.out.println("Query a ser executada: " + novaQuery);

            // Chamada do método executeQueryAndAppendToCSV para executar a consulta e adicionar os resultados ao arquivo CSV
            connector.executeQueryAndAppendToCSV(novaQuery, nomeArquivo);
        }

        // Desconecta do banco de dados
        connector.disconnect();
    }
}
