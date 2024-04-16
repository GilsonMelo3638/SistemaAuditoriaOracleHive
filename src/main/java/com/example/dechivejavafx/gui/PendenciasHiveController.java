package com.example.dechivejavafx.gui;

import com.example.dechivejavafx.Validacoes.TipoDoc;
import com.example.dechivejavafx.db.DatabaseConfig;
import com.example.dechivejavafx.db.HiveDecDatabaseOperations;
import com.example.dechivejavafx.gui.util.ComboBoxUtil;
import com.example.dechivejavafx.model.entities.DuplicidadeId;
import com.example.dechivejavafx.model.entities.OracleHive;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PendenciasHiveController {

    public StackPane pendenciasHivePane;
    public Button btLimpar;
    @FXML private  Label tituloTableViews;
    // Labels para exibição de resultados individuais
    @FXML private Label labelArquivo;
    @FXML private Label labelcolumnTabelaDetalhe;
    @FXML private Label labelQuantidadeNsuchave;
    @FXML private ComboBox<TipoDoc> comboTipoDoc;
    // Tabela para exibição de resultados em formato tabular
    @FXML private TableView<com.example.dechivejavafx.model.entities.PendenciasHive> tableViewDetNFeNFCeInf;
    @FXML private TableView<DuplicidadeId> tableDuplicidadeId;
    @FXML private TableView<OracleHive> tableViewOracleHive;
    // Colunas da tabela para diferentes propriedades da entidade TotalizacaoNfe
    @FXML private TableColumn<com.example.dechivejavafx.model.entities.PendenciasHive, String> columnArquivo;
    @FXML private TableColumn<com.example.dechivejavafx.model.entities.PendenciasHive, String> columnTabelaDetalhe;
    @FXML private TableColumn<com.example.dechivejavafx.model.entities.PendenciasHive, Integer> columnQuantidadeNsuchave;
    @FXML private TableColumn<DuplicidadeId, String> columnArquivoDuplicidade;
    @FXML private TableColumn<DuplicidadeId, String> columnTabelaDuplicidade;
    @FXML private TableColumn<OracleHive, String> columnArquivoOracle;
    @FXML private TableColumn<OracleHive, String> columnTipoDoc;
    @FXML private TableColumn<OracleHive, String> columnTabelaOracle;
    @FXML private TableColumn<OracleHive, String> columnTabelaHive;
    @FXML private TableColumn<OracleHive, Integer> columnTotalOracle;
    @FXML private TableColumn<OracleHive, Integer> columnTotalHive;
    @FXML private TableColumn<OracleHive, Integer> columnDiferenca;
    // Operações no banco de dados Hive
    private HiveDecDatabaseOperations hiveDatabaseOperations;
    private ObservableList<OracleHive> originalOracleHiveList = FXCollections.observableArrayList(); // Lista para armazenar os resultados originais
    // Configurações do banco de dados
    private DatabaseConfig databaseConfig = new DatabaseConfig();
    // Método de inicialização chamado pelo JavaFX quando o arquivo FXML é carregado
    @FXML
    public void initialize() {
        initializeComboBoxes();
        // Configurar as colunas da tabela
        configureColumn(columnArquivo, com.example.dechivejavafx.model.entities.PendenciasHive::getArquivo);
        configureColumn(columnTabelaDetalhe, com.example.dechivejavafx.model.entities.PendenciasHive::getTabelaDetalhe);
        configureColumn(columnQuantidadeNsuchave, com.example.dechivejavafx.model.entities.PendenciasHive::getQuantidadeNsuchave);
        configureColumnDuplicidade(columnArquivoDuplicidade, DuplicidadeId::getArquivo);
        configureColumnDuplicidade(columnTabelaDuplicidade, DuplicidadeId::getTabela);
        configureColumOracleHive(columnArquivoOracle, OracleHive::getArquivo);
        configureColumOracleHive(columnTipoDoc, OracleHive::getTipoDoc);
        configureColumOracleHive(columnTabelaOracle, OracleHive::getTabelaOracle);
        configureColumOracleHive(columnTabelaHive, OracleHive::getTabelaHive);
        configureColumOracleHive(columnTotalOracle, OracleHive::getTotalOracle);
        configureColumOracleHive(columnTotalHive, OracleHive::getTotalHive);
        configureColumOracleHive(columnDiferenca, OracleHive::getDiferenca);

        // Configurar a instância HiveDatabaseOperations
        hiveDatabaseOperations = createHiveDatabaseOperations();
        // Carregar dados dos arquivos CSV para a tabela de duplicidade
        loadDuplicidadeIdData("DuplicidadeIdDetalhe.csv");
        loadDuplicidadeIdData("DuplicidadeIdPrincipal.csv");
        loadOracleHiveData("pendencias.csv"); // Adicione esta linha para carregar dados para tableViewOracleHive
        // Executar a consulta e atualizar a interface gráfica
        // Adicionar um ouvinte de alteração ao ComboBox comboTipoDoc
        comboTipoDoc.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Filtrar a tabela OracleHive com base no novo valor selecionado
            filterOracleHiveTable(newValue.toString());
        });
        // Copie a lista original ao iniciar
        originalOracleHiveList.addAll(tableViewOracleHive.getItems());
        executeQueryAndUpdateUI();

    }

    // Método para inicializar os ComboBoxes
    private void initializeComboBoxes() {
        ComboBoxUtil.initializeComboBoxTipoDoc(comboTipoDoc, Arrays.asList(TipoDoc.values()));
    }


    // Método para configurar coluna de texto na tabela
    private <T> void configureColumn(TableColumn<com.example.dechivejavafx.model.entities.PendenciasHive, T> column, Function<com.example.dechivejavafx.model.entities.PendenciasHive, T> valueExtractor) {
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

    private <T> void configureColumnDuplicidade(TableColumn<DuplicidadeId, T> column, Function<DuplicidadeId, T> valueExtractor) {
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

    private <T> void configureColumOracleHive(TableColumn<OracleHive, T> column, Function<OracleHive, T> valueExtractor) {
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

    // Método para carregar dados de um arquivo CSV para a tabela de duplicidade
    private void loadDuplicidadeIdData(String fileName) {
        List<DuplicidadeId> duplicidadeIdList = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader("\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\" + fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    String arquivo = parts[0];
                    String tabela = parts[1];
                    duplicidadeIdList.add(new DuplicidadeId(arquivo, tabela));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Converter a lista em ObservableList
        ObservableList<DuplicidadeId> observableDuplicidadeIdList = FXCollections.observableArrayList(duplicidadeIdList);

        // Adicionar os novos dados à tabela sem limpar os dados antigos
        tableDuplicidadeId.getItems().addAll(observableDuplicidadeIdList);
    }

    private void loadOracleHiveData(String fileName) {
        List<OracleHive> oracleHiveList = new ArrayList<>(); // Lista para os dados da tableViewOracleHive

        try (BufferedReader br = new BufferedReader(new FileReader("\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\" + fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7) { // Verificar se a linha possui pelo menos 7 partes
                    String arquivo = parts[0].replaceAll("\"", "").trim();
                    String tipoDoc = parts[1].replaceAll("\"", "").trim();
                    String tabelaOracle = parts[2].replaceAll("\"", "").trim();
                    String tabelaHive = parts[3].replaceAll("\"", "").trim();

                    // Verificar se os valores são números antes de tentar converter
                    int totalOracle = 0;
                    int totalHive = 0;
                    int diferenca = 0;
                    try {
                        totalOracle = Integer.parseInt(parts[4].replaceAll("\"", "").trim());
                        totalHive = Integer.parseInt(parts[5].replaceAll("\"", "").trim());
                        diferenca = Integer.parseInt(parts[6].replaceAll("\"", "").trim());
                    } catch (NumberFormatException e) {
                        // Se ocorrer uma exceção, continue para a próxima linha
                        continue;
                    }

                    oracleHiveList.add(new OracleHive(arquivo, tipoDoc, tabelaOracle, tabelaHive, totalOracle, totalHive, diferenca));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Converter a lista em ObservableList
        ObservableList<OracleHive> observableOracleHiveList = FXCollections.observableArrayList(oracleHiveList);

        // Adicionar os novos dados à tabela sem limpar os dados antigos
        tableViewOracleHive.getItems().addAll(observableOracleHiveList);
    }
    private void filterOracleHiveTable(String filter) {
        if (filter == null || filter.isEmpty()) {
            // Se o filtro estiver vazio, restaurar a lista original
            tableViewOracleHive.setItems(originalOracleHiveList);
        } else {
            // Aplicar filtro à lista original
            ObservableList<OracleHive> filteredList = originalOracleHiveList.stream()
                    .filter(item -> item.getTipoDoc().equalsIgnoreCase(filter))
                    .collect(Collectors.toCollection(FXCollections::observableArrayList));

            tableViewOracleHive.setItems(filteredList);
        }
    }

    // Método para limpar o filtro e restaurar a lista original
    private void clearFilter() {
        tableViewOracleHive.setItems(originalOracleHiveList);
    }

    // Método para configurar o botão de limpar filtro
    @FXML
    private void onBtLimparAction() {
        btLimpar.setOnAction(event -> clearFilter());
    }

    private void executeQueryAndUpdateUI() {
        if (hiveDatabaseOperations != null) {
            hiveDatabaseOperations.executeQueryAndPopulateResultsDet();
            // Obter os resultados da consulta
            List<com.example.dechivejavafx.model.entities.PendenciasHive> resultList = hiveDatabaseOperations.getQueryResultsDet();
            // Converter a lista em ObservableList
            ObservableList<com.example.dechivejavafx.model.entities.PendenciasHive> observableResultList = FXCollections.observableArrayList(resultList);
            // Preencher a tabela com os resultados
            tableViewDetNFeNFCeInf.setItems(observableResultList);

        }
    }
}
