package com.example.dechivejavafx.application.Testes;

import com.example.dechivejavafx.gui.util.Configuracao;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HiveQueryExecutor {

    public static void main(String[] args) {
        String directory = "\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\";

        executeQueryAndSaveResults(
                "SELECT DISTINCT a.arquivo arquivo, a.total totalInfNfe, b.detpag detpag, c.vol vol, d.nfref nfref " +
                        "FROM ( SELECT arquivo, COUNT(*) AS total " +
                        "FROM seec_prd_documento_fiscal.tb_nfe_infnfe " +
                        "GROUP BY arquivo) a " +
                        "LEFT JOIN (SELECT DISTINCT arquivo, 'ok' AS detpag " +
                        "FROM seec_prd_documento_fiscal.tb_nfe_detpag) b " +
                        "ON a.arquivo = b.arquivo " +
                        "LEFT JOIN (SELECT DISTINCT arquivo, 'ok' AS vol " +
                        "FROM seec_prd_documento_fiscal.tb_nfe_vol) c " +
                        "ON a.arquivo = c.arquivo " +
                        "LEFT JOIN (SELECT DISTINCT arquivo, 'ok' AS nfref " +
                        "FROM seec_prd_documento_fiscal.tb_nfe_nfref) d " +
                        "ON a.arquivo = d.arquivo " +
                        "WHERE (b.detpag IS NULL " +
                        "OR c.vol IS NULL " +
                        "OR d.nfref IS NULL) " +
                        "AND SUBSTRING(a.arquivo, 1, 8) >= ? " +
                        "ORDER BY a.arquivo",
                directory + "NfeDetpagVolNfref.csv",
                "arquivo,totalInfNfe,detpag,vol,nfref"
        );

        executeQueryAndSaveResults(
                "SELECT b.arquivo AS arquivo,  COUNT(b.arquivo) AS totalItensAverbados,'ok' AS itensAverbados " +
                        "FROM seec_prd_documento_fiscal.tb_nfe_evento_itens_averbados a " +
                        "RIGHT JOIN seec_prd_documento_fiscal.tb_nfe_evento b " +
                        "ON a.nsuchave = b.nsuchave " +
                        "WHERE a.arquivo IS NULL AND b.nfeproc_infevento_tpevento = '790700' " +
                        "AND SUBSTRING(a.arquivo, 1, 8) >= ? " +
                        "GROUP BY b.arquivo, 'ok'",
                directory + "nfeItensAverbados.csv",
                "arquivo,totalItensAverbados,itensAverbados"
        );

        executeQueryAndSaveResults(
                "SELECT DISTINCT a.arquivo arquivo, a.total totalInfNfce, b.detpag detpag " +
                        "FROM ( SELECT arquivo, COUNT(*) AS total " +
                        "FROM seec_prd_documento_fiscal.tb_nfce_infnfce " +
                        "GROUP BY arquivo) a " +
                        "LEFT JOIN (SELECT DISTINCT arquivo, 'ok' AS detpag " +
                        "FROM seec_prd_documento_fiscal.tb_nfce_detpag) b " +
                        "ON a.arquivo = b.arquivo " +
                        "WHERE b.detpag IS NULL " +
                        "AND SUBSTRING(a.arquivo, 1, 8) >= ? " +
                        "ORDER BY a.arquivo",
                directory + "NfceDetpag.csv",
                "arquivo,totalInfNfce,detpag"
        );

        executeQueryAndSaveResults(
                "SELECT DISTINCT a.arquivo arquivo, a.total totalInfCte, b.infnfe infnfe, c.infq infq " +
                        "FROM ( SELECT arquivo, COUNT(*) AS total " +
                        "FROM seec_prd_documento_fiscal.tb_cte_infcte " +
                        "GROUP BY arquivo) a " +
                        "LEFT JOIN (SELECT DISTINCT arquivo, 'ok' AS infnfe " +
                        "FROM seec_prd_documento_fiscal.tb_cte_infnfe) b " +
                        "ON a.arquivo = b.arquivo " +
                        "LEFT JOIN (SELECT DISTINCT arquivo, 'ok' AS infq " +
                        "FROM seec_prd_documento_fiscal.tb_cte_infq) c " +
                        "ON a.arquivo = c.arquivo " +
                        "WHERE (b.infnfe IS NULL OR c.infq IS NULL) " +
                        "AND SUBSTRING(a.arquivo, 1, 8) >= ? " +
                        "ORDER BY a.arquivo",
                directory + "CteInfcteInfq.csv",
                "arquivo,totalInfCte,infnfe,infq"
        );

        executeQueryAndSaveResults(
                "SELECT DISTINCT a.arquivo arquivo, a.total totalInfMdfe, b.infcte infcte, c.infnfe infnfe " +
                        "FROM ( SELECT arquivo, COUNT(*) AS total " +
                        "FROM seec_prd_documento_fiscal.tb_mdfe_infmdfe " +
                        "GROUP BY arquivo) a " +
                        "LEFT JOIN (SELECT DISTINCT arquivo, 'ok' AS infcte " +
                        "FROM seec_prd_documento_fiscal.tb_mdfe_infcte) b " +
                        "ON a.arquivo = b.arquivo " +
                        "LEFT JOIN (SELECT DISTINCT arquivo, 'ok' AS infnfe " +
                        "FROM seec_prd_documento_fiscal.tb_mdfe_infnfe) c " +
                        "ON a.arquivo = c.arquivo " +
                        "WHERE (b.infcte IS NULL OR c.infnfe IS NULL) " +
                        "AND SUBSTRING(a.arquivo, 1, 8) >= ? " +
                        "AND SUBSTRING(a.arquivo, 9, 2) > '06' " +
                        "AND SUBSTRING(a.arquivo, 9, 2) < '23' " +
                        "ORDER BY a.arquivo",
                directory + "MdfeInfMdfeInfCteInfNfe.csv",
                "arquivo,totalInfMdfe,infcte,infnfe"
        );

        executeQueryAndSaveResults(
                "SELECT DISTINCT a.arquivo arquivo, a.total totalInfBpe, b.comp comp " +
                        "FROM ( SELECT arquivo, COUNT(*) AS total " +
                        "FROM seec_prd_documento_fiscal.tb_bpe_infbpe " +
                        "GROUP BY arquivo) a " +
                        "LEFT JOIN (SELECT DISTINCT arquivo, 'ok' AS comp " +
                        "FROM seec_prd_documento_fiscal.tb_bpe_comp) b " +
                        "ON a.arquivo = b.arquivo " +
                        "WHERE b.comp IS NULL " +
                        "AND SUBSTRING(a.arquivo, 1, 8) >= ? " +
                        "ORDER BY a.arquivo",
                directory + "BpeComp.csv",
                "arquivo,totalInfBpe,comp"
        );

        executeQueryAndSaveResults(
                "SELECT a.arquivo, " +
                        "COUNT(a.arquivo) AS totalInfNf3e, b.ggrandfat, c.ggrcontrat " +
                        "FROM seec_prd_documento_fiscal.tb_nf3e_infnf3e a " +
                        "LEFT JOIN (SELECT DISTINCT arquivo, 'ok' AS ggrandfat FROM seec_prd_documento_fiscal.tb_nf3e_ggrandfat) b " +
                        "ON a.arquivo = b.arquivo " +
                        "LEFT JOIN (SELECT DISTINCT arquivo, 'ok' AS ggrcontrat FROM seec_prd_documento_fiscal.tb_nf3e_ggrcontrat) c " +
                        "ON a.arquivo = c.arquivo " +
                        "WHERE (b.ggrandfat IS NULL OR c.ggrcontrat IS NULL) " +
                        "AND SUBSTRING(a.arquivo, 1, 8) >= ? " +
                        "GROUP BY a.arquivo, b.ggrandfat, c.ggrcontrat " +
                        "ORDER BY a.arquivo",
                directory + "Nf3egrandfatGrcontrat.csv",
                "arquivo,totalInfNf3e,ggrandfat,ggrcontrat"
        );
    }

    public static void executeQueryAndSaveResults(String sql, String filePath, String csvHeader) {
        // Obtém os valores das variáveis de ambiente
        String jdbcURL = System.getenv("HIVE_JDBC_URL");
        String username = System.getenv("HIVE_USERNAME");
        String password = System.getenv("HIVE_PASSWORD");

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password);
             FileWriter writer = new FileWriter(filePath)) {

            // Log da query e parâmetro
            System.out.println("Query a ser executada: " + sql);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                LocalDate hoje = LocalDate.now();
                LocalDate varquivo = hoje.minusDays(Configuracao.dias);
                DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("yyyyMMdd");
                String varquivoString = varquivo.format(formatoData);

                // Log do parâmetro
                System.out.println("Parâmetro da query: " + varquivoString);

                preparedStatement.setString(1, varquivoString);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    writer.append(csvHeader).append("\n"); // Adiciona cabeçalho e nova linha

                    while (resultSet.next()) {
                        StringBuilder row = new StringBuilder();
                        String[] columns = csvHeader.split(",");

                        for (int i = 0; i < columns.length; i++) {
                            String value = resultSet.getString(i + 1); // Índice do ResultSet começa em 1
                            row.append(value != null ? value : "").append(",");
                        }

                        row.setLength(row.length() - 1); // Remove a última vírgula
                        row.append("\n"); // Adiciona nova linha
                        writer.append(row.toString());
                        System.out.println("Resultado da query - " + row.toString());
                    }
                }
                System.out.println("Consulta concluída. Resultados salvos em: " + filePath);
            }
        } catch (SQLException | IOException e) {
            System.err.println("Erro ao executar a query: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
