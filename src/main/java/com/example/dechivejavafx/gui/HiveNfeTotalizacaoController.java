package com.example.dechivejavafx.gui;

import com.example.dechivejavafx.db.DatabaseConfig;
import com.example.dechivejavafx.db.HiveDecDatabaseOperations;
import com.example.dechivejavafx.model.entities.TotalizacaoNfe;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.function.Function;

public class HiveNfeTotalizacaoController {

    // Labels para exibição de resultados individuais
    @FXML private Label labelTotalNFe;
    @FXML private Label labelUF;
    @FXML private Label labelArquivo;
    @FXML private Label labelVNF;
    @FXML private Label labelVServ;
    @FXML private Label labelVISS;
    @FXML private Label labelVBC;
    @FXML private Label labelVICMS;
    @FXML private Label labelVST;
    @FXML private Label labelVFCPST;
    @FXML private Label labelVFCPSTRet;
    @FXML private Label labelVProd;
    @FXML private Label labelVFCPUFDest;

    // Tabela para exibição de resultados em formato tabular
    @FXML private TableView<TotalizacaoNfe> tableViewNfeTotalizacao;

    // Colunas da tabela para diferentes propriedades da entidade TotalizacaoNfe
    @FXML private TableColumn<TotalizacaoNfe, Integer> columnTotalNFe;
    @FXML private TableColumn<TotalizacaoNfe, String> columnUF;
    @FXML private TableColumn<TotalizacaoNfe, String> columnArquivo;
    @FXML private TableColumn<TotalizacaoNfe, Double> columnVNF;
    @FXML private TableColumn<TotalizacaoNfe, Double> columnVServ;
    @FXML private TableColumn<TotalizacaoNfe, Double> columnVISS;
    @FXML private TableColumn<TotalizacaoNfe, Double> columnVBC;
    @FXML private TableColumn<TotalizacaoNfe, Double> columnVICMS;
    @FXML private TableColumn<TotalizacaoNfe, Double> columnVST;
    @FXML private TableColumn<TotalizacaoNfe, Double> columnVFCPST;
    @FXML private TableColumn<TotalizacaoNfe, Double> columnVFCPSTRet;
    @FXML private TableColumn<TotalizacaoNfe, Double> columnVProd;
    @FXML private TableColumn<TotalizacaoNfe, Double> columnVFCPUFDest;

    // Operações no banco de dados Hive
    private HiveDecDatabaseOperations hiveDatabaseOperations;

    // Configurações do banco de dados
    private DatabaseConfig databaseConfig = new DatabaseConfig();

    // Método de inicialização chamado pelo JavaFX quando o arquivo FXML é carregado
    @FXML
    public void initialize() {
        // Configurar as colunas da tabela
        configureColumn(columnTotalNFe, TotalizacaoNfe::getTotalNFe);
        configureColumn(columnUF, TotalizacaoNfe::getUf);
        configureColumn(columnArquivo, TotalizacaoNfe::getArquivo);
        configureDoubleColumn(columnVNF, TotalizacaoNfe::getVnf);
        configureDoubleColumn(columnVServ, TotalizacaoNfe::getVserv);
        configureDoubleColumn(columnVISS, TotalizacaoNfe::getViss);
        configureDoubleColumn(columnVBC, TotalizacaoNfe::getVbc);
        configureDoubleColumn(columnVICMS, TotalizacaoNfe::getVicms);
        configureDoubleColumn(columnVST, TotalizacaoNfe::getVst);
        configureDoubleColumn(columnVFCPST, TotalizacaoNfe::getVfcpst);
        configureDoubleColumn(columnVFCPSTRet, TotalizacaoNfe::getVfcpstret);
        configureDoubleColumn(columnVProd, TotalizacaoNfe::getVprod);
        configureDoubleColumn(columnVFCPUFDest, TotalizacaoNfe::getVfcpufdest);

        // Configurar a instância HiveDatabaseOperations
        hiveDatabaseOperations = createHiveDatabaseOperations();

        // Executar a consulta e atualizar a interface gráfica
        executeQueryAndUpdateUI();
    }

    // Método para configurar coluna de números decimais na tabela
    private void configureDoubleColumn(TableColumn<TotalizacaoNfe, Double> column, Function<TotalizacaoNfe, Double> valueExtractor) {
        column.setCellValueFactory(cellData -> new SimpleDoubleProperty(valueExtractor.apply(cellData.getValue())).asObject());
        column.setCellFactory(createDoubleCellFactory());
    }

    // Método para configurar coluna de texto na tabela
    private <T> void configureColumn(TableColumn<TotalizacaoNfe, T> column, Function<TotalizacaoNfe, T> valueExtractor) {
        column.setCellValueFactory(cellData -> new SimpleObjectProperty<>(valueExtractor.apply(cellData.getValue())));
    }

    // Método para criar uma fábrica de células para colunas de números decimais
    private Callback<TableColumn<TotalizacaoNfe, Double>, TableCell<TotalizacaoNfe, Double>> createDoubleCellFactory() {
        return new Callback<TableColumn<TotalizacaoNfe, Double>, TableCell<TotalizacaoNfe, Double>>() {
            @Override
            public TableCell<TotalizacaoNfe, Double> call(TableColumn<TotalizacaoNfe, Double> param) {
                return new TableCell<TotalizacaoNfe, Double>() {
                    @Override
                    protected void updateItem(Double item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setText(null);
                        } else {
                            setText(formatDouble(item));
                        }
                    }
                };
            }
        };
    }

    // Método para formatar números decimais
    private String formatDouble(double value) {
        NumberFormat formatter = new DecimalFormat("#0.00");
        return formatter.format(value);
    }

    // Método para criar uma instância de HiveDatabaseOperations
    private HiveDecDatabaseOperations createHiveDatabaseOperations() {
        String jdbcUrl = System.getenv("HIVE_JDBC_URL");
        String username = System.getenv("HIVE_USERNAME");
        String password = System.getenv("HIVE_PASSWORD");

        // Verificar se as variáveis de ambiente estão configuradas corretamente
        if (jdbcUrl == null || username == null || password == null) {
            System.err.println("Erro: As variáveis de ambiente não estão configuradas corretamente.");
            return null;
        }

        return new HiveDecDatabaseOperations(jdbcUrl, username, password);
    }

    // Método para executar a consulta e atualizar a interface gráfica
    private void executeQueryAndUpdateUI() {
        if (hiveDatabaseOperations != null) {
            hiveDatabaseOperations.executeQueryAndPopulateResults();

            // Obter os resultados da consulta
            List<TotalizacaoNfe> resultList = hiveDatabaseOperations.getQueryResults();

            // Converter a lista em ObservableList
            ObservableList<TotalizacaoNfe> observableResultList = FXCollections.observableArrayList(resultList);

            // Preencher a tabela com os resultados
            tableViewNfeTotalizacao.setItems(observableResultList);
        }
    }
}
