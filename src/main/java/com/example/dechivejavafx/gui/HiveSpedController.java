package com.example.dechivejavafx.gui;

import com.example.dechivejavafx.db.HiveSpedDatabaseOperations;
import com.example.dechivejavafx.gui.util.CSVUtils;
import com.example.dechivejavafx.model.entities.Sped9900;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

public class HiveSpedController {
    private static HiveSpedDatabaseOperations databaseOperations;
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
    @FXML
    private ComboBox<String> comboTipoDoc;
    @FXML
    private TableView<Sped9900> tableViewOPendenciaHive;

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

        // Carregar os dados do arquivo CSV na TableView
        loadCSVData();
    }

    private void loadCSVData() {
        List<Sped9900> dataList = CSVUtils.loadSped9900FromCsv("X:\\Dados\\SPED\\Pendencia_Processamento_0000.csv");
        ObservableList<Sped9900> observableDataList = FXCollections.observableArrayList(dataList);
        tableViewOPendenciaHive.setItems(observableDataList);
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

    public HiveSpedController() {
        this.jdbcUrl = System.getenv("HIVE_JDBC_URL");
        this.user = System.getenv("HIVE_USERNAME");
        this.password = System.getenv("HIVE_PASSWORD");
        this.databaseOperations = new HiveSpedDatabaseOperations(jdbcUrl, user, password);

        // Define startDate e filePath
        this.startDate = LocalDateTime.now().minusDays(15);
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
}
