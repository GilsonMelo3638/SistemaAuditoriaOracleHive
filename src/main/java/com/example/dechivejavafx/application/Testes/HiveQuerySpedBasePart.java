package com.example.dechivejavafx.application.Testes;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HiveQuerySpedBasePart {

    public static void main(String[] args) {
        executeQuerySpedBasePart();
    }

    public static void executeQuerySpedBasePart() {
        // Diretório e nome do arquivo CSV
        String directory = "\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\";
        String fileName = "SpedBasePart.csv";
        String filePath = directory + fileName;

        // Obtém os valores das variáveis de ambiente
        String jdbcURL = System.getenv("HIVE_JDBC_URL");
        String username = System.getenv("HIVE_USERNAME");
        String password = System.getenv("HIVE_PASSWORD");

        // Bloco try-with-resources para garantir que as conexões sejam fechadas corretamente
        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password);
             FileWriter writer = new FileWriter(filePath)) {
            String sql = "SELECT " +
                    "periodo, " +
                    "conteudo_arquivo, " +
                    "logs, " +
                    "conteudo_assinatura, " +
                    "recibo_pdf " +
                    "FROM seec_hom_declaracao_fiscal.tb_sped_base_part " +
                    "Where id = '949004950' or id = '960854619' or id = '956354838' or id = '953555398' or id = '948504666' " +
                    "LIMIT 5";

            System.out.println("Query a ser executada: " + sql);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                // Execute a query e processe os resultados
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    // Escreva o cabeçalho do CSV
                    writer.append("periodo,conteudo_arquivo,logs,conteudo_assinatura,recibo_pdf\n");

                    while (resultSet.next()) {
                        String periodo = resultSet.getString("periodo");
                        String conteudoArquivo = resultSet.getString("conteudo_arquivo");
                        String logs = resultSet.getString("logs");
                        String conteudoAssinatura = resultSet.getString("conteudo_assinatura");
                        String reciboPdf = resultSet.getString("recibo_pdf");

                        // Escreva os resultados no arquivo CSV
                        writer.append(periodo).append(",");
                        writer.append(conteudoArquivo != null ? conteudoArquivo : "").append(",");
                        writer.append(logs != null ? logs : "").append(",");
                        writer.append(conteudoAssinatura != null ? conteudoAssinatura : "").append(",");
                        writer.append(reciboPdf != null ? reciboPdf : "").append("\n");

                        // Imprimir no console
                        System.out.println("Resultado da query - Periodo: " + periodo +
                                ", Conteudo_Arquivo: " + conteudoArquivo +
                                ", Logs: " + logs +
                                ", Conteudo_Assinatura: " + conteudoAssinatura +
                                ", Recibo_Pdf: " + reciboPdf);
                    }
                }
            }
            System.out.println("Consulta concluída. Resultados salvos em: " + filePath);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}
