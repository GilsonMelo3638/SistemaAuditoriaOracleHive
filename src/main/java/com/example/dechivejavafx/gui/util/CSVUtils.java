package com.example.dechivejavafx.gui.util;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import com.example.dechivejavafx.Validacoes.SituacaoProcessamento;
import com.example.dechivejavafx.Validacoes.TipoDoc;
import com.example.dechivejavafx.gui.OracleHiveController;
import com.example.dechivejavafx.gui.QuantidadeDocumentoArquivoController;
import com.example.dechivejavafx.model.entities.Agenda;
import com.example.dechivejavafx.model.entities.OracleHive;
import com.example.dechivejavafx.model.entities.QuantidadeDocumentoArquivo;

import java.io.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Classe utilitária para manipulação de arquivos CSV.
 */
public class CSVUtils {

    private static final Logger LOGGER = Logger.getLogger(QuantidadeDocumentoArquivoController.class.getName());
    private static final String CSV_HEADER = "chave,tabelaHive,totalHive";
    private static final String DIRECTORY_PATH = "\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\";
    // Caminho do arquivo CSV do resultado final de diferenças de quantidades de documentos no Oracle e Hive
    private static final String CSV_OUTPUT_PATH = "\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia/pendencias.csv";
    private static final String PENDENCIA_FILE_NAME = "pendencias.csv";
    private static final String Resultado_FILE_NAME = "Resultado.csv";
    private static final String AGENDA_FILE_NAME = "Agenda.csv";
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
}
