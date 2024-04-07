package com.example.dechivejavafx.gui;

import com.example.dechivejavafx.Validacoes.TipoDoc;
import com.example.dechivejavafx.application.Main;
import com.example.dechivejavafx.db.HiveDecDatabaseOperations;
import com.example.dechivejavafx.db.HiveSpedDatabaseOperations;
import com.example.dechivejavafx.db.OracleSpedDatabaseOperations;
import com.example.dechivejavafx.gui.util.Alerts;
import com.example.dechivejavafx.gui.util.CSVUtils;
import com.example.dechivejavafx.model.entities.Agenda;
import com.example.dechivejavafx.model.services.AgendaService;
import com.example.dechivejavafx.model.services.SchedulerManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

// Controlador principal da aplicação
public class MainViewController implements Initializable {

    private static final Logger logger = Logger.getLogger(MainViewController.class.getName());

    // Serviço para manipulação de agendas
    private final AgendaService service = new AgendaService();
    // Private Stage
    private Stage primaryStage;

    // FXML MenuItems
    @FXML private MenuItem menuItemAgenda; // FXML MenuItem
    @FXML private MenuItem menuItemSped_9900; // FXML MenuItem
    @FXML private MenuItem menuItemHiveSped_9900; // FXML MenuItem
    @FXML private MenuItem menuItemPendenciaProcessamento0000; // FXML MenuItem
    @FXML private MenuItem menuItemAbout; // FXML MenuItem
    @FXML private MenuItem menuItemTotalizacaoNfe; // FXML MenuItem
    @FXML private MenuItem menuItemQuantidadeDocumentoArquivo; // FXML MenuItem
    @FXML private MenuItem menuItemOracleHive; // FXML MenuItem
    @FXML private MenuItem menuProcessarPendencia; // FXML MenuItem
    @FXML private MenuItem MenuProcessarDuplicidadeId; // FXML MenuItem
    @FXML private MenuItem menuItemGerarPendencia9900Oracle_TabelasHiveGeral;
    @FXML private MenuItem menuItemGerarPendenciaSpedFormulario;
    // FXML MenuItems Fechar
    @FXML private MenuItem menuItemFecharAgenda; // FXML MenuItem
    @FXML private MenuItem menuItemFecharAbout; // FXML MenuItem
    @FXML private MenuItem menuItemFecharOracleHive; // FXML MenuItem
    @FXML private MenuItem menuItemFecharProcessarPendenciaHive; // FXML MenuItem
    @FXML private MenuItem menuItemFecharQuantidadeDocumentoArquivo; // FXML MenuItem
    @FXML private MenuItem menuItemFecharSped9900; // FXML MenuItem
    @FXML private MenuItem menuItemFecharTotalizacaoNfe; // FXML MenuItem
    @FXML private MenuItem menuItemGerarPendencia9900Oracle_TabelasHive;
    // Public MenuItems Sped
    public MenuItem menuItemPendenciasHive;
    public MenuItem menuItemPendenciaFormulario0000;
    public MenuItem menuItemSped0990xTabelasSped;

    // FXML ComboBox and TextField
    @FXML private ComboBox<TipoDoc> comboTipoDoc; // FXML ComboBox
    @FXML private ComboBox<TipoDoc> comboTipoDocCentral; // FXML ComboBox
    @FXML private TextField txtDias; // FXML TextField

    // FXML TitledPane and Layout Nodes
    @FXML private TitledPane titledPanecomboTipoDocCentral; // FXML TitledPane
    @FXML private VBox mainVBox; // FXML VBox
    @FXML private ScrollPane mainScrollPane; // FXML ScrollPane

    // Other Controllers and Managers
    private QuantidadeDocumentoArquivoController quantidadeDocumentoArquivoController; // Controller
    private AgendaListController agendaListController; // Controller
    private static OracleSpedDatabaseOperations oracleSpedDatabaseOperations; // Manager
    private SchedulerManager schedulerManager; // Manager


    // Método de inicialização do controlador
    @Override
    public void initialize(URL uri, ResourceBundle rb) {

        // Configuração inicial do ComboBox de tipo de documento central
        configurarComboTipoDocCentral();

        // Configura a visibilidade inicial dos itens de menu
        configurarVisibilidadeInicialItensMenu();

        // Adiciona um listener para o ComboBox de tipo de documento central
        adicionarListenerComboTipoDocCentral();

        // Inicializa o gerenciador de agendamento
        schedulerManager = new SchedulerManager(this);
        schedulerManager.iniciarAgendamento();
        schedulerManager.agendarProcessamentoDuplicidadeId(); // Agendamento do método ProcessarDuplicidadeId
    }

    // Método chamado quando a opção "About" no menu é selecionada
    @FXML
    public void onMenuItemAboutAction() {
        loadView("/Fxml/About.fxml", x -> {});
    }
    @FXML
    public void onMenuItemSped9900Action() {
        // Define a cena atual e carrega a view de quantidade de documentos em arquivo
        SceneManager.setCurrentScene(Main.getMainScene());
        // Inicializa a instância de OracleSpedDatabaseOperations
        oracleSpedDatabaseOperations = new OracleSpedDatabaseOperations();

        // Chama o método após a inicialização
        oracleSpedDatabaseOperations.executeQueryAndSaveToCSV();
        loadView("/Fxml/Sped9900.fxml", x -> {});
    }

    @FXML
    public void onMenuItemHiveSped9900Action() {
        // Define a cena atual e carrega a view de quantidade de documentos em arquivo
        SceneManager.setCurrentScene(Main.getMainScene());

        // Cria uma instância de HiveSpedController, que inicializa automaticamente as variáveis de ambiente
        HiveSpedController hiveSpedController = new HiveSpedController();

        hiveSpedController.executeQueryAndSaveToCSV();
        // Carrega a view desejada
        loadView("/Fxml/Sped9900.fxml", x -> {});
    }


    @FXML
    public void onMenuItemHivedivergencia9900TabelasSpedAction() {
        // Define a cena atual e carrega a view de quantidade de documentos em arquivo
        SceneManager.setCurrentScene(Main.getMainScene());

        // Obtém os valores das variáveis de ambiente para conexão com o Hive
        String jdbcURL = System.getenv("HIVE_JDBC_URL");
        String username = System.getenv("HIVE_USERNAME");
        String password = System.getenv("HIVE_PASSWORD");

        // Cria uma instância de HiveSpedDatabaseOperations com os parâmetros de conexão
        HiveSpedDatabaseOperations hiveSpedDB = new HiveSpedDatabaseOperations(jdbcURL, username, password);

        // Obtém a data atual e subtrai 60 dias
        LocalDateTime dataAtual = LocalDateTime.now().minusDays(60);

        // Define o nome do arquivo CSV
        String nomeArquivo = "X:\\Dados\\SPED\\divergencia9900TabelasSped.csv";

        // Executa a consulta e grava no arquivo CSV
        hiveSpedDB.divergencia9900TabelasSpedAndWriteToCSV(dataAtual, nomeArquivo);

        // Certifique-se de desconectar após o uso
        hiveSpedDB.disconnect();

        // Carrega a view desejada
        loadView("/Fxml/Sped9900.fxml", x -> {});
    }

    public void onMenuItemPendencia9900OracleTabelasHiveAction() {
        // Define a cena atual, se necessário
        SceneManager.setCurrentScene(Main.getMainScene());

        // Obtém os valores das variáveis de ambiente para conexão com o Hive
        String jdbcURL = System.getenv("HIVE_JDBC_URL");
        String username = System.getenv("HIVE_USERNAME");
        String password = System.getenv("HIVE_PASSWORD");

        // Cria uma instância de HiveSpedDatabaseOperations com os parâmetros de conexão
        HiveSpedDatabaseOperations hiveSpedDB = new HiveSpedDatabaseOperations(jdbcURL, username, password);

        // Define os caminhos para o arquivo de entrada e para o arquivo de saída
        String inputFilePath = "X:\\Dados\\SPED\\ORACLE_SPED_9900.csv";
        String outputFilePath9900OracleTabelasHive = "X:\\Dados\\SPED\\ResultadoQuery.csv";

        try {
            // Executa a consulta com join e escreve os resultados no arquivo CSV de saída
            hiveSpedDB.executeQueryWithJoinAndWriteResults(inputFilePath, outputFilePath9900OracleTabelasHive);
            System.out.println("Operação concluída com sucesso.");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            // Certifique-se de desconectar após o uso
            hiveSpedDB.disconnect();
        }

         loadView("/Fxml/Sped9900.fxml", x -> {});
    }


    @FXML
    public void onMenuItemPendenciaProcessamento0000() {
        String oracleFilePath = "X:\\Dados\\SPED\\ORACLE_SPED_9900.csv";
        String hiveFilePath = "X:\\Dados\\SPED\\HIVE_SPED_9900.csv";
        String outputFilePath = "X:\\Dados\\SPED\\Pendencia_Processamento_0000.csv";

        // Inicializa a instância de OracleSpedDatabaseOperations
        oracleSpedDatabaseOperations = new OracleSpedDatabaseOperations();

        // Chama o método após a inicialização
        oracleSpedDatabaseOperations.executeQueryAndSaveToCSV();

        // Cria uma instância de HiveSpedController, que inicializa automaticamente as variáveis de ambiente
        HiveSpedController hiveSpedController = new HiveSpedController();

        hiveSpedController.executeQueryAndSaveToCSV();

        CSVUtils.FileComparisonService.compareAndSaveDifferences(oracleFilePath, hiveFilePath, outputFilePath);
        // Carrega a view desejada
        loadView("/Fxml/Sped9900.fxml", x -> {});
    }

    @FXML
    public void onMenuItemPendenciaFormulario0000() {
        String oracleFilePath = "X:\\Dados\\SPED\\ORACLE_SPED_9900.csv";
        String hiveFilePath = "X:\\Dados\\SPED\\HIVE_SPED_9900.csv";
        String outputFilePath = "X:\\Dados\\SPED\\Pendencia_Processamento_0000.csv";

        CSVUtils.FileComparisonService.compareAndSaveDifferences(oracleFilePath, hiveFilePath, outputFilePath);
        // Carrega a view desejada
        loadView("/Fxml/Sped9900.fxml", x -> {});
    }

    @FXML
    public void onMenuItemPendenciaFormulario() {
        // Carrega a view desejada
        loadView("/Fxml/Sped9900.fxml", x -> {});
    }

    @FXML
    public void onMenuItemTotalizacaoNfeAction() {
        // Carrega a view de totalização de Nfe
        loadView("/Fxml/NfeTotalizacao.fxml", x -> {});
    }

    @FXML
    public void onMenuItemQuantidadeDocumentoArquivoAction() {
        // Define a cena atual e carrega a view de quantidade de documentos em arquivo
        SceneManager.setCurrentScene(Main.getMainScene());
        loadView("/Fxml/QuantidadeDocumentoArquivo.fxml", x -> {});
    }

    // Método chamado quando a opção "Agenda" no menu é selecionada
    @FXML
    public void onMenuItemAgendaAction() {
        // Define a cena atual e carrega a view da lista de agendas
        SceneManager.setCurrentScene(Main.getMainScene());
        loadView("/Fxml/AgendaList.fxml", this::initializeAgendaListController);
    }

    // Método chamado quando a opção "Agenda" no menu é selecionada
    @FXML
    public void onMenuProcessarPendenciaAction() {
        // Limpar a cena antes de carregar novas visualizações
        if (isFormularioPresente("agendaListPane")) {
            handleRemoveAgendaList();
        }

        if (isFormularioPresente("pendenciasHivePane")) {
            handleRemoveProcessarPendenciaHive();
        }

        if (isFormularioPresente("oracleHivePane")) {
            handleRemoveOracleHive();
        }

        if (isFormularioPresente("quantidadeDocArquivoPane")) {
            handleRemoverFormulario();
        }

        if (isFormularioPresente("sped9900Pane")) {
            handleRemoveSped9900();
        }


        // Define a cena atual e carrega a view da lista de agendas
        SceneManager.setCurrentScene(Main.getMainScene());
        loadView("/Fxml/AgendaList.fxml", this::initializeAgendaListController);
        loadView("/Fxml/QuantidadeDocumentoArquivo.fxml", x -> {});

        // Adiciona a opção "NFCeCanc" ao ComboBox comboTipoDocCentral
        TipoDoc tipoDocNFCeCanc = TipoDoc.NFCeCanc;
        comboTipoDocCentral.getSelectionModel().select(tipoDocNFCeCanc);

        // Chama o método para processar a seleção do tipo de documento central e tabelas
        selecionarTipoDocCentralETabelas(tipoDocNFCeCanc);
        loadView("/Fxml/OracleHive.fxml", x -> {});
        loadView("/Fxml/PendenciasHive.fxml", x -> {});
        handleRemoveOracleHive();
        CSVUtils.deleteFilesExceptPendencia("\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\");
    }
    @FXML
    public void onMenuProcessarDuplicidadeIdAction() {
        // Obtém os valores das variáveis de ambiente
        String jdbcURL = System.getenv("HIVE_JDBC_URL");
        String username = System.getenv("HIVE_USERNAME");
        String password = System.getenv("HIVE_PASSWORD");
        // Gerar arquivo de duplicidades de Id das tabelas principais.
        HiveDecDatabaseOperations.executeHiveQueryIdDuplicidadePrincipal(jdbcURL, username, password);
        // Gerar arquivo de duplicidades de Id e itens das tabelas detalhe.
        HiveDecDatabaseOperations.executeHiveQueryIdDuplicidadeDetalhe(jdbcURL, username, password);
    }
    // Verifica se um formulário específico está presente na cena
    private boolean isFormularioPresente(String formId) {
        VBox mainVBox = (VBox) ((ScrollPane) Main.getMainScene().getRoot()).getContent();
        return mainVBox.lookup("#" + formId) != null;
    }

    @FXML
    public void onMenuItemProcessarPendenciaHiveAction() {
        // Define a cena atual e carrega a view de quantidade de documentos em arquivo
        SceneManager.setCurrentScene(Main.getMainScene());
        loadView("/Fxml/PendenciasHive.fxml", x -> {});
    }


    @FXML
    public void onMenuItemDetNFeNFCeInfAction() {
        // Define a cena atual e carrega a view de quantidade de documentos em arquivo
        SceneManager.setCurrentScene(Main.getMainScene());
        loadView("/Fxml/DetNFeNFCeInf.fxml", x -> {});
    }

    @FXML
    public void onMenuItemOracleHive() {
        // Define a cena atual e carrega a view de quantidade de documentos em arquivo
        SceneManager.setCurrentScene(Main.getMainScene());
        loadView("/Fxml/OracleHive.fxml", x -> {});
    }

    // Inicializa o controlador da lista de agendas
    private void initializeAgendaListController(AgendaListController controller) {
        controller.setAgendaService(service);
        controller.updateTableView();
        agendaListController = controller;  // Armazena a instância de AgendaListController
    }

    @FXML
    public void comboTipoDocChanged() {
        try {
            TipoDoc selectedTipoDoc = comboTipoDoc.getValue();
            int dias = Integer.parseInt(txtDias.getText());
            List<Agenda> agendas = service.findAllByTipoDoc(selectedTipoDoc, dias);
            // Atualiza a exibição com as novas agendas

        } catch (NumberFormatException e) {
            handleNumberFormatException();
        } catch (Exception e) {
            handleGenericException(e);
        }
    }

    // Carrega uma nova view e executa uma ação de inicialização
    private synchronized <T> void loadView(String relativeName, Consumer<T> initializingAction) {
        try {
            // Cria um loader para carregar a nova view
            FXMLLoader loader = new FXMLLoader(getClass().getResource(relativeName));
            // Carrega o layout raiz e a cena principal
            Parent root = loader.load();
            Scene mainScene = Main.getMainScene();
            // Obtém a VBox principal da cena
            VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

            // Adiciona a nova view à VBox principal
            mainVBox.getChildren().add(root);

            // Obtém o controlador da nova view
            T controller = loader.getController();
            // Executa a ação de inicialização no controlador
            initializingAction.accept(controller);

            // Configura a visibilidade dos itens de menu
            configurarVisibilidadeItensMenu();

        } catch (IOException e) {
            // Exibe um alerta em caso de erro ao carregar a view
            Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
        }
    }
    // Configura a visibilidade inicial dos itens de menu como false
    private void configurarVisibilidadeInicialItensMenu() {
        // Lista dos itens de menu a serem configurados
        Arrays.asList(menuItemFecharAgenda, menuItemFecharTotalizacaoNfe, menuItemFecharProcessarPendenciaHive,
                        menuItemFecharQuantidadeDocumentoArquivo, menuItemFecharOracleHive, menuItemFecharSped9900,
                        menuItemFecharAbout).forEach(menuItem -> menuItem.setVisible(false));
    }

    /**
     * Configura a visibilidade dos itens de menu com base nos elementos presentes na cena.
     * Este método realiza a verificação da visibilidade de elementos específicos na cena
     * e ajusta a visibilidade dos itens de menu correspondentes e do TitledPane
     * "titledPanecomboTipoDocCentral" de acordo com a presença desses elementos.
     */
    private void configurarVisibilidadeItensMenu() {
        // Obtém a VBox principal da cena
        VBox mainVBox = (VBox) ((ScrollPane) Main.getMainScene().getRoot()).getContent();

        // Mapeia IDs de elementos para seus respectivos itens de menu
        Map<String, MenuItem> elementoParaMenuItem = Map.of(
                "#nfeTotalizacaoPane", menuItemFecharTotalizacaoNfe,
                "#quantidadeDocArquivoPane", menuItemFecharQuantidadeDocumentoArquivo,
                "#agendaListPane", menuItemFecharAgenda,
                "#pendenciasHivePane", menuItemFecharProcessarPendenciaHive,
                "#oracleHivePane", menuItemFecharOracleHive,
                "#sped9900Pane", menuItemFecharSped9900,
                "#aboutPane", menuItemFecharAbout
        );

        // Verifica a visibilidade de cada elemento e configura os itens de menu correspondentes
        elementoParaMenuItem.forEach((elementoId, menuItem) -> {
            boolean isActive = mainVBox.lookup(elementoId) != null;
            menuItem.setVisible(isActive);
        });

        // Ajusta visibilidade do TitledPane "titledPanecomboTipoDocCentral"
        boolean isAgendaListActive = mainVBox.lookup("#agendaListPane") != null;
        boolean isQuantidadeDocumentoArquivoActive = mainVBox.lookup("#quantidadeDocArquivoPane") != null;
        boolean shouldDisplayComboTipoDocCentral = isAgendaListActive && isQuantidadeDocumentoArquivoActive;

        titledPanecomboTipoDocCentral.setVisible(shouldDisplayComboTipoDocCentral);
        titledPanecomboTipoDocCentral.setManaged(shouldDisplayComboTipoDocCentral);
    }
    // Adiciona um listener para o ComboBox de tipo de documento central
    private void adicionarListenerComboTipoDocCentral() {
        // Adiciona um EventHandler para tratar eventos de seleção no comboTipoDocCentral
        comboTipoDocCentral.setOnAction(event -> {
            // Obtém o tipo de documento selecionado
            TipoDoc tipoDocSelecionado = comboTipoDocCentral.getValue();
            // Chama o método para processar a seleção do tipo de documento central e tabelas
            selecionarTipoDocCentralETabelas(tipoDocSelecionado);
        });
    }

    // Trata uma exceção de formato de número inválido, exibindo uma mensagem de erro
    private void handleNumberFormatException() {
        Alerts.showAlert("Error", null, "Please enter a valid number for days.", AlertType.ERROR);
    }

    // Trata uma exceção genérica, exibindo uma mensagem de erro com base na exceção recebida
    private void handleGenericException(Exception e) {
        Alerts.showAlert("Error", null, e.getMessage(), AlertType.ERROR);
    }

    // Classe estática para gerenciar cenas
    public class SceneManager {
        private static Scene currentScene;

        // Obtém a cena atual
        public static Scene getCurrentScene() {
            return currentScene;
        }

        // Define a cena atual
        public static void setCurrentScene(Scene scene) {
            currentScene = scene;
        }
    }

    // Configura o ComboBox de tipo de documento central
    private void configurarComboTipoDocCentral() {
        // Configura o converter para exibição correta no ComboBox
        comboTipoDocCentral.setConverter(new StringConverter<>() {
            @Override
            public String toString(TipoDoc tipoDoc) {
                return tipoDoc == null ? null : tipoDoc.toString();
            }

            @Override
            public TipoDoc fromString(String string) {
                // Converte a String de volta para o TipoDoc correspondente
                return Arrays.stream(TipoDoc.values())
                        .filter(tipoDoc -> tipoDoc.toString().equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });

        // Filtra os tipos de documento ativos
        ObservableList<TipoDoc> tipoDocOptionsAtivas = FXCollections.observableArrayList(
                Arrays.stream(TipoDoc.values())
                        .filter(tipoDoc -> "sim".equalsIgnoreCase(tipoDoc.getAtivo()))
                        .collect(Collectors.toList())
        );

        // Configura as opções do ComboBox
        comboTipoDocCentral.setItems(tipoDocOptionsAtivas);

        // Adiciona um listener para processar a seleção do tipo de documento central
        comboTipoDocCentral.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Ação a ser realizada quando o tipo de documento central for alterado
            }
        });
    }

    // Obtém o ComboBox de tipo de documento central
    public ComboBox<TipoDoc> getComboTipoDocCentral() {
        return comboTipoDocCentral;
    }
    /**
     * Seleciona o tipo de documento central e atualiza tabelas em diferentes controladores.
     *
     * Este método é responsável por realizar as seguintes ações:
     *
     * 1. Seleciona o tipo de documento central no ComboBox "comboTipoDocCentral".
     * 2. Seleciona o mesmo tipo de documento central no ComboBox "comboBoxTabelas" do controlador
     *    "QuantidadeDocumentoArquivoController" para manter a consistência entre os ComboBoxes.
     * 3. Seleciona o mesmo tipo de documento central no ComboBox "comboTipoDoc" do controlador
     *    "AgendaListController" para manter a consistência entre os ComboBoxes.
     *
     * @param tipoDoc O tipo de documento central a ser selecionado.
     */
    public void selecionarTipoDocCentralETabelas(TipoDoc tipoDoc) {
        // 1. Seleciona o item no comboTipoDocCentral
        comboTipoDocCentral.setValue(tipoDoc);

        // 2. Seleciona o mesmo item no comboBoxTabelas em QuantidadeDocumentoArquivoController
        QuantidadeDocumentoArquivoController quantidadeDocumentoArquivoController = QuantidadeDocumentoArquivoController.getInstance();
        ComboBox<TipoDoc> comboBoxTabelas = quantidadeDocumentoArquivoController.getComboBoxTabelas();

        try {
            // Lógica para selecionar o tipoDoc no comboBoxTabelas
            if (comboBoxTabelas != null) {
                comboBoxTabelas.setValue(tipoDoc);
            } else {
                // Lança uma exceção se o comboBoxTabelas não foi inicializado corretamente
                throw new IllegalStateException("O comboBoxTabelas em QuantidadeDocumentoArquivoController não foi inicializado corretamente.");
            }
        } catch (Exception e) {
            // Loga a exceção em vez de imprimir no console
            logger.log(Level.SEVERE, "Erro ao selecionar o tipoDoc no comboBoxTabelas", e);
        }

        // 3. Seleciona o mesmo item no comboTipoDoc em AgendaListController
        if (agendaListController != null) {
            agendaListController.selecionarTipoDoc(tipoDoc);
        } else {
            // Loga a mensagem em vez de imprimir no console
            logger.log(Level.SEVERE, "A instância de AgendaListController não foi configurada corretamente.");
        }
    }

    // Remove um formulário específico da cena
    private void removeForm(String formId, MenuItem correspondingMenuItem) {
        Scene mainScene = Main.getMainScene();
        VBox mainVBox = (VBox) ((ScrollPane) mainScene.getRoot()).getContent();

        // Obtém o nó do formulário a ser removido
        Node existingForm = mainVBox.lookup("#" + formId);
        if (existingForm != null) {
            // Remove o nó do VBox principal
            mainVBox.getChildren().remove(existingForm);
        }

        // Configura a visibilidade do titledPaneComboTipoDocCentral com base nos formulários presentes
        boolean isNfeTotalizacaoActive = mainVBox.lookup("#nfeTotalizacaoPane") != null;
        boolean isQuantidadeDocumentoArquivoActive = mainVBox.lookup("#quantidadeDocArquivoPane") != null;
        boolean isAgendaListActive = mainVBox.lookup("#agendaListPane") != null;
        boolean isPendenciaHiveActive = mainVBox.lookup("#pendenciasHivePane") != null;
        boolean isOracleHiveActive = mainVBox.lookup("#OracleHivePane") != null;
        boolean isOracleSped9900 = mainVBox.lookup("#sped9900Pane") != null;
        boolean isAboutActive = mainVBox.lookup("#aboutPane") != null;

        titledPanecomboTipoDocCentral.setVisible(isAgendaListActive && isQuantidadeDocumentoArquivoActive);
        titledPanecomboTipoDocCentral.setManaged(isAgendaListActive && isQuantidadeDocumentoArquivoActive);

        // Configura a visibilidade do item de menu correspondente com base nos formulários presentes
        correspondingMenuItem.setVisible(isQuantidadeDocumentoArquivoActive);
        correspondingMenuItem.setVisible(isNfeTotalizacaoActive);
        correspondingMenuItem.setVisible(isAgendaListActive);
        correspondingMenuItem.setVisible(isPendenciaHiveActive);
        correspondingMenuItem.setVisible(isOracleHiveActive);
        correspondingMenuItem.setVisible(isOracleSped9900);
        correspondingMenuItem.setVisible(isAboutActive);
    }

    // Manipula a ação de remoção do formulário "quantidadeDocArquivoPane"
    @FXML
    private void handleRemoverFormulario() {
        // Chama o método genérico para remover o formulário, passando o ID do formulário e o item de menu correspondente
        removeForm("quantidadeDocArquivoPane", menuItemFecharQuantidadeDocumentoArquivo);
    }

    // Manipula a ação de remoção do formulário "nfeTotalizacaoPane"
    @FXML
    private void handleRemoverNfeTotalizacao() {
        // Chama o método genérico para remover o formulário, passando o ID do formulário e o item de menu correspondente
        removeForm("nfeTotalizacaoPane", menuItemFecharTotalizacaoNfe);
    }

    // Manipula a ação de remoção do formulário "agendaListPane"
    @FXML
    private void handleRemoveAgendaList() {
        // Chama o método genérico para remover o formulário, passando o ID do formulário e o item de menu correspondente
        removeForm("agendaListPane", menuItemFecharAgenda);
    }

    // Manipula a ação de remoção do formulário "processarPendenciasPane"
    @FXML
    private void handleRemoveProcessarPendenciaHive() {
        // Chama o método genérico para remover o formulário, passando o ID do formulário e o item de menu correspondente
        removeForm("pendenciasHivePane", menuItemFecharProcessarPendenciaHive);
    }

    // Manipula a ação de remoção do formulário "OracleHivePane"
    @FXML
    private void handleRemoveOracleHive() {
        // Chama o método genérico para remover o formulário, passando o ID do formulário e o item de menu correspondente
        removeForm("oracleHivePane", menuItemFecharOracleHive);
    }

    // Manipula a ação de remoção do formulário "OracleHivePane"
    @FXML
    private void handleRemoveSped9900() {
        // Chama o método genérico para remover o formulário, passando o ID do formulário e o item de menu correspondente
        removeForm("sped9900Pane", menuItemFecharSped9900);
    }

    // Manipula a ação de remoção do formulário "aboutPane"
    @FXML
    private void handleRemoveAboutForm() {
        // Chama o método genérico para remover o formulário, passando o ID do formulário e o item de menu correspondente
        removeForm("aboutPane", menuItemFecharAbout);
    }

    public void iniciarAgendamentoDuplicidadeId(ScheduledExecutorService scheduler) {
        scheduler.scheduleAtFixedRate(this::onMenuProcessarDuplicidadeIdAction, 0, 7, TimeUnit.DAYS); // Agendar para rodar a cada 7 dias
    }

    public void pararAgendamento(ScheduledExecutorService scheduler) {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    public void setSchedulerManager(SchedulerManager schedulerManager) {
        this.schedulerManager = schedulerManager;
    }
    @FXML
    public void onMenuItemPendencia9900OracleTabelasHiveGeralAction() {
        String oracleFilePath = "X:\\Dados\\SPED\\ORACLE_SPED_9900.csv";
        String hiveFilePath = "X:\\Dados\\SPED\\HIVE_SPED_9900.csv";
        String outputFilePath = "X:\\Dados\\SPED\\Pendencia_Processamento_0000.csv";

        // Inicializa a instância de OracleSpedDatabaseOperations
        oracleSpedDatabaseOperations = new OracleSpedDatabaseOperations();

        // Chama o método após a inicialização
        oracleSpedDatabaseOperations.executeQueryAndSaveToCSV();

        // Cria uma instância de HiveSpedController, que inicializa automaticamente as variáveis de ambiente
        HiveSpedController hiveSpedController = new HiveSpedController();

        hiveSpedController.executeQueryAndSaveToCSV();

        CSVUtils.FileComparisonService.compareAndSaveDifferences(oracleFilePath, hiveFilePath, outputFilePath);

        // Obtém os valores das variáveis de ambiente para conexão com o Hive
        String jdbcURL = System.getenv("HIVE_JDBC_URL");
        String username = System.getenv("HIVE_USERNAME");
        String password = System.getenv("HIVE_PASSWORD");

        // Cria uma instância de HiveSpedDatabaseOperations com os parâmetros de conexão
        HiveSpedDatabaseOperations hiveSpedDB = new HiveSpedDatabaseOperations(jdbcURL, username, password);

        // Obtém a data atual e subtrai 60 dias
        LocalDateTime dataAtual = LocalDateTime.now().minusDays(60);

        // Define o nome do arquivo CSV
        String nomeArquivo = "X:\\Dados\\SPED\\divergencia9900TabelasSped.csv";

        // Executa a consulta e grava no arquivo CSV
        hiveSpedDB.divergencia9900TabelasSpedAndWriteToCSV(dataAtual, nomeArquivo);

        // Define os caminhos para o arquivo de entrada e para o arquivo de saída
        String inputFilePath = "X:\\Dados\\SPED\\ORACLE_SPED_9900.csv";
        String outputFilePath9900OracleTabelasHive = "X:\\Dados\\SPED\\ResultadoQuery.csv";

        try {
            // Executa a consulta com join e escreve os resultados no arquivo CSV de saída
            hiveSpedDB.executeQueryWithJoinAndWriteResults(inputFilePath, outputFilePath9900OracleTabelasHive);
            System.out.println("Operação concluída com sucesso.");
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        } finally {
            // Certifique-se de desconectar após o uso
            hiveSpedDB.disconnect();
        }

        loadView("/Fxml/Sped9900.fxml", x -> {});
    }
}