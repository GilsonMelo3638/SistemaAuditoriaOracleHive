package com.example.dechivejavafx.gui.util;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.example.dechivejavafx.Validacoes.SituacaoProcessamento;
import com.example.dechivejavafx.Validacoes.TabelasSped;
import com.example.dechivejavafx.Validacoes.TipoDoc;
import com.example.dechivejavafx.gui.OracleHiveController;
import com.example.dechivejavafx.gui.QuantidadeDocumentoArquivoController;
import com.example.dechivejavafx.model.entities.*;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Classe utilitária para manipulação de arquivos CSV.
 */
public class CSVUtils {

    private static final Logger LOGGER = Logger.getLogger(QuantidadeDocumentoArquivoController.class.getName());
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String CSV_HEADER = "chave,tabelaHive,totalHive";
    private static final String DIRECTORY_PATH = "\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\";
    // Caminho do arquivo CSV do resultado final de diferenças de quantidades de documentos no Oracle e Hive
    private static final String CSV_OUTPUT_PATH = "\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia/pendencias.csv";
    private static final String PENDENCIA_FILE_NAME = "pendencias.csv";
    private static final String Resultado_FILE_NAME = "Resultado.csv";
    private static final String AGENDA_FILE_NAME = "Agenda.csv";
    private static final String DUPLICIDADE_PRINCIPAL_FILE_NAME = "DuplicidadeIdPrincipal.csv";
    private static final String DUPLICIDADE_DETALHE_FILE_NAME = "DuplicidadeIdDetalhe.csv";
    private static final String PENDENCIA_PRINCIPAL_DETALHE = "PendenciaPrincipalDetalhe.csv";
    private static final String CONSOLIDATED_FILE_NAME = "CargasHive.csv";
    private static List<String> concatenatedData = new ArrayList<>();
    private static QuantidadeDocumentoArquivoController quantidadeDocumentoArquivoController;

    /**
     * Realiza a junção de dois arquivos CSV com base em uma chave comum.
     *
     * @param agendaFilePath       Caminho do arquivo CSV da agenda.
     * @param cargasHiveFilePath   Caminho do arquivo CSV das cargas Hive.
     * @param outputFilePath       Caminho do arquivo de saída.
     */
    public static void joinCSVFiles(String agendaFilePath, String cargasHiveFilePath, String outputFilePath) {
        try {
            List<String[]> agendaRecords = readCSV(agendaFilePath);
            List<String[]> cargasHiveRecords = readCSV(cargasHiveFilePath);

            List<String[]> joinedRecords = performJoin(agendaRecords, cargasHiveRecords);

            writeCSV(outputFilePath, joinedRecords);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao unir arquivos CSV", e);
        }
    }

    /**
     * Lê um arquivo CSV e retorna os registros como uma lista de arrays de strings.
     *
     * @param filePath Caminho do arquivo CSV.
     * @return Lista de registros.
     * @throws IOException Se ocorrer um erro durante a leitura do arquivo.
     */
    private static List<String[]> readCSV(String filePath) throws IOException {
        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            return csvReader.readAll();
        }
    }

    /**
     * Realiza a junção dos registros da agenda com os registros das cargas Hive com base em uma chave comum.
     *
     * @param agendaRecords        Registros da agenda.
     * @param cargasHiveRecords    Registros das cargas Hive.
     * @return Lista de registros resultantes da junção.
     */
    private static List<String[]> performJoin(List<String[]> agendaRecords, List<String[]> cargasHiveRecords) {
        List<String[]> joinedRecords = new ArrayList<>();
        for (String[] agendaRecord : agendaRecords) {
            String commonKeyAgenda = agendaRecord[0];
            String[] correspondingCargasHiveRecord = cargasHiveRecords.stream()
                    .filter(record -> record[0].equals(commonKeyAgenda))
                    .findFirst()
                    .orElse(null);
            List<String> joinedRecordList = createJoinedRecordList(agendaRecord, correspondingCargasHiveRecord);
            joinedRecords.add(joinedRecordList.toArray(new String[0]));
        }
        return joinedRecords;
    }

    /**
     * Cria uma lista de registros resultantes da junção.
     *
     * @param agendaRecord                  Registro da agenda.
     * @param correspondingCargasHiveRecord Registro correspondente das cargas Hive.
     * @return Lista de registros resultantes da junção.
     */
    private static List<String> createJoinedRecordList(String[] agendaRecord, String[] correspondingCargasHiveRecord) {
        List<String> joinedRecordList = new ArrayList<>(Arrays.asList(agendaRecord));
        if (correspondingCargasHiveRecord != null) {
            joinedRecordList.addAll(Arrays.asList(correspondingCargasHiveRecord).subList(1, correspondingCargasHiveRecord.length));
        } else {
            fillMissingCargasHiveColumns(joinedRecordList, agendaRecord.length - 1);
        }
        return joinedRecordList;
    }

    /**
     * Preenche as colunas ausentes das cargas Hive com valores nulos.
     *
     * @param list            Lista de registros.
     * @param cargasHiveColumns Número de colunas das cargas Hive.
     */
    private static void fillMissingCargasHiveColumns(List<String> list, int cargasHiveColumns) {
        for (int i = 1; i < cargasHiveColumns; i++) {
            list.add(null);
        }
        list.add("0");
    }

    /**
     * Escreve os registros em um arquivo CSV.
     *
     * @param filePath Caminho do arquivo CSV.
     * @param records  Lista de registros.
     * @throws IOException Se ocorrer um erro durante a escrita do arquivo.
     */
    private static void writeCSV(String filePath, List<String[]> records) throws IOException {
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath))) {
            csvWriter.writeAll(records);
        }
    }

    /**
     * Exclui arquivos no diretório, exceto os relacionados a pendências (PendenciaFileName).
     * Evita excluir arquivos relacionados a Agendas (AgendaFileName) e Pendências (PendenciaFileName).
     *
     * @param diretorio Caminho do diretório onde os arquivos serão excluídos.
     */
    public static void deleteFilesExceptPendencia(String diretorio) {
        File dir = new File(diretorio);

        // Verifica se o diretório existe e é realmente um diretório
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles(file ->
                    // Exclui arquivos que não correspondem ao nome de arquivo de Pendências
                    !file.getName().equalsIgnoreCase(PENDENCIA_FILE_NAME)
                            // Mantém arquivos relacionados a Agendas (AgendaFileName)
                            && !file.getName().equalsIgnoreCase(AGENDA_FILE_NAME)
                            // Mantém arquivos relacionados a Agendas (AgendaFileName)
                            && !file.getName().equalsIgnoreCase(Resultado_FILE_NAME)
                            // Mantém arquivos relacionados a Agendas (DUPLICIDADE_PRINCIPAL_FILE_NAME)
                            && !file.getName().equalsIgnoreCase(DUPLICIDADE_PRINCIPAL_FILE_NAME)
                            // Mantém arquivos relacionados a Agendas (DUPLICIDADE_DETALHE_FILE_NAME)
                            && !file.getName().equalsIgnoreCase(DUPLICIDADE_DETALHE_FILE_NAME)
                            && !file.getName().equalsIgnoreCase(PENDENCIA_PRINCIPAL_DETALHE)
            );

            // Verifica se há arquivos a serem processados
            if (files != null) {
                for (File file : files) {
                    // Tenta excluir o arquivo e registra um erro se não for possível
                    if (!file.delete()) {
                        LOGGER.log(Level.SEVERE, "Erro ao excluir o arquivo: " + file.getName());
                    }
                }
            }
        }
    }
    /**
     * Gera um arquivo CSV com base nos resultados e no tipo de documento.
     *
     * @param resultados Lista de resultados.
     * @param tipoDoc    Tipo de documento.
     */
    public static void gerarArquivo(List<QuantidadeDocumentoArquivo> resultados, TipoDoc tipoDoc) {
        String nomeArquivo = tipoDoc.toString() + ".csv";
        try (PrintWriter writer = new PrintWriter(new FileWriter(DIRECTORY_PATH + nomeArquivo))) {
            writer.println(CSV_HEADER);
            for (QuantidadeDocumentoArquivo resultado : resultados) {
                TipoDoc tipoDocCorrespondente = quantidadeDocumentoArquivoController.getTipoDocByTabelaHive(tipoDoc, resultado.getTabelaHive());
                if (tipoDocCorrespondente != null) {
                    String novoArquivo = tipoDocCorrespondente.getTabelaOracle() + resultado.getArquivo();
                    writer.println(formatCSVLine(novoArquivo, resultado.getTabelaHive(), resultado.getTotal()));
                    concatenateData(novoArquivo, resultado.getTabelaHive(), resultado.getTotal());
                } else {
                    LOGGER.log(Level.INFO, "Não foi encontrado TipoDoc correspondente para tabelaHive: " + resultado.getTabelaHive());
                }
            }
            generateConsolidatedFile();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao gerar o arquivo CSV", e);
        }
    }

    /**
     * Concatena dados para o arquivo consolidado.
     *
     * @param novoArquivo Nome do novo arquivo.
     * @param tabelaHive  Tabela Hive.
     * @param total       Total.
     */
    private static void concatenateData(String novoArquivo, String tabelaHive, int total) {
        String formattedLine = formatCSVLine(novoArquivo, tabelaHive, total);
        concatenatedData.add(formattedLine);
    }

    /**
     * Formata uma linha para o arquivo CSV.
     *
     * @param novoArquivo Nome do novo arquivo.
     * @param tabelaHive  Tabela Hive.
     * @param total       Total.
     * @return Linha formatada.
     */
    private static String formatCSVLine(String novoArquivo, String tabelaHive, int total) {
        return String.format("%s,%s,%d%n", novoArquivo, tabelaHive, total);
    }

    /**
     * Gera o arquivo consolidado com os dados concatenados.
     */
    private static void generateConsolidatedFile() {
        try (PrintWriter consolidatedWriter = new PrintWriter(new FileWriter(DIRECTORY_PATH + CONSOLIDATED_FILE_NAME))) {
            consolidatedWriter.println(CSV_HEADER);
            concatenatedData.forEach(consolidatedWriter::print);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao gerar o arquivo consolidado", e);
        }
    }

    /**
     * Salva todos os dados da agenda em um arquivo CSV.
     *
     * @param agendaList Lista de agendas.
     */
    public static void saveAllDataToFile(List<Agenda> agendaList) {
        String filePath = DIRECTORY_PATH + "Agenda.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(CSV_HEADER);
            writer.newLine();
            agendaList.forEach(agenda -> writeAgendaRecord(writer, agenda));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao salvar o arquivo com todos os dados", e);
        }
    }

    /**
     * Escreve um registro da agenda no arquivo CSV.
     *
     * @param writer BufferedWriter para escrita.
     * @param agenda Registro da agenda.
     */
    private static void writeAgendaRecord(BufferedWriter writer, Agenda agenda) {
        SituacaoProcessamento situacao = agenda.getInd_situacao();
        String tipoDocAtivo = agenda.getTipo_doc().getAtivo();
        if ((situacao == null || SituacaoProcessamento.CONCLUIDO.equals(situacao)) && "sim".equalsIgnoreCase(tipoDocAtivo)) {
            BigDecimal totalOracle = agenda.getQuantidade();
            if (totalOracle != null && totalOracle.compareTo(BigDecimal.ZERO) > 0) {
                String numericValues = extractNumericValues(agenda.getNome_arquivo());
                String chave = agenda.getTipo_doc() + numericValues;
                try {
                    writer.write(String.format("%s,%s,%s,%s%n",
                            chave,
                            agenda.getNome_arquivo(), // Assume this is the correct field to match "arquivo"
                            totalOracle.toPlainString(),
                            agenda.getTipo_doc().toString()));
                } catch (IOException e) {
                    LOGGER.log(Level.SEVERE, "Erro ao escrever registro de agenda", e);
                }
            }
        }
    }

    /**
     * Extrai valores numéricos de uma string.
     *
     * @param input String de entrada.
     * @return Valores numéricos extraídos.
     */
    private static String extractNumericValues(String input) {
        Matcher matcher = Pattern.compile(".*?([0-9]+)\\.xml$").matcher(input);
        return matcher.find() ? matcher.group(1) : "";
    }

    public static void salvarResultadosCSV(List<OracleHive> resultados) {
        try (Writer writer = new FileWriter(CSV_OUTPUT_PATH)) {
            // Escreve o cabeçalho
            writer.write("Arquivo,TipoDoc,TabelaOracle,TabelaHive,TotalOracle,TotalHive,Diferenca\n");
            // Escreve os dados
            for (OracleHive oracleHive : resultados) {
                writer.write(String.format("\"%s\",\"%s\",\"%s\",\"%s\",%d,%d,%d\n",
                        oracleHive.getArquivo(),
                        oracleHive.getTabelaOracle(),
                        oracleHive.getTabelaHive(),  // Corrigido: estava getTabelaOracle() em vez de getTabelaHive()
                        oracleHive.getTabelaHive(),
                        oracleHive.getTotalOracle(),
                        oracleHive.getTotalHive(),
                        oracleHive.getDiferenca()));
            }
            System.out.println("Resultados salvos em: " + CSV_OUTPUT_PATH);
        } catch (IOException e) {
            // Imprime a stack trace para depuração
            e.printStackTrace();
            // Registra a exceção no log
            Logger.getLogger(OracleHiveController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    public static void saveSpedHiveToCsv(ResultSet resultSet, String filePath) {
        try (FileWriter csvWriter = new FileWriter(filePath);
             BufferedWriter bufferedWriter = new BufferedWriter(csvWriter)) {

            // Escreve o cabeçalho do CSV
            bufferedWriter.write("Data Processamento,id_base,Linha,Quantidade Reg Blc,Reg,Reg Blc\n");

            // Processa os resultados e escreve no arquivo CSV
            while (resultSet.next()) {
                String datahoraProcessamento = resultSet.getString("datahora_fin");
                int idBase = resultSet.getInt("id_base");
                String linha = resultSet.getString("linha");
                int qtdRegBlc = resultSet.getInt("qtd_reg_blc");
                String reg = resultSet.getString("reg");
                String regBlc = resultSet.getString("reg_blc");

                // Escreve uma linha no CSV
                bufferedWriter.write(datahoraProcessamento + "," + idBase + "," + linha + ","
                        + qtdRegBlc + "," + reg + "," + regBlc + "\n");
            }

            System.out.println("Dados salvos com sucesso no arquivo CSV: " + filePath);

        } catch (Exception e) {
            System.err.println("Erro ao salvar no arquivo CSV: " + e.getMessage());
        }
    }

    /**
     * Converte uma linha do arquivo CSV em um objeto OracleHive.
     *
     * Este método recebe uma linha do arquivo CSV, realiza a análise dos valores e cria um objeto OracleHive.
     * Certifica-se de que existam pelo menos 6 valores na linha do CSV antes de continuar o processamento.
     * Ajusta os índices se necessário e remove as aspas duplas dos valores.
     * Cria uma instância de OracleHive com valores padrão se totalOracle for menor ou igual a zero,
     * ou se tabelaOracle for nula ou vazia. Calcula a diferença entre totalOracle e totalHive.
     *
     * @param line A linha do arquivo CSV a ser convertida.
     * @return Um objeto OracleHive representando os dados da linha CSV, ou null se houver um problema de formato.
     */
    public OracleHive parseCsvLine(String line) {
        String[] values = line.split(",");

        // Certifica-se de que há pelo menos 6 valores no array antes de continuar
        if (values.length < 6) {
            System.err.println("Número insuficiente de valores na linha do CSV: " + line);
            return null;
        }
        // Ajusta os índices se necessário e remove as aspas duplas dos valores
        String tabelaOracleValue = removeAspasDuplas(values[3]);
        int totalOracleValue = parseInteger(removeAspasDuplas(values[2]));
        String tabelaHiveValue = removeAspasDuplas(values[4]);
        int totalHiveValue = parseInteger(removeAspasDuplas(values[5]));
        // Cria uma instância de OracleHive com valores padrão se totalOracle for menor ou igual a zero,
        // ou se tabelaOracle for nula ou vazia
        OracleHive oracleHive = new OracleHive();
        oracleHive.setArquivo(removeAspasDuplas(values[1]));
        oracleHive.setTabelaOracle(tabelaOracleValue);
        oracleHive.setTotalOracle(Math.max(totalOracleValue, 0));
        oracleHive.setTabelaHive(tabelaHiveValue);
        oracleHive.setTotalHive(Math.max(totalHiveValue, 0));
        // Calcula a diferença entre totalOracle e totalHive
        oracleHive.calcularDiferenca();
        return oracleHive;
    }

    /**
     * Remove as aspas duplas de uma string.
     *
     * Este método recebe uma string e remove as aspas duplas presentes nela.
     *
     * @param value A string contendo as aspas duplas a serem removidas.
     * @return A string sem as aspas duplas.
     */
    private String removeAspasDuplas(String value) {
        return value.replaceAll("\"", "");
    }
    /**
     * Converte uma string em um valor inteiro.
     *
     * Este método tenta converter uma string em um valor inteiro.
     * Retorna 0 se a conversão não for bem-sucedida.
     *
     * @param value A string a ser convertida.
     * @return O valor inteiro convertido, ou 0 se a conversão falhar.
     */
    private Integer parseInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0; // Ou outro valor padrão adequado
        }
    }

    public class FileComparisonService {

        /**
         * Compara dois arquivos CSV, identifica linhas no primeiro arquivo que não estão no segundo
         * baseado em uma chave específica, e salva essas linhas em um novo arquivo.
         *
         * @param oracleFilePath Caminho para o arquivo Oracle CSV.
         * @param hiveFilePath Caminho para o arquivo Hive CSV.
         * @param outputFilePath Caminho para o arquivo de saída com as diferenças.
         */
        public static void compareAndSaveDifferences(String oracleFilePath, String hiveFilePath, String outputFilePath) {
            Set<String> hiveIDs = new HashSet<>();

            // Leitura dos IDs do arquivo Hive
            try (Stream<String> hiveStream = Files.lines(Paths.get(hiveFilePath))) {
                hiveIDs.addAll(hiveStream.map(line -> line.split(",")[1].trim()).collect(Collectors.toSet()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            // Comparação e escrita das diferenças no arquivo de saída
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
    /**
     * Carrega os dados do arquivo CSV para uma lista de objetos Sped9900.
     *
     * @param filePath O caminho do arquivo CSV.
     * @return Uma lista de objetos Sped9900.
     */
    public static List<Sped9900> loadSped9900FromCsv(String filePath) {
        List<Sped9900> dataList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // Ignora a primeira linha (cabeçalhos)

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                // Verifica se a linha possui todos os campos esperados
                if (values.length != 7) {
                    System.err.println("Erro ao processar linha: " + line + ". Número incorreto de campos.");
                    continue; // Ignora esta linha e passa para a próxima
                }

                // Tenta converter os valores da linha para os tipos corretos
                try {
                    BigInteger idBase = new BigInteger(values[0].trim());
                    LocalDateTime dhProcessamento = LocalDateTime.parse(values[1].trim(), dateTimeFormatter);
                    int statusProcessamento = Integer.parseInt(values[2].trim());
                    int linha = Integer.parseInt(values[3].trim());
                    int quantidadeRegBloco = Integer.parseInt(values[4].trim());
                    String registro = values[5].trim(); // Mantém como String
                    String registroBloco = values[6].trim(); // Mantém como String

                    // Cria um objeto Sped9900 com os valores extraídos da linha e adiciona à lista
                    Sped9900 sped9900 = new Sped9900(idBase, dhProcessamento, statusProcessamento,
                            linha, quantidadeRegBloco, registro, registroBloco);
                    dataList.add(sped9900);
                } catch (NumberFormatException | DateTimeParseException e) {
                    // Se ocorrer um erro ao processar a linha, imprime uma mensagem de erro
                    System.err.println("Erro ao processar linha: " + line);
                    e.printStackTrace(); // Imprime o rastreamento da pilha para entender melhor o erro
                }
            }
        } catch (IOException e) {
            e.printStackTrace(); // Trate adequadamente as exceções conforme necessário
        }

        return dataList;
    }

    /**
     * Carrega os dados do arquivo CSV para uma lista de objetos Hive9900TabelasHive.
     *
     * @param filePath O caminho do arquivo CSV.
     * @return Uma lista de objetos Hive9900TabelasHive.
     */
    public static List<Hive9900TabelasHive> loadHive9900TabelasHiveFromCsv(String filePath) {
        List<Hive9900TabelasHive> dataList = new ArrayList<>();

        // Cria um DateTimeFormatter local que pode lidar com os microssegundos
        DateTimeFormatter localDateTimeFormatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")
                .optionalStart()
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 6, true)
                .optionalEnd()
                .toFormatter();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // Ignora a primeira linha (cabeçalhos)

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                // Verifica se a linha possui todos os campos esperados
                if (values.length != 6) {
                    LOGGER.log(Level.SEVERE, "Erro ao processar linha: " + line + ". Número incorreto de campos.");
                    continue; // Ignora esta linha e passa para a próxima
                }

                // Tenta converter os valores da linha para os tipos corretos
                try {
                    BigInteger idBase = new BigInteger(values[0].trim());
                    LocalDateTime dhProcessamento = LocalDateTime.parse(values[1].trim(), localDateTimeFormatter);
                    int statusProcessamento = Integer.parseInt(values[2].trim());
                    LocalDateTime dataHoraFin = LocalDateTime.parse(values[3].trim(), localDateTimeFormatter);
                    String registro = values[4].trim(); // Mantém como String
                    String registroBloco = values[5].trim(); // Mantém como String

                    // Cria um objeto Hive9900TabelasHive com os valores extraídos da linha e adiciona à lista
                    Hive9900TabelasHive hive9900TabelasHive = new Hive9900TabelasHive(idBase, dhProcessamento, statusProcessamento, dataHoraFin,
                            registro, registroBloco);
                    dataList.add(hive9900TabelasHive);
                } catch (NumberFormatException | DateTimeParseException e) {
                    // Se ocorrer um erro ao processar a linha, imprime uma mensagem de erro
                    LOGGER.log(Level.SEVERE, "Erro ao processar linha: " + line, e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao ler o arquivo CSV: " + filePath, e);
        }

        return dataList;
    }

    /**
     * Carrega os dados do arquivo CSV para uma lista de objetos Hive9900TabelasHiveFaltantes.
     *
     * @param filePath O caminho do arquivo CSV.
     * @return Uma lista de objetos Hive9900TabelasHiveFaltantes.
     */
    public static List<Hive9900TabelasHiveFaltantes> loadHive9900TabelasHiveFromCsvFaltantes(String filePath) {
        List<Hive9900TabelasHiveFaltantes> dataList = new ArrayList<>();

        // Cria um DateTimeFormatter local que pode lidar com os microssegundos
        DateTimeFormatter localDateTimeFormatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd HH:mm:ss")
                .optionalStart()
                .appendFraction(ChronoField.NANO_OF_SECOND, 0, 6, true)
                .optionalEnd()
                .toFormatter();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            reader.readLine(); // Ignorar a primeira linha (cabeçalhos)

            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",", -1); // Alteração aqui

                // Verifica se a linha possui todos os campos esperados
                if (values.length != 5) {
                    LOGGER.log(Level.SEVERE, "Erro ao processar linha: " + line + ". Número incorreto de campos.");
                    continue; // Ignora esta linha e passa para a próxima
                }

                // Tenta converter os valores da linha para os tipos corretos
                try {
                    BigInteger idBase = new BigInteger(values[0].trim());
                    LocalDateTime dhProcessamento = LocalDateTime.parse(values[1].trim(), localDateTimeFormatter);
                    int statusProcessamento = Integer.parseInt(values[2].trim());
                    String registroBloco = values[4].trim(); // Mantém como String
                    int quantidadeRegBloco = Integer.parseInt(values[3].trim());

                    // Cria um objeto Hive9900TabelasHiveFaltantes com os valores extraídos da linha e adiciona à lista
                    Hive9900TabelasHiveFaltantes hive9900TabelasHiveFaltantes = new Hive9900TabelasHiveFaltantes(idBase, dhProcessamento, statusProcessamento, registroBloco, quantidadeRegBloco);
                    dataList.add(hive9900TabelasHiveFaltantes);
                } catch (NumberFormatException | DateTimeParseException e) {
                    // Se ocorrer um erro ao processar a linha, imprime uma mensagem de erro
                    LOGGER.log(Level.SEVERE, "Erro ao processar linha: " + line, e);
                }
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erro ao ler o arquivo CSV: " + filePath, e);
        }

        return dataList;
    }
    /**
     * Carrega IDs distintos de um arquivo CSV para uma tabela SPED específica.
     *
     * @param filePath O caminho do arquivo CSV.
     * @param tabela A tabela SPED para a qual carregar IDs.
     * @return Um conjunto de IDs distintos.
     * @throws IOException Se ocorrer um erro de E/S durante a leitura do arquivo.
     */
    public static Set<String> loadDistinctIdsFromCSV(String filePath, TabelasSped tabela) throws IOException {
        Set<String> distinctIds = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] columns = line.split(",");
                // Garantir que temos um número suficiente de colunas para evitar ArrayIndexOutOfBoundsException
                if (columns.length >= 7 && columns[6].trim().equals(tabela.getFormattedName())) {
                    // Verificar também se columns[1] e columns[2] existem
                    if (columns.length > 2) {
                        String id = columns[0].trim() + "," + columns[1].trim() + "," + columns[2].trim() + "," + columns[4].trim();
                        distinctIds.add(id);
                    }
                }
            }
        }
        return distinctIds;
    }

    public static void saveListPendenciaPrincipalDetalheToCSV(List<com.example.dechivejavafx.model.entities.PendenciasHive> list) {
        // Caminho do arquivo CSV
        String filePath = DIRECTORY_PATH + PENDENCIA_PRINCIPAL_DETALHE;

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Escrever cabeçalhos
            writer.println("Arquivo,TabelaDetalhe,QuantidadeNsuchave"); // Adicione mais cabeçalhos conforme necessário

            // Escrever os dados no arquivo CSV
            for (com.example.dechivejavafx.model.entities.PendenciasHive pendencia : list) {
                writer.println(pendencia.getArquivo() + "," + pendencia.getTabelaDetalhe() + "," + pendencia.getQuantidadeNsuchave());
                // Adicione mais campos conforme necessário
            }
            System.out.println("Lista de pendências salva com sucesso em: " + filePath);
        } catch (IOException e) {
            // Lidar com exceções de E/S, se houver
            e.printStackTrace();
        }
    }

}
