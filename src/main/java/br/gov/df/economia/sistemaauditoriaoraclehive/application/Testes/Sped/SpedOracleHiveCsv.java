package br.gov.df.economia.sistemaauditoriaoraclehive.application.Testes.Sped;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpedOracleHiveCsv {

    public static void main(String[] args) {
        String oracleFilePath = "X:\\Dados\\SPED\\ORACLE_SPED_9900.csv";
        String hiveFilePath = "X:\\Dados\\SPED\\HIVE_SPED_9900.csv";
        String outputFilePath = "X:\\Dados\\SPED\\Pendencia_Processamento_0000.csv";

        saveLinesNotInHive(oracleFilePath, hiveFilePath, outputFilePath);
    }

    private static void saveLinesNotInHive(String oracleFilePath, String hiveFilePath, String outputFilePath) {
        Set<String> hiveIDs = new HashSet<>();

        try (Stream<String> hiveStream = Files.lines(Paths.get(hiveFilePath))) {
            hiveIDs.addAll(hiveStream.map(line -> line.split(",")[1].trim()).collect(Collectors.toSet()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (Stream<String> oracleStream = Files.lines(Paths.get(oracleFilePath));
             var writer = Files.newBufferedWriter(Paths.get(outputFilePath))) {

            final boolean[] isHeader = {true};
            oracleStream.forEach(line -> {
                if (isHeader[0]) {
                    try {
                        writer.write(line);
                        writer.newLine();
                        isHeader[0] = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    String[] fields = line.split(",");
                    String oracleID = fields[0].trim();
                    if ("0000".equals(fields[fields.length - 1].trim()) && !hiveIDs.contains(oracleID)) {
                        try {
                            writer.write(line);
                            writer.newLine();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            System.out.println("Arquivo gravado com sucesso em: " + outputFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
