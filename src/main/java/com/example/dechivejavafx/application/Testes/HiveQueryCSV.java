package com.example.dechivejavafx.application.Testes;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HiveQueryCSV {

    private static final Logger logger = Logger.getLogger(HiveQueryCSV.class.getName());

    public static void main(String[] args) {
        executeLeftJoin();
    }

    public static void executeLeftJoin() {
        Map<String, Timestamp> hiveRecords = fetchHiveRecords();
        processCsvFile(hiveRecords);
    }

    private static Map<String, Timestamp> fetchHiveRecords() {
        String jdbcURL = System.getenv("HIVE_JDBC_URL");
        String username = System.getenv("HIVE_USERNAME");
        String password = System.getenv("HIVE_PASSWORD");
        Map<String, Timestamp> hiveRecords = new HashMap<>();

        // Obtendo a data de quinze dias atrás
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -15);
        Timestamp quinzeDiasAtras = new Timestamp(cal.getTime().getTime());

        // Montando a query com a condição da datahora_fin ser maior que quinze dias atrás
        String hiveQuery = "SELECT id_base, datahora_fin " +
                "FROM seec_prd_declaracao_fiscal.tb_sped_reg_0000 " +
                "WHERE datahora_fin >= '" + quinzeDiasAtras + "'";

        try (Connection connection = DriverManager.getConnection(jdbcURL, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(hiveQuery)) {

            while (resultSet.next()) {
                String idBase = resultSet.getString("id_base");
                Timestamp dataHoraFin = resultSet.getTimestamp("datahora_fin");
                hiveRecords.put(idBase, dataHoraFin);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to fetch Hive records", e);
        }

        return hiveRecords;
    }


    private static void processCsvFile(Map<String, Timestamp> hiveRecords) {
        String filePath = "X:\\Dados\\SPED\\ORACLE_SPED_9900.csv";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] fields = line.split(",");
                String idBase = fields[0];
                String dataProcessamento = fields[1];
                String statusProcessamento = fields[2];
                String linha = fields[3];
                String quantidade = fields[4];
                String reg = fields[5];
                String regBlc = fields[6];

                if ("0000".equals(regBlc)) {
                    Timestamp dataHoraFin = hiveRecords.getOrDefault(idBase, null);
                    if (dataHoraFin == null) {
                        logger.info("Resultado do left join: id_base = " + idBase +
                                ", Data Processamento = " + dataProcessamento +
                                ", Status Processamento = " + statusProcessamento +
                                ", Linha = " + linha +
                                ", Quantidade = " + quantidade +
                                ", Registro = " + reg +
                                ", Registro Bloco = " + regBlc +
                                ", datahora_fin = Não disponível");
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to process CSV file", e);
        }
    }
}