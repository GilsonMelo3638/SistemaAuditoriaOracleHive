package br.gov.df.economia.sistemaauditoriaoraclehive.application.Testes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class HiveConnectorDesribe {
    private Connection connection;

    public HiveConnectorDesribe() {
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
                int columnCount = resultSet.getMetaData().getColumnCount();
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
        // Cria uma instância da classe HiveConnector
        HiveConnectorDesribe connector = new HiveConnectorDesribe();

        // Executa uma consulta (substitua pela sua própria consulta)
        String query = "DESCRIBE FORMATTED seec_prd_documento_fiscal.tb_mdfe_infmdfe";

        System.out.println("Query a ser executada: " + query);
        connector.executeQuery(query);
        System.out.println("Query a ser executada: " + query);

        // Desconecta do banco de dados
        connector.disconnect();
    }
}
