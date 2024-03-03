package com.example.dechivejavafx.gui.util;

import com.example.dechivejavafx.gui.QuantidadeDocumentoArquivoController;
import com.example.dechivejavafx.model.entities.Agenda;
import com.example.dechivejavafx.model.entities.QuantidadeDocumentoArquivo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Classe utilitária para manipulação de gráficos (PieChart, LineChart, BarChart) na aplicação.
 */
public class ChartsUtils {

    // Logger para registrar informações ou erros
    private static final Logger LOGGER = Logger.getLogger(QuantidadeDocumentoArquivoController.class.getName());

    // Formatters para formatar datas
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter YEAR_MONTH_DAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Referências aos gráficos PieChart e LineChart
    private static PieChart pieChart;
    private static LineChart<String, Number> lineChart;

    /**
     * Atualiza o PieChart com dados da lista de Agendas fornecida.
     *
     * @param agendaList Lista de Agendas a serem processadas.
     */
    public static void updatePieChart(List<Agenda> agendaList) {
        // Processa as agendas e obtém dados para o PieChart
        Map<String, Map<String, Integer>> parInicioData = processAgendasForPieChart(agendaList);
        int distinctDays = parInicioData.size();
        String chartTitle = generatePieChartTitle(distinctDays, Configuracao.dias);

        // Atualiza o título e dados do PieChart
        pieChart.setTitle(chartTitle);
        ObservableList<PieChart.Data> pieChartData = generatePieChartData(parInicioData);
        pieChart.setData(pieChartData);
    }

    /**
     * Processa a lista de Agendas para gerar dados adequados ao PieChart.
     *
     * @param agendaList Lista de Agendas a serem processadas.
     * @return Mapa contendo dados formatados para o PieChart.
     */
    private static Map<String, Map<String, Integer>> processAgendasForPieChart(List<Agenda> agendaList) {
        Map<String, Map<String, Integer>> parInicioData = new TreeMap<>();
        for (Agenda agenda : agendaList) {
            String parInicio = agenda.getPar_inicio();
            if (parInicio != null) {
                LocalDateTime dateTime = LocalDateTime.parse(parInicio, DATE_TIME_FORMATTER);
                String formattedParInicio = dateTime.format(DATE_FORMATTER);

                parInicioData.computeIfAbsent(formattedParInicio, k -> new HashMap<>())
                        .merge(agenda.getNome_arquivo(), 1, Integer::sum);
            }
        }
        return parInicioData;
    }

    /**
     * Gera o título para o PieChart com base na quantidade de dias distintos e no limite de dias configurado.
     *
     * @param distinctDays Quantidade de dias distintos.
     * @param dias Limite de dias configurado.
     * @return Título formatado para o PieChart.
     */
    private static String generatePieChartTitle(int distinctDays, int dias) {
        // Verifica se a quantidade de dias distintos é maior ou igual ao limite configurado
        if (distinctDays >= dias) {
            // Retorna o título formatado indicando o total de dias quando não há divergência
            return "Total de dias: " + dias;
        } else {
            // Caso haja divergência, usa a classe Text do JavaFX para formatar o título com cor vermelha
            Text redText = new Text("Divergência de dias: " + (dias - distinctDays));
            redText.setFill(Color.RED);
            redText.setFont(Font.font("System", 12));

            // Retorna o título formatado
            return redText.getText();
        }
    }
    /**
     * Gera os dados necessários para o PieChart a partir do mapa fornecido.
     *
     * @param parInicioData Mapa contendo dados formatados para o PieChart.
     * @return Lista de dados formatados para o PieChart.
     */
    private static ObservableList<PieChart.Data> generatePieChartData(Map<String, Map<String, Integer>> parInicioData) {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();

        parInicioData.forEach((parInicio, nomeArquivoCount) -> {
            int totalDistinctFiles = nomeArquivoCount.size();
            PieChart.Data pieData = new PieChart.Data(parInicio + " (" + totalDistinctFiles + " arquivos)", totalDistinctFiles);
            pieChartData.add(pieData);
        });

        return pieChartData;
    }

    /**
     * Atualiza o LineChart com dados da lista de Agendas fornecida.
     *
     * @param agendaList Lista de Agendas a serem processadas.
     */
    public static void updateLineChart(List<Agenda> agendaList) {
        // Cria uma série para armazenar dados no LineChart
        XYChart.Series<String, Number> series = new XYChart.Series<>();

        // Obtém quantidades somadas por data
        Map<String, BigDecimal> summedQuantitiesPerDate = sumQuantitiesPerDate(agendaList);

        // Converte as chaves para objetos LocalDate
        List<LocalDate> sortedDates = summedQuantitiesPerDate.keySet().stream()
                .map(dateString -> LocalDate.parse(dateString, YEAR_MONTH_DAY_FORMATTER))
                .sorted()
                .collect(Collectors.toList());

        // Para cada data ordenada, adiciona um ponto de dados à série
        sortedDates.forEach(date -> {
            String formattedDate = date.format(YEAR_MONTH_DAY_FORMATTER);
            BigDecimal quantity = summedQuantitiesPerDate.get(formattedDate);

            XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(formattedDate, quantity);
            series.getData().add(dataPoint);

            // Instala um tooltip no ponto de dados para exibir informações ao passar o mouse
            installTooltipOnDataPoint(dataPoint, quantity);
        });

        // Limpa os dados anteriores e adiciona a nova série ao LineChart
        lineChart.getData().clear();
        lineChart.getData().add(series);
    }

    /**
     * Soma as quantidades por data a partir da lista de Agendas fornecida.
     *
     * @param agendaList Lista de Agendas a serem processadas.
     * @return Mapa contendo quantidades somadas por data.
     */
    private static Map<String, BigDecimal> sumQuantitiesPerDate(List<Agenda> agendaList) {
        Map<String, BigDecimal> quantitiesPerDate = new HashMap<>();
        for (Agenda agenda : agendaList) {
            BigDecimal quantidade = agenda.getQuantidade();
            if (quantidade != null) {
                LocalDateTime parInicio = LocalDateTime.parse(agenda.getPar_inicio(), DATE_TIME_FORMATTER);
                String formattedDate = parInicio.toLocalDate().format(YEAR_MONTH_DAY_FORMATTER);
                quantitiesPerDate.merge(formattedDate, quantidade, BigDecimal::add);
            }
        }
        return quantitiesPerDate;
    }

    /**
     * Instala um tooltip em um ponto de dados no LineChart com informações sobre a quantidade e a data.
     *
     * @param data Ponto de dados no LineChart.
     * @param quantity Quantidade associada ao ponto de dados.
     */
    private static void installTooltipOnDataPoint(XYChart.Data<String, Number> data, BigDecimal quantity) {
        Tooltip tooltip = new Tooltip(String.format("Data: %s\nQuantidade: %s", data.getXValue(), quantity));
        Tooltip.install(data.getNode(), tooltip);
    }

    /**
     * Define a referência ao PieChart.
     *
     * @param chart Referência ao PieChart.
     */
    public static void setPieChart(PieChart chart) {
        pieChart = chart;
    }

    /**
     * Define a referência ao LineChart.
     *
     * @param chart Referência ao LineChart.
     */
    public static void setLineChart(LineChart<String, Number> chart) {
        lineChart = chart;
    }

    /**
     * Atualiza um gráfico de barras com dados da lista de QuantidadeDocumentoArquivo fornecida.
     *
     * @param barChart Gráfico de barras a ser atualizado.
     * @param resultados Lista de QuantidadeDocumentoArquivo a ser processada.
     */
    public static void atualizarGrafico(BarChart<String, Number> barChart, List<QuantidadeDocumentoArquivo> resultados) {
        // Verifica se o campo barChart foi inicializado corretamente
        if (barChart == null) {
            LOGGER.log(Level.SEVERE, "O campo barChart não foi inicializado corretamente.");
            return;
        }

        // Limpa os dados anteriores no gráfico
        barChart.getData().clear();
        // Desativa a animação para melhorar o desempenho
        barChart.setAnimated(false);

        // Cria uma série para armazenar os dados do gráfico
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Quantidade de Arquivos Por Dia");

        // Agrupa os resultados por dia e conta a quantidade de ocorrências
        Map<String, Long> contagemPorDia = resultados.stream()
                .collect(Collectors.groupingBy(QuantidadeDocumentoArquivo::getDia, Collectors.counting()));

        // Ordena as entradas do mapa por chave (dia)
        List<Map.Entry<String, Long>> entradasOrdenadas = contagemPorDia.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(Collectors.toList());

        // Mapeia cores únicas para cada quantidade de arquivos
        Map<Long, Color> corPorQuantidade = new HashMap<>();

        // Para cada entrada no mapa, cria um ponto de dados no gráfico
        entradasOrdenadas.forEach(entry -> {
            String dia = entry.getKey();
            Long quantidade = entry.getValue();

            XYChart.Data<String, Number> data = new XYChart.Data<>(dia, quantidade);
            series.getData().add(data);

            // Obtém ou gera uma cor única para a quantidade atual
            Color cor = corPorQuantidade.computeIfAbsent(quantidade, q -> gerarCorUnica());

            // Define o estilo da barra no gráfico com a cor específica
            String estiloCor = String.format("-fx-bar-fill: rgba(%d, %d, %d, 0.8);",
                    (int) (cor.getRed() * 255), (int) (cor.getGreen() * 255), (int) (cor.getBlue() * 255));

            // Adiciona um listener para configurar o estilo da barra e exibir tooltips ao passar o mouse
            data.nodeProperty().addListener((observable, oldValue, newNode) -> {
                if (newNode != null) {
                    newNode.setStyle(estiloCor);
                    Tooltip tooltip = new Tooltip("Quantidade: " + quantidade);
                    Tooltip.install(newNode, tooltip);
                    displayLabelForData(data, "Quantidade: " + quantidade, dia);
                }
            });
        });

        // Adiciona a série ao gráfico de barras
        barChart.getData().add(series);
    }

    /**
     * Gera uma cor única para ser usada no gráfico.
     *
     * @return Uma instância de Color representando uma cor única.
     */
    private static Color gerarCorUnica() {
        // Implemente lógica para gerar uma cor única, se necessário
        return Color.rgb((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255));
    }

    /**
     * Adiciona um rótulo ao ponto de dados no gráfico.
     *
     * @param data Ponto de dados no gráfico.
     * @param text Texto a ser exibido no rótulo.
     * @param dia  Dia correspondente ao ponto de dados.
     */
    private static void displayLabelForData(XYChart.Data<String, Number> data, String text, String dia) {
        // Cria um tooltip com informações sobre a quantidade e o dia
        Tooltip tooltip = new Tooltip(text + "\nDia: " + dia);
        // Instala o tooltip no nó associado ao ponto de dados
        Tooltip.install(data.getNode(), tooltip);

        // Adiciona listeners para alterar o estilo ao passar o mouse sobre o nó
        data.nodeProperty().addListener((observable, oldValue, newNode) -> {
            if (newNode != null) {
                // Configura um estilo diferente ao passar o mouse sobre o nó
                newNode.setOnMouseEntered(event -> {
                    newNode.setStyle("-fx-bar-fill: derive(turquoise, 50%);"); // Altere a cor conforme necessário
                });
                // Limpa o estilo ao sair do nó
                newNode.setOnMouseExited(event -> {
                    newNode.setStyle(null);
                });
            }
        });
    }

    /**
     * Método auxiliar para formatar LocalDate como String.
     *
     * @param localDate  Data a ser formatada.
     * @return           Representação formatada da data.
     */
    private static String formatYearMonthDay(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
