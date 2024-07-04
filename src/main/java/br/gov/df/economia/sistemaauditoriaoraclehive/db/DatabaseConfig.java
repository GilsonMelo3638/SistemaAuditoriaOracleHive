package br.gov.df.economia.sistemaauditoriaoraclehive.db;

import br.gov.df.economia.sistemaauditoriaoraclehive.gui.util.Configuracao;
import br.gov.df.economia.sistemaauditoriaoraclehive.Validacoes.ConnectionStatus;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConfig {

    private static final String CONFIG_FILE = "db.properties";
    private static Properties properties;
    private static Connection conn;
    public static void setConnectionStatus(ConnectionStatus status) {
        Configuracao.connectionOraprd21Status = status;
    }

    static {
        loadConfigurations();
    }

    public static void loadConfigurations() {
        properties = new Properties();

        try (InputStream input = new FileInputStream(CONFIG_FILE)) {
            if (input == null) {
                System.err.println("Desculpe, o arquivo " + CONFIG_FILE + " não foi encontrado.");
                return;
            }
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Erro ao carregar configurações do arquivo " + CONFIG_FILE);
            throw new DatabaseExceptions.DbException(e.getMessage());
        }

        // Substitua as propriedades com base nas variáveis de ambiente
        properties.setProperty("user", System.getenv("SPRING_DATASOURCE_USERNAME"));
        properties.setProperty("password", System.getenv("SPRING_DATASOURCE_PASSWORD"));
        properties.setProperty("dburl", System.getenv("SPRING_DATASOURCE_URL"));
        properties.setProperty("hive.user", System.getenv("HIVE_USERNAME"));
        properties.setProperty("hive.password", System.getenv("HIVE_PASSWORD"));
        properties.setProperty("hive.dburl", System.getenv("HIVE_JDBC_URL"));
        properties.setProperty("hive.zookeeper.namespace", "hiveserver2");
    }

    public static String getJdbcUrl() {
        return properties.getProperty("dburl");
    }

    public static String getUsername() {
        return properties.getProperty("user");
    }

    public static String getPassword() {
        return properties.getProperty("password");
    }

    public static boolean getUseSSL() {
        String useSSL = properties.getProperty("useSSL");
        return useSSL != null && useSSL.equalsIgnoreCase("true");
    }

    public static Connection getConnection() {
        if (Configuracao.connectionOraprd21Status == ConnectionStatus.DISABLED) {
            System.out.println("Connection is disabled."); // Adicione esta linha
            return null;
        }

        if (conn == null) {
            try {
                loadConfigurations(); // Certifique-se de que as configurações estejam carregadas
                System.out.println("Connecting to: " + getJdbcUrl()); // Adicione esta linha
                conn = DriverManager.getConnection(getJdbcUrl(), properties);
            } catch (SQLException e) {
                System.err.println("Error connecting to database: " + e.getMessage()); // Adicione esta linha
                e.printStackTrace(); // Adicione esta linha para imprimir o stack trace da exceção
                throw new DatabaseExceptions.DbException(e.getMessage());
            }
        }
        return conn;
    }

 /*         // System.err.println("Error connecting to database: " + e.getMessage()); // Adicione esta linha
            //  e.printStackTrace(); // Adicione esta linha para imprimir o stack trace da exceção
           //  throw new DatabaseExceptions.DbException(e.getMessage());
           return null;*/

    public static void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                throw new DatabaseExceptions.DbException(e.getMessage());
            }
        }
    }

    public static void closeStatement(Statement st) {
        if (st != null) {
            try {
                st.close();
            } catch (SQLException e) {
                throw new DatabaseExceptions.DbException(e.getMessage());
            }
        }
    }

    public static void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                throw new DatabaseExceptions.DbException(e.getMessage());
            }
        }
    }

    // Métodos para obter configurações específicas do Hive
    public static String getHiveJdbcUrl() {
        return properties.getProperty("hive.dburl") + ";serviceDiscoveryMode=zooKeeper;zooKeeperNamespace=hiveserver2";
    }

    public static String getHiveUsername() {
        return properties.getProperty("hive.user");
    }

    public static String getHivePassword() {
        return properties.getProperty("hive.password");
    }



    public static boolean getHiveUseSSL() {
        String useSSL = properties.getProperty("hive.useSSL");
        return useSSL != null && useSSL.equalsIgnoreCase("true");
    }
}