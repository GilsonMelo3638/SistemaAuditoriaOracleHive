package com.example.dechivejavafx.gui;

import com.example.dechivejavafx.Validacoes.TipoDoc;
import com.example.dechivejavafx.gui.util.CSVUtils;
import com.example.dechivejavafx.gui.util.ComboBoxUtil;
import com.example.dechivejavafx.gui.util.TableViewManager;
import com.example.dechivejavafx.gui.util.TabelaDataManager;
import com.example.dechivejavafx.model.entities.OracleHive;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Controlador para a interface gráfica que exibe a divergência de documentos no Oracle e no ambiente Hive.
 */
public class OracleHiveController implements Initializable {

    private TabelaDataManager tabelaDataManager = new TabelaDataManager();
    /**
     * Controlador para a interface gráfica que exibe dados relacionados a Oracle e Hive.
     *
     * Este controlador gerencia a interface gráfica associada a operações relacionadas a Oracle e Hive,
     * como a exibição e filtragem de resultados, interação com ComboBoxes e a inicialização da interface.
     */
    @FXML
    private Label labelArquivo;
    @FXML
    private Label labelTipoDoc;
    @FXML
    private Label labelTabelaOracle;
    @FXML
    private Label labelTabelaHive;
    @FXML
    private Label labelTotalOracle;
    @FXML
    private Label labelTotalHive;
    @FXML
    private Label txtDiferenca;
    @FXML private Label tituloTableViews;
    @FXML
    private TableColumn<OracleHive, String> columnArquivo;
    @FXML
    private TableColumn<OracleHive, String> columnTipoDoc;
    @FXML
    private TableColumn<OracleHive, String> columnTabelaOracle;
    @FXML
    private TableColumn<OracleHive, String> columnTabelaHive;
    @FXML
    private TableColumn<OracleHive, Integer> columnTotalOracle;
    @FXML
    private TableColumn<OracleHive, Integer> columnTotalHive;
    @FXML
    private TableColumn<OracleHive, Integer> columnDiferenca;
    @FXML
    private TableView<OracleHive> tableViewOracleHive;
    @FXML
    private ComboBox<TipoDoc> comboTipoDoc;
    private static OracleHiveController instance;
    // Caminho do arquivo CSV do resultado final de diferenças de quantidades de documentos no Oracle e Hive
    private static final String CSV_OUTPUT_PATH = "\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia/pendencias.csv";
    // Adicione o caminho do arquivo CSV do resultado de diferenças de quantidades de documentos no Oracle e Hive.
    private static final String CSV_FILE_PATH = "\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\Resultado.csv";

    // Construtor padrão.
    public OracleHiveController() {
        instance = this;
    }

    // No método initialize
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initializeComboBoxes();
        loadCsvData();
        columnArquivo.setCellValueFactory(new PropertyValueFactory<OracleHive, String>("arquivo"));
        columnTipoDoc.setCellValueFactory(new PropertyValueFactory<OracleHive, String>("tipoDoc"));
        columnTabelaOracle.setCellValueFactory(new PropertyValueFactory<OracleHive, String>("tabelaOracle"));
        columnTabelaHive.setCellValueFactory(new PropertyValueFactory<OracleHive, String>("tabelaHive"));
        columnTotalOracle.setCellValueFactory(new PropertyValueFactory<OracleHive, Integer>("totalOracle"));
        columnTotalHive.setCellValueFactory(new PropertyValueFactory<OracleHive, Integer>("totalHive"));
        columnDiferenca.setCellValueFactory(new PropertyValueFactory<OracleHive, Integer>("diferenca"));

        // Mantenha uma cópia original dos resultados
        List<OracleHive> resultadosOriginais = new ArrayList<>(tableViewOracleHive.getItems());

        // Adicione um listener ao comboBoxTabelas para filtrar a TableView com base na seleção do ComboBox
        comboTipoDoc.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                filterTableView(resultadosOriginais, newValue);
            }
        });}

    // Método para inicializar os ComboBoxes
    private void initializeComboBoxes() {
        ComboBoxUtil.initializeComboBoxTipoDoc(comboTipoDoc, Arrays.asList(TipoDoc.values()));
    }
    /**
     * Carrega dados do arquivo CSV e atualiza a tabela.
     *
     * Este método é responsável por ler as linhas de um arquivo CSV, pular o cabeçalho
     * (se existir), e transformar essas linhas em objetos OracleHive. Posteriormente,
     * esses objetos são utilizados para popular a tabela na interface gráfica.
     */
    private void loadCsvData() {
        try {
            // Obtém o caminho do arquivo CSV
            Path filePath = Paths.get(CSV_FILE_PATH);
            // Verifica se o arquivo existe
            if (!Files.exists(filePath)) {
                // Se o arquivo não existir, encerra o método
                return;
            }
            // Lê todas as linhas do arquivo CSV
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            // Verifica se o arquivo CSV está vazio
            if (lines.isEmpty()) {
                // Se estiver vazio, encerra o método
                return;
            }
           // Crie uma instância de TabelaDataManager, se necessário
            CSVUtils csvUtils = new CSVUtils();

            // Pula o cabeçalho (se existir) e mapeia as linhas para objetos OracleHive
            List<OracleHive> resultados = lines.stream().skip(1).map(csvUtils::parseCsvLine).collect(Collectors.toList());

            // Chama o método para popular a tabela com os resultados obtidos (da classe ManagerTable)
            TableViewManager managerTable = new TableViewManager();
            managerTable.popularTabelaOracleHive(tableViewOracleHive, resultados);
        } catch (IOException e) {
            // Imprime a stack trace para depuração
            e.printStackTrace();
            // Registra a exceção no log
            Logger.getLogger(OracleHiveController.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Filtra a TableView com base na seleção do ComboBox.
     *
     * Este método recebe a lista original de resultados e o TipoDoc selecionado no ComboBox.
     * Ele cria um Predicate para filtrar os resultados com base no TipoDoc selecionado e
     * utiliza uma FilteredList para configurar a TableView exibindo apenas os resultados filtrados.
     *
     * @param resultadosOriginais A lista original de resultados a serem filtrados.
     * @param selectedTipoDoc O TipoDoc selecionado no ComboBox para filtragem.
     */
    private void filterTableView(List<OracleHive> resultadosOriginais, TipoDoc selectedTipoDoc) {
        // Cria um Predicate para filtrar os resultados com base no TipoDoc selecionado
        Predicate<OracleHive> filterPredicate = oracleHive ->
                oracleHive.getTabelaOracle().equals(selectedTipoDoc.toString());
        // Cria uma FilteredList usando o Predicate
        FilteredList<OracleHive> filteredList = new FilteredList<>(FXCollections.observableArrayList(resultadosOriginais), filterPredicate);
        // Configura a TableView para usar a FilteredList
        tableViewOracleHive.setItems(filteredList);
    }
    // Método getComboBoxTabelas
    public ComboBox<TipoDoc> getComboBoxTabelas() {
        return comboTipoDoc;
    }
    // Obtém a instância única do controlador.
    public static OracleHiveController getInstance() {
        return instance;
    }
}