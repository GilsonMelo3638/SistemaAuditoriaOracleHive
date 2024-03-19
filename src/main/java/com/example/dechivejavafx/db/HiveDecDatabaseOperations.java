package com.example.dechivejavafx.db;

import com.example.dechivejavafx.Validacoes.TipoDoc;
import com.example.dechivejavafx.gui.util.Configuracao;
import com.example.dechivejavafx.model.entities.DetNFeNFCeInf;
import com.example.dechivejavafx.model.entities.QuantidadeDocumentoArquivo;
import com.example.dechivejavafx.model.entities.TotalizacaoNfe;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

public class HiveDecDatabaseOperations {
    protected String jdbcURL;
    protected String username;
    protected String password;
    private List<TotalizacaoNfe> queryResults;
    private List<DetNFeNFCeInf> queryResultsDet;

    public HiveDecDatabaseOperations(String jdbcURL, String username, String password) {
        this.jdbcURL = jdbcURL;
        this.username = username;
        this.password = password;
        this.queryResults = new ArrayList<>();
        this.queryResultsDet = new ArrayList<>();
    }
    public void executeQueryAndPopulateResults() {
        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
            System.out.println("Driver Hive JDBC registrado com sucesso!");
        } catch (ClassNotFoundException e) {
            DatabaseExceptions.handleException("Erro ao registrar o driver Hive JDBC", e);
        }

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            System.out.println("Conexão com o Hive estabelecida com sucesso!");

            try {
                executeQueryNfeTotalizacao(connection);
            } catch (SQLException e) {
                DatabaseExceptions.handleException("Erro ao executar a consulta Hive", e);
            }
        } catch (SQLException e) {
            DatabaseExceptions.handleException("Erro ao conectar ao Hive", e);
        }
    }

    public void executeQueryAndPopulateResultsDet() {
        try {
            Class.forName("org.apache.hive.jdbc.HiveDriver");
            System.out.println("Driver Hive JDBC registrado com sucesso!");
        } catch (ClassNotFoundException e) {
            DatabaseExceptions.handleException("Erro ao registrar o driver Hive JDBC", e);
        }

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password)) {
            System.out.println("Conexão com o Hive estabelecida com sucesso!");

            try {
                executeQueryDetFaltante(connection); // Substitua pelos nomes reais das tabelas
            } catch (SQLException e) {
                DatabaseExceptions.handleException("Erro ao executar a consulta Hive", e);
            }
        } catch (SQLException e) {
            DatabaseExceptions.handleException("Erro ao conectar ao Hive", e);
        }
    }



    public static Connection getHiveConnection(String jdbcUrl, String username, String password) throws SQLException {
        try {
            System.out.println("Connecting to Hive: " + jdbcUrl); // Adicione esta linha

            Class.forName("org.apache.hive.jdbc.HiveDriver");
            Connection hiveConn = DriverManager.getConnection(jdbcUrl, username, password);

            System.out.println("Connected to Hive successfully!"); // Adicione esta linha
            return hiveConn;
        } catch (ClassNotFoundException e) {
            System.err.println("Hive driver not found: " + e.getMessage()); // Adicione esta linha
            throw new SQLException("Hive driver not found", e);
        } catch (SQLException e) {
            System.err.println("Error connecting to Hive: " + e.getMessage()); // Adicione esta linha
            throw e;
        }
    }


    private void executeQueryNfeTotalizacao(Connection connection) throws SQLException {
        String sqlQuery = "SELECT count(arquivo) as Total_NFe, arquivo, enderemit_uf as uf, SUM(issqntot_vserv) as vserv, " +
                "SUM(issqntot_viss) as viss, SUM(icmstot_vbc) as vbc, SUM(icmstot_vicms) as vicms, " +
                "SUM(icmstot_vst) as vst, SUM(icmstot_vfcpst) as vfcpst, SUM(icmstot_vfcpstret) as vfcpstret, " +
                "SUM(icmstot_vprod) as vprod, SUM(icmstot_vnf) as vnf, SUM(icmstot_vfcpufdest) as vfcpufdest, " +
                "arquivo FROM seec_prd_documento_fiscal.tb_nfe_infnfe " +
                "WHERE SUBSTRING(arquivo, 1, 8) >= '20240101' AND  SUBSTRING(arquivo, 3, 3) <> 'Dif'GROUP BY arquivo, enderemit_uf";
        System.out.println("Query a ser executada: " + sqlQuery);
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sqlQuery)) {

            while (resultSet.next()) {
                TotalizacaoNfe nFeInfo = DatabaseIntegrityExceptions.mapResultSetToTotalizacaoNfe(resultSet);
                queryResults.add(nFeInfo);
                System.out.println(nFeInfo);
            }

        } // O recurso ResultSet será fechado automaticamente quando o bloco try-with-resources terminar
    }

    public static List<QuantidadeDocumentoArquivo> executeSQLQuantidadeDocumentos(Connection connection, String tabela, String data) throws SQLException {
        List<QuantidadeDocumentoArquivo> resultados = new ArrayList<>();
        String sql = "SELECT '" + tabela + "' AS tabela, arquivo, SUBSTRING(arquivo, 1,8) AS dia, COUNT(arquivo) AS HIVE FROM seec_prd_documento_fiscal."
                + tabela + " WHERE SUBSTRING(arquivo, 1, 8) >= '" + data + "' AND  SUBSTRING(arquivo, 3, 3) <> 'Dif' GROUP BY arquivo ORDER BY arquivo";

        System.out.println("Query a ser executada: " + sql);

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {
            while (resultSet.next()) {
                int count = resultSet.getInt("HIVE");
                String arquivo = resultSet.getString("arquivo");
                String dia = resultSet.getString("dia");

                System.out.println("Resultado da query - Tabela: " + tabela + ", Arquivo: " + arquivo +  ", Dia: " + dia + ", Contagem: " + count);

                // Criar instâncias de QuantidadeDocumentoArquivo e adicioná-las à lista de resultados
                QuantidadeDocumentoArquivo resultado = new QuantidadeDocumentoArquivo(tabela, arquivo, dia, count);
                resultados.add(resultado);
            }
        }

        System.out.println("Total de resultados da query: " + resultados.size());

        return resultados;
    }

    private void executeQueryDetFaltante(Connection connection) throws SQLException {
        List<DetNFeNFCeInf> resultados = new ArrayList<>();
        String[] detTables = {"tb_nfe_detnfe", "tb_nfe", "tb_nfce_detnfce", "tb_nfce", "tb_nf3e_detnf3e"};
        String[] infTables = {"tb_nfe_infnfe", "tb_nfe_infnfe", "tb_nfce_infnfce", "tb_nfce_infnfce", "tb_nf3e_infnf3e"};

        for (int i = 0; i < detTables.length; i++) {
            String sql = "SELECT '" + detTables[i] + "' AS tabela, i.arquivo, SUBSTRING(i.arquivo, 1, 8) AS dia, " +
                    "COUNT(DISTINCT i.nsuchave) AS quantidade_nsuchave " +
                    "FROM seec_prd_documento_fiscal." + infTables[i] + " i " +
                    "LEFT JOIN seec_prd_documento_fiscal." + detTables[i] + " d " +
                    "ON i.arquivo = d.arquivo AND i.nsuchave = d.nsuchave " +
                    "WHERE SUBSTRING(i.arquivo, 1, 8) >= ? AND d.arquivo IS NULL " +
                    "GROUP BY i.arquivo ORDER BY i.arquivo";

            System.out.println("Query a ser executada: " + sql);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                // Defina o parâmetro de data
                LocalDate hoje = LocalDate.now();
                LocalDate varquivo = hoje.minusDays(Configuracao.dias);
                DateTimeFormatter formatoData = DateTimeFormatter.ofPattern("yyyyMMdd");
                String varquivoString = varquivo.format(formatoData);

                // Vincule o valor do parâmetro ao marcador de posição
                preparedStatement.setString(1, varquivoString);

                // Execute a query e processe os resultados
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        int count = resultSet.getInt("quantidade_nsuchave");
                        String arquivo = resultSet.getString("arquivo");
                        String dia = resultSet.getString("dia");

                        System.out.println("Resultado da query - Tabela: " + detTables[i] + ", Arquivo: " + arquivo +  ", Dia: " + dia + ", Contagem: " + count);

                        // Criar instância de DetNFeNFCeInf e adicioná-la à lista de resultados
                        DetNFeNFCeInf detInfo = DatabaseIntegrityExceptions.mapResultSetToDetNFeNFCeInf(resultSet);
                       // resultados.add(detInfo);

                        // Criar instância de DetNFeNFCeInf para representar o resultado
                        DetNFeNFCeInf resultadoDet = new DetNFeNFCeInf(arquivo, detTables[i], count);
                        resultados.add(resultadoDet);
                    }
                }
            }
        }

        // Retorna a lista completa de resultados
        queryResultsDet = resultados;
    }

    public static void executeHiveQueryIdDuplicidadePrincipal(String jdbcURL, String username, String password) {
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
        String fileName = "DuplicidadeIdPrincipal.csv";
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

    public static void executeHiveQueryIdDuplicidadeDetalhe(String jdbcURL, String username, String password) {
        // Lista de tabelas com seus respectivos nomes

        // Lista de tabelas com seus respectivos nomes
        List<String> tables = Arrays.asList(
                "tb_nfe_detnfe",
                "tb_nfe",
                "tb_nf3e_detnf3e",
                "tb_nfce_detnfce",
                "tb_nfce"
        );

        // Diretório e nome do arquivo CSV
        String directory = "\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\";
        String fileName = "DuplicidadeIdDetalhe.csv";
        String filePath = directory + fileName;

        // Bloco try-with-resources para garantir que as conexões sejam fechadas corretamente
        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password);
             FileWriter writer = new FileWriter(filePath)) {

            for (String table : tables) {
                // Consulta Hive com a adição da coluna "tabela" para indicar a origem
                String hiveQuery = "SELECT DISTINCT arquivo, '" + table + "' AS tabela FROM ( " +
                        "    SELECT arquivo, id, nitem FROM seec_prd_documento_fiscal." + table + " " +
                        "    WHERE arquivo >= "+ Configuracao.inicioDuplicidade +
                        "    GROUP BY arquivo, id, nitem " +
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

    public List<TotalizacaoNfe> getQueryResults() {
        return queryResults;
    }

    public List<DetNFeNFCeInf> getQueryResultsDet() {
        return queryResultsDet;
    }

    public void executeQueryDetFaltantePublic(Connection connection) throws SQLException {
        executeQueryDetFaltante(connection);
    }
    public List<TipoDoc> obterTipoDocs() {
        // Use diretamente os valores do enum TipoDoc
        List<TipoDoc> tipoDocsAtivos = Arrays.stream(TipoDoc.values())
                .filter(tipoDoc -> "sim".equalsIgnoreCase(tipoDoc.getAtivo()))
                .collect(Collectors.toList());
        return tipoDocsAtivos;
    }
    public String getJdbcURL() {
        return jdbcURL;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}