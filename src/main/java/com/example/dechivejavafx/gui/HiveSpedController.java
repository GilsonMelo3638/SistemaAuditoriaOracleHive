package com.example.dechivejavafx.gui;

import com.example.dechivejavafx.Validacoes.TabelasSped;
import com.example.dechivejavafx.db.HiveSpedDatabaseOperations;
import com.example.dechivejavafx.gui.util.CSVUtils;
import com.example.dechivejavafx.gui.util.Configuracao;
import com.example.dechivejavafx.model.entities.Hive9900TabelasHive;
import com.example.dechivejavafx.model.entities.Hive9900TabelasHiveFaltantes;
import com.example.dechivejavafx.model.entities.Sped9900;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class HiveSpedController {
    private static HiveSpedDatabaseOperations databaseOperations;
    public Button btLimpar;
    @FXML public Label txtTotalIdBase;
    @FXML public Label txtTotalIdBaseFaltante;
    @FXML public Label txtTotalIdBaseFaltante0000;
    @FXML public Label txtTotalLinhasFaltante;
    @FXML private Label tituloTableViews;

    @FXML private TableView<Sped9900> tableView9900Orapr12Hive0000;
    @FXML private TableView<Hive9900TabelasHive> tableViewHive9900TodasTabelasHive;
    @FXML private TableView <Hive9900TabelasHiveFaltantes> tableViewHive9900TodasTabelasHiveFaltantes;

    @FXML private TableColumn <Hive9900TabelasHiveFaltantes, BigInteger> columnTabelaidBase9900HiveFaltantes;
    @FXML private TableColumn<Hive9900TabelasHiveFaltantes, LocalDateTime> columnTabelasProcessamentoFin9900Hive;
    @FXML private TableColumn<Hive9900TabelasHiveFaltantes, Integer> columnTabelaStatus9900HiveFaltantes;
    @FXML private TableColumn <Hive9900TabelasHiveFaltantes, String> columnTabelaregistroBloco9900HiveFaltantes;
    @FXML private TableColumn <Hive9900TabelasHiveFaltantes, Integer> columnTabela9900QuantidadeLinhasHiveFaltantes;

    @FXML private TableColumn<Hive9900TabelasHive, BigInteger>columnTabelaidBase9900Hive;
    @FXML private TableColumn<Hive9900TabelasHive, LocalDateTime> columnTabeladhProcessamentoSpedBase;
    @FXML private TableColumn<Hive9900TabelasHive, Integer> columnTabelastatusProcessamentoSpedBase;
    @FXML private TableColumn<Hive9900TabelasHive, LocalDateTime> columnTabelasdataHoraFin9900Hive;
    @FXML private TableColumn<Hive9900TabelasHive, String>columnTabelaregistro9900Hive;
    @FXML private TableColumn<Hive9900TabelasHive, String> columnTabelaregistroBloco9900Hive;
    private String jdbcUrl;
    private String user;
    private String password;
    private LocalDateTime startDate;
    private String filePath;
    private final StringProperty idBase = new SimpleStringProperty();
    @FXML private TableColumn<Sped9900, BigInteger> columnTabelaidBase;
    @FXML private TableColumn<Sped9900, LocalDateTime> columnTabeladhProcessamento;
    @FXML private TableColumn<Sped9900, Integer> columnTabelastatusProcessamento;
    @FXML private TableColumn<Sped9900, Integer> columnTabelalinha;
    @FXML private TableColumn<Sped9900, Integer> columnTabelaquantidadeRegBloco;
    @FXML private TableColumn<Sped9900, String> columnTabelaregistro;
    @FXML private TableColumn<Sped9900, String> columnTabelaregistroBloco;

    @FXML private ComboBox<TabelasSped> comboTabelasSped;

    private ObservableList<Hive9900TabelasHive> originalDataList = FXCollections.observableArrayList();
    private ObservableList<Hive9900TabelasHiveFaltantes> originalDataListFaltantes = FXCollections.observableArrayList();


    @FXML
    public void initialize() {
        // Configurar as colunas da tabela
        configureColumn(columnTabelaidBase, Sped9900::getIdBase);
        configureColumn(columnTabeladhProcessamento, Sped9900::getDhProcessamento);
        configureColumn(columnTabelastatusProcessamento, Sped9900::getStatusProcessamento);
        configureColumn(columnTabelalinha, Sped9900::getLinha);
        configureColumn(columnTabelaquantidadeRegBloco, Sped9900::getQuantidadeRegBloco);
        configureColumn(columnTabelaregistro, Sped9900::getRegistro);
        configureColumn(columnTabelaregistroBloco, Sped9900::getRegistroBloco);

        configureColumnHive9900TabelasHive(columnTabelaidBase9900Hive, Hive9900TabelasHive::getIdBase);
        configureColumnHive9900TabelasHive(columnTabeladhProcessamentoSpedBase, Hive9900TabelasHive::getDhProcessamento);
        configureColumnHive9900TabelasHive(columnTabelastatusProcessamentoSpedBase, Hive9900TabelasHive::getStatusProcessamento);
        configureColumnHive9900TabelasHive(columnTabelasdataHoraFin9900Hive, Hive9900TabelasHive::getDataHoraFin);
        configureColumnHive9900TabelasHive(columnTabelaregistro9900Hive, Hive9900TabelasHive::getRegistro);
        configureColumnHive9900TabelasHive(columnTabelaregistroBloco9900Hive, Hive9900TabelasHive::getRegistroBloco);

        configureColumnHive9900TabelasHiveFaltantes(columnTabelaidBase9900HiveFaltantes, Hive9900TabelasHiveFaltantes::getIdBase);
        configureColumnHive9900TabelasHiveFaltantes(columnTabelasProcessamentoFin9900Hive, Hive9900TabelasHiveFaltantes::getDhProcessamento);
        configureColumnHive9900TabelasHiveFaltantes(columnTabelaStatus9900HiveFaltantes, Hive9900TabelasHiveFaltantes::getStatusProcessamento);
        configureColumnHive9900TabelasHiveFaltantes(columnTabelaregistroBloco9900HiveFaltantes, Hive9900TabelasHiveFaltantes::getRegistroBloco);
        configureColumnHive9900TabelasHiveFaltantes(columnTabela9900QuantidadeLinhasHiveFaltantes, Hive9900TabelasHiveFaltantes::getQuantidadeRegBloco);

        // Inicializar o ComboBox com os valores da enumeração TabelasSped (sem o sublinhado)
        ObservableList<TabelasSped> tabelasSpedList = FXCollections.observableArrayList(TabelasSped.values());
        comboTabelasSped.setItems(tabelasSpedList);

        // Configurar o formato de exibição dos itens do ComboBox
        comboTabelasSped.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(TabelasSped item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFormattedName());
            }
        });

        comboTabelasSped.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                System.out.println("Item selecionado no ComboBox: " + newValue.getFormattedName());
                filterTable(newValue); // Filtrar as tabelas com base no valor selecionado no ComboBox
                updateRowCounts(); // Atualizar a contagem de linhas quando o item do ComboBox mudar
            }
        });

        // Carregar os dados do arquivo CSV na TableView
        loadCSVData();
        loadCSVDataHive9900TabelasHive();
        loadCSVDataHive9900TabelasHiveFaltante();

        // Atualizar a contagem inicial de linhas
        updateRowCounts();

        // Adicionar ouvintes aos campos txtTotalIdBase e txtTotalIdBaseFaltante
        tableViewHive9900TodasTabelasHive.itemsProperty().addListener((observable, oldValue, newValue) -> updateRowCounts());
        tableViewHive9900TodasTabelasHiveFaltantes.itemsProperty().addListener((observable, oldValue, newValue) -> updateRowCounts());
        tableView9900Orapr12Hive0000.itemsProperty().addListener((observable, oldValue, newValue) -> updateRowCounts());

        // Configurar a ordenação inicial da tableView9900Orapr12Hive0000
        tableView9900Orapr12Hive0000.getSortOrder().addAll(columnTabeladhProcessamento, columnTabelaidBase);

        // Configurar a ordenação inicial da tableView9900Orapr12Hive0000
        tableViewHive9900TodasTabelasHive.getSortOrder().addAll(columnTabeladhProcessamentoSpedBase, columnTabelaidBase9900Hive);

        // Configurar a ordenação inicial da tableView9900Orapr12Hive0000
        tableViewHive9900TodasTabelasHiveFaltantes.getSortOrder().addAll(columnTabelasProcessamentoFin9900Hive, columnTabelaidBase9900HiveFaltantes);
    }

    private void filterTable(TabelasSped selectedValue) {
        // Obter o valor formatado selecionado no ComboBox
        String selectedFormattedName = selectedValue.getFormattedName();

        // Criar uma nova lista filtrada com base no valor formatado selecionado no ComboBox
        ObservableList<Hive9900TabelasHive> filteredDataList = FXCollections.observableArrayList();
        ObservableList<Hive9900TabelasHiveFaltantes> filteredDataListFaltantes = FXCollections.observableArrayList();

        for (Hive9900TabelasHive item : originalDataList) {
            // Comparar o valor do registroBloco com o valor formatado selecionado
            if (item.getRegistroBloco().equals(selectedFormattedName)) {
                filteredDataList.add(item);
            }
        }

        for (Hive9900TabelasHiveFaltantes item : originalDataListFaltantes) {
            // Comparar o valor do registroBloco com o valor formatado selecionado
            if (item.getRegistroBloco().equals(selectedFormattedName)) {
                filteredDataListFaltantes.add(item);
            }
        }

        // Atualizar as TableViews com os dados filtrados
        tableViewHive9900TodasTabelasHive.setItems(filteredDataList);
        tableViewHive9900TodasTabelasHiveFaltantes.setItems(filteredDataListFaltantes);
    }

    @FXML
    private void onBtLimparAction() {
        btLimpar.setOnAction(event -> clearFilter());
    }

    // Método para limpar o filtro e restaurar a lista original
    private void clearFilter() {
        tableViewHive9900TodasTabelasHive.setItems(originalDataList);
        tableViewHive9900TodasTabelasHiveFaltantes.setItems(originalDataListFaltantes);
    }

    private void loadCSVData() {
        List<Sped9900> dataList = CSVUtils.loadSped9900FromCsv("X:\\Dados\\SPED\\Pendencia_Processamento_0000.csv");
        ObservableList<Sped9900> observableDataList = FXCollections.observableArrayList(dataList);
        tableView9900Orapr12Hive0000.setItems(observableDataList);
    }

    private void loadCSVDataHive9900TabelasHive() {
        List<Hive9900TabelasHive> dataList = CSVUtils.loadHive9900TabelasHiveFromCsv("X:\\Dados\\SPED\\divergencia9900TabelasSped.csv");
        originalDataList.addAll(dataList);
        ObservableList<Hive9900TabelasHive> observableDataList = FXCollections.observableArrayList(dataList);
        tableViewHive9900TodasTabelasHive.setItems(observableDataList);
    }

    private void loadCSVDataHive9900TabelasHiveFaltante() {
        List<Hive9900TabelasHiveFaltantes> dataList = CSVUtils.loadHive9900TabelasHiveFromCsvFaltantes("X:\\Dados\\SPED\\ResultadoQuery.csv");
        originalDataListFaltantes.addAll(dataList);
        ObservableList<Hive9900TabelasHiveFaltantes> observableDataList = FXCollections.observableArrayList(dataList);
        tableViewHive9900TodasTabelasHiveFaltantes.setItems(observableDataList);
    }


    // Método para configurar coluna de texto na tabela
    private <T> void configureColumn(TableColumn<Sped9900, T> column, Function<Sped9900, T> valueExtractor) {
        column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(valueExtractor.apply(cellData.getValue())));

        // Configurar comparadores para ordenação múltipla
        column.setComparator((o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return -1;
            } else if (o2 == null) {
                return 1;
            }
            return String.valueOf(o1).compareToIgnoreCase(String.valueOf(o2));
        });

        // Configurar a ordenação inicial
        column.setSortType(TableColumn.SortType.ASCENDING);
    }

    // Método para configurar coluna de texto na tabela
    private <T> void configureColumnHive9900TabelasHive(TableColumn<Hive9900TabelasHive, T> column, Function<Hive9900TabelasHive, T> valueExtractor) {
        column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(valueExtractor.apply(cellData.getValue())));

        // Configurar comparadores para ordenação múltipla
        column.setComparator((o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return -1;
            } else if (o2 == null) {
                return 1;
            }
            return String.valueOf(o1).compareToIgnoreCase(String.valueOf(o2));
        });

        // Configurar a ordenação inicial
        column.setSortType(TableColumn.SortType.ASCENDING);
    }

    // Método para configurar coluna de texto na tabela
    private <T> void configureColumnHive9900TabelasHiveFaltantes(TableColumn<Hive9900TabelasHiveFaltantes, T> column, Function<Hive9900TabelasHiveFaltantes, T> valueExtractor) {
        column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(valueExtractor.apply(cellData.getValue())));

        // Configurar comparadores para ordenação múltipla
        column.setComparator((o1, o2) -> {
            if (o1 == null && o2 == null) {
                return 0;
            } else if (o1 == null) {
                return -1;
            } else if (o2 == null) {
                return 1;
            }
            return String.valueOf(o1).compareToIgnoreCase(String.valueOf(o2));
        });

        // Configurar a ordenação inicial
        column.setSortType(TableColumn.SortType.ASCENDING);
    }

    public HiveSpedController() {
        this.jdbcUrl = System.getenv("HIVE_JDBC_URL");
        this.user = System.getenv("HIVE_USERNAME");
        this.password = System.getenv("HIVE_PASSWORD");
        this.databaseOperations = new HiveSpedDatabaseOperations(jdbcUrl, user, password);

        // Define startDate e filePath
        this.startDate = LocalDateTime.now().minusDays(Configuracao.diasSpedHive);
        this.filePath = "X:\\Dados\\SPED\\HIVE_SPED_9900.csv";
    }

    public void executeQueryAndSaveToCSV() {
        try {
            // Verifique se databaseOperations não é nulo
            if (databaseOperations != null) {
                // Executa a consulta utilizando a classe HiveSpedDatabaseOperations
                ResultSet resultSet = databaseOperations.executeSpedReg9900Query(startDate);

                // Chama o método da classe CSVUtils para salvar no CSV
                CSVUtils.saveSpedHiveToCsv(resultSet, filePath);

                // Fecha os recursos
                resultSet.close();
            } else {
                System.err.println("Erro: databaseOperations não foi inicializado.");
            }
        } catch (SQLException e) {
            System.err.println("Erro ao executar a consulta: " + e.getMessage());
        }
    }

    public void disconnect() {
        if (databaseOperations != null) {
            databaseOperations.disconnect();
        }
    }

    private void updateRowCounts() {
        // Contar e associar a quantidade de linhas à label txtTotalIdBase
        int totalLinhasHive = tableViewHive9900TodasTabelasHive.getItems().size();
        txtTotalIdBase.setText(formatarNumero(totalLinhasHive));

        // Contar e associar a quantidade de linhas à label txtTotalIdBaseFaltante
        int totalLinhasHiveFaltantes = tableViewHive9900TodasTabelasHiveFaltantes.getItems().size();
        txtTotalIdBaseFaltante.setText(formatarNumero(totalLinhasHiveFaltantes));

        // Contar e associar a quantidade de linhas à label txtTotalIdBaseFaltante
        int totalIdBaseFaltante0000 = tableView9900Orapr12Hive0000.getItems().size();
        txtTotalIdBaseFaltante0000.setText(formatarNumero(totalIdBaseFaltante0000));

        // Calcular a soma da coluna columnTabela9900QuantidadeLinhasHiveFaltantes
        int totalLinhasFaltantes = 0;
        for (Hive9900TabelasHiveFaltantes item : tableViewHive9900TodasTabelasHiveFaltantes.getItems()) {
            totalLinhasFaltantes += item.getQuantidadeRegBloco();
        }

        // Associar a quantidade total de linhas faltantes à label txtTotalLinhasFaltante
        txtTotalLinhasFaltante.setText(formatarNumero(totalLinhasFaltantes));
    }

    private String formatarNumero(int numero) {
        // Criar um formato com separador de milhares como ponto
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.getDefault());
        symbols.setGroupingSeparator('.');
        DecimalFormat df = new DecimalFormat("#,###", symbols);
        return df.format(numero);
    }

}
