package br.gov.df.economia.sistemaauditoriaoraclehive.gui;

import br.gov.df.economia.sistemaauditoriaoraclehive.db.DatabaseConfig;
import br.gov.df.economia.sistemaauditoriaoraclehive.gui.util.*;
import br.gov.df.economia.sistemaauditoriaoraclehive.Validacoes.TipoDoc;
import br.gov.df.economia.sistemaauditoriaoraclehive.db.HiveDecDatabaseOperations;
import br.gov.df.economia.sistemaauditoriaoraclehive.db.ManipuladorBancoDados;
import br.gov.df.economia.sistemaauditoriaoraclehive.model.entities.QuantidadeDocumentoArquivo;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controlador para a interface gráfica que exibe a quantidade de documentos por arquivo em um ambiente Hive.
 */
public class QuantidadeDocumentoArquivoController implements Initializable {
    // Logger para registro de mensagens
    private static final Logger LOGGER = Logger.getLogger(QuantidadeDocumentoArquivoController.class.getName());

    // Formato da data utilizado na aplicação
    private static final String DATE_FORMAT = "yyyyMMdd";

    // Mensagem de erro para ambiente não configurado corretamente
    private static final String ERRO_AMBIENTE_NAO_CONFIGURADO = "Erro: As variáveis de ambiente não estão configuradas corretamente.";
    @FXML
    private Label tituloTableViews;

    // Componentes da interface gráfica mapeados do arquivo FXML
    @FXML
    private Label labelTabelaHive;
    @FXML
    private Label labelArquivo;
    @FXML
    private Label labelDia;
    @FXML
    private Label labelTotal;
    @FXML
    private Label txtTotalArquivo;
    @FXML
    private Label txtTotalArquivoDistinto;
    @FXML
    private ComboBox<TipoDoc> comboBoxTabelas;
    @FXML
    private ComboBox<String> comboBoxData;
    @FXML
    private TableView<QuantidadeDocumentoArquivo> tableViewQuantidadeDocumentosArquivo;
    @FXML
    private TableColumn<QuantidadeDocumentoArquivo, String> columnTabelaHive;
    @FXML
    private TableColumn<QuantidadeDocumentoArquivo, String> columnArquivo;
    @FXML
    private TableColumn<QuantidadeDocumentoArquivo, String> columnDia;
    @FXML
    private TableColumn<QuantidadeDocumentoArquivo, Integer> columnTotal;
    @FXML
    private RadioButton radioGerarArquivoIndividual;
    @FXML
    private RadioButton radioGerarArquivosTodos;
    private ToggleGroup toggleGroup = new ToggleGroup();
    @FXML
    private StackPane stackPaneRoot;
    @FXML
    private BarChart<String, Number> barChart;
    @FXML
    private ComboBox<TipoDoc> comboTipoDoc;
    @FXML
    private StackPane quantidadeDocumentoArquivoPane;
    // Instância para operações no banco de dados Hive
    private HiveDecDatabaseOperations hiveDatabaseOperations;
    // Instância única do controlador
    private static QuantidadeDocumentoArquivoController instance;
    // Referência ao controlador da lista de agenda
    private AgendaListController agendaListController;
    private ManipuladorBancoDados manipuladorBancoDados;
    /**
     * Construtor padrão.
     */
    public QuantidadeDocumentoArquivoController() {
        instance = this;
        // Crie uma instância de ManipuladorBancoDados no construtor
        manipuladorBancoDados = new ManipuladorBancoDados(
                System.getenv("HIVE_JDBC_URL"),
                System.getenv("HIVE_USERNAME"),
                System.getenv("HIVE_PASSWORD")
        );
    }
    /**
     * Obtém a instância única do controlador.
     *
     * @return Instância única do controlador.
     */
    public static QuantidadeDocumentoArquivoController getInstance() {
        return instance;
    }
    /**
     * Define o controlador da lista de agenda.
     *
     * @param agendaListController Controlador da lista de agenda.
     */
    public void setAgendaListController(AgendaListController agendaListController) {
        this.agendaListController = agendaListController;
    }
    // Define o ComboBox de tabelas a partir da classe AgendaListController
    public void setComboBoxTabelas(ComboBox<TipoDoc> comboBoxTabelas) {
        this.comboBoxTabelas = comboBoxTabelas;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configura o ToggleGroup para os RadioButtons
        ToggleGroup toggleGroup = new ToggleGroup();
        radioGerarArquivoIndividual.setToggleGroup(toggleGroup);
        radioGerarArquivosTodos.setToggleGroup(toggleGroup);
        // Defina um RadioButton como selecionado por padrão
        radioGerarArquivosTodos.setSelected(true);
        try {
            // Carrega as configurações do banco de dados Hive a partir do arquivo de configuração
            DatabaseConfig.loadConfigurations();
            // Verifica se as configurações do ambiente estão corretas
            if (configuracoesAmbienteCorretas()) {
                // Inicializa os ComboBoxes, adiciona listeners e configura o ComboBox comboTipoDoc
                initializeComboBoxes();
                addEventListeners();
                // Supondo que você tenha uma maneira de obter a lista de TipoDoc
                List<TipoDoc> tipoDocs = manipuladorBancoDados.obterTipoDocs();
                // Modificação: Passe o comboBoxTabelas e a lista de TipoDoc explicitamente para o método
                ComboBoxUtil.initializeComboBoxTipoDoc(comboBoxTabelas, tipoDocs);
                tableViewQuantidadeDocumentosArquivo.getSortOrder().addAll(columnTabelaHive, columnArquivo);
            } else {
                LOGGER.log(Level.SEVERE, ERRO_AMBIENTE_NAO_CONFIGURADO);
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao inicializar o controlador", e);
        }
    }

    // Este método é um placeholder e precisa ser implementado de acordo com a sua lógica de aplicação
    private List<TipoDoc> obterTipoDocs() {
        // Use diretamente os valores do enum TipoDoc
        List<TipoDoc> tipoDocsAtivos = Arrays.stream(TipoDoc.values())
                .filter(tipoDoc -> "sim".equalsIgnoreCase(tipoDoc.getAtivo()))
                .collect(Collectors.toList());
        return tipoDocsAtivos;
    }
    /**
     * Método para inicializar os ComboBoxes.
     */
    private void initializeComboBoxes() {
        // Supondo que você tenha uma maneira de obter a lista de TipoDoc
        List<TipoDoc> tipoDocs = obterTipoDocs(); // Este método precisa ser implementado por você
        // Passe o comboBoxTabelas e a lista de TipoDoc explicitamente para o método
        ComboBoxUtil.initializeComboBoxTipoDoc(comboBoxTabelas, tipoDocs);
        // Inicializa o comboBoxData com os dias configurados
        ComboBoxUtil.initializeComboBoxData(comboBoxData, Configuracao.dias);
    }
    /**
     * Adiciona os listeners aos ComboBoxes.
     */
    private void addEventListeners() {
        // Verifica se comboBoxTabelas não é nulo antes de adicionar os listeners
        if (comboBoxTabelas != null) {
            ComboBoxUtil.adicionarEventListeners(comboBoxTabelas, this::onTipoDocSelected);
        }
        if (comboBoxData != null) {
            ComboBoxUtil.adicionarEventListeners(comboBoxData, this::onDataSelected);
        }
    }
    /**
     * Callback chamado quando um TipoDoc é selecionado no ComboBox.
     *
     * @param tipoDoc TipoDoc selecionado.
     */
    private void onTipoDocSelected(TipoDoc tipoDoc) {
        if (tipoDoc != null) {
            updateUI(tipoDoc, comboBoxData.getSelectionModel().getSelectedItem());
        }
    }
    /**
     * Callback chamado quando uma data é selecionada no ComboBox.
     *
     * @param dataSelecionada Data selecionada.
     */
    private void onDataSelected(String dataSelecionada) {
        TipoDoc tipoDocSelecionado = comboBoxTabelas.getSelectionModel().getSelectedItem();
        if (tipoDocSelecionado != null && dataSelecionada != null) {
            updateUI(tipoDocSelecionado, dataSelecionada);
        }
    }
    /**
     * Verifica se as configurações do ambiente estão corretas.
     *
     * @return true se as configurações estão corretas, false caso contrário.
     */
    private boolean configuracoesAmbienteCorretas() {
        String jdbcUrl = System.getenv("HIVE_JDBC_URL");
        String username = System.getenv("HIVE_USERNAME");
        String password = System.getenv("HIVE_PASSWORD");
        return jdbcUrl != null && username != null && password != null;
    }
    /**
     * Atualiza a interface gráfica com base no TipoDoc e na data selecionados.
     *
     * @param tipoDoc         TipoDoc selecionado.
     * @param dataSelecionada Data selecionada.
     */
    private void updateUI(TipoDoc tipoDoc, String dataSelecionada) {
        try {
            // Obtém apenas os tipos de documentos ativos
            List<TipoDoc> tiposAtivos = Arrays.stream(TipoDoc.values())
                    .filter(tipo -> "sim".equalsIgnoreCase(tipo.getAtivo()))
                    .collect(Collectors.toList());

            if (radioGerarArquivoIndividual.isSelected()) {
                // Verifica se o tipoDoc selecionado está ativo
                if (tipoDoc != null && tiposAtivos.contains(tipoDoc)) {
                    // Execute a consulta SQL para um tipoDoc específico
                    List<QuantidadeDocumentoArquivo> resultados = HiveDecDatabaseOperations.executeSQLQuantidadeDocumentos(
                            HiveDecDatabaseOperations.getHiveConnection(
                                    System.getenv("HIVE_JDBC_URL"),
                                    System.getenv("HIVE_USERNAME"),
                                    System.getenv("HIVE_PASSWORD")),
                            tipoDoc.getTabelaHive(), dataSelecionada);

                    // Atualize a interface gráfica com os resultados obtidos
                    TableViewManager.popularTabelaQuantidadeDocumentosArquivo(tableViewQuantidadeDocumentosArquivo, resultados, columnTabelaHive, columnArquivo, columnDia, columnTotal);

                    // Atualize os rótulos e o gráfico
                    atualizarLabelsEGrafico(resultados);

                    // Gere o arquivo para o tipoDoc específico
                    CSVUtils.gerarArquivo(resultados, tipoDoc);
                }
            } else if (radioGerarArquivosTodos.isSelected()) {
                // Execute a consulta SQL para todos os tipos de documentos ativos
                for (TipoDoc tipo : tiposAtivos) {
                    List<QuantidadeDocumentoArquivo> resultadosTipo = HiveDecDatabaseOperations.executeSQLQuantidadeDocumentos(
                            HiveDecDatabaseOperations.getHiveConnection(
                                    System.getenv("HIVE_JDBC_URL"),
                                    System.getenv("HIVE_USERNAME"),
                                    System.getenv("HIVE_PASSWORD")),
                            tipo.getTabelaHive(), dataSelecionada);

                    // Atualize a interface gráfica com os resultados obtidos
                    TableViewManager.popularTabelaQuantidadeDocumentosArquivo(tableViewQuantidadeDocumentosArquivo, resultadosTipo, columnTabelaHive, columnArquivo, columnDia, columnTotal);

                    // Atualize os rótulos e o gráfico
                    atualizarLabelsEGrafico(resultadosTipo);

                    // Gere o arquivo para o tipoDoc específico
                    CSVUtils.gerarArquivo(resultadosTipo, tipo);

                    // Fazer Join entre dados de agendamentos do Oracle e dados de carga do Hive.
                    String agendaFilePath = "\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\Agenda.csv";
                    String cargasHiveFilePath = "\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\CargasHive.csv";
                    String outputFilePath = "\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\Resultado.csv";

                    CSVUtils.joinCSVFiles(agendaFilePath, cargasHiveFilePath, outputFilePath);
                }
            }
        } catch (IllegalArgumentException e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao executar a consulta SQL", e);
        }
    }
    /**
     * Atualiza os rótulos e o gráfico com base nos resultados obtidos.
     *
     * @param resultados Lista de resultados de QuantidadeDocumentoArquivo.
     */
    private void atualizarLabelsEGrafico(List<QuantidadeDocumentoArquivo> resultados) {
        // Atualiza o label txtTotalArquivo com o total de resultados da query
        txtTotalArquivo.setText("Total de Arquivos: " + resultados.size());
        // Atualiza o label txtTotalArquivoDistinto com a contagem de arquivos distintos
        long diasDistintos = resultados.stream().map(QuantidadeDocumentoArquivo::getDia).distinct().count();
        txtTotalArquivoDistinto.setText("Dias Distintos: " + diasDistintos);
        // Atualiza o gráfico com os resultados
        ChartsUtils.atualizarGrafico(barChart, resultados);
    }
    /**
     * Obtém o ComboBox associado às tabelas.
     *
     * @return O ComboBox que representa as tabelas.
     */
    public ComboBox<TipoDoc> getComboBoxTabelas() {
        return comboBoxTabelas;
    }
    // Método auxiliar para obter o TipoDoc correspondente pela coluna tabelaHive
    public static TipoDoc getTipoDocByTabelaHive(TipoDoc tipoDoc, String tabelaHive) {
        // Iterar sobre os valores do enum TipoDoc
        for (TipoDoc doc : TipoDoc.values()) {
            // Verificar se a tabelaHive do TipoDoc atual é igual à tabelaHive passada como argumento
            if (doc.getTabelaHive().equals(tabelaHive)) {
                // Retornar o TipoDoc correspondente se encontrado
                return doc;
            }
        }
        // Retornar null se nenhum TipoDoc correspondente for encontrado
        return null;
    }
}