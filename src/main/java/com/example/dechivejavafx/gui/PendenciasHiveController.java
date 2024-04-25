package com.example.dechivejavafx.gui;

import com.example.dechivejavafx.Validacoes.TipoDoc;
import com.example.dechivejavafx.db.DatabaseConfig;
import com.example.dechivejavafx.db.HiveDecDatabaseOperations;
import com.example.dechivejavafx.gui.util.CSVUtils;
import com.example.dechivejavafx.gui.util.ComboBoxUtil;
import com.example.dechivejavafx.model.entities.DuplicidadeId;
import com.example.dechivejavafx.model.entities.OracleHive;
import com.example.dechivejavafx.model.entities.PendenciasHive;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PendenciasHiveController {

    public StackPane pendenciasHivePane;
    // Declaração do conjunto para armazenar os arquivos distintos
    private Set<String> distinctFiles = new HashSet<>();
    // Conjunto para armazenar as chaves compostas únicas de arquivo e tabela
    private Set<String> distinctKeys = new HashSet<>();
    private boolean executeQuery;
    public Button btLimpar;
    @FXML
    private Label tituloTableViews;
    // Labels para exibição de resultados individuais
    @FXML
    private Label labelArquivo;
    @FXML
    private Label labelcolumnTabelaDetalhe;
    @FXML
    private Label labelQuantidadeNsuchave;
    @FXML
    private ComboBox<TipoDoc> comboTipoDoc;
    // Tabela para exibição de resultados em formato tabular
    @FXML
    private TableView<PendenciasHive> tableViewDetNFeNFCeInf;
    @FXML
    private TableView<DuplicidadeId> tableDuplicidadeId;
    @FXML
    private TableView<OracleHive> tableViewOracleHive;
    // Colunas da tabela para diferentes propriedades da entidade TotalizacaoNfe
    @FXML
    private TableColumn<PendenciasHive, String> columnArquivo;
    @FXML
    private TableColumn<PendenciasHive, String> columnTabelaDetalhe;
    @FXML
    private TableColumn<PendenciasHive, Integer> columnQuantidadeNsuchave;
    @FXML
    private TableColumn<DuplicidadeId, String> columnArquivoDuplicidade;
    @FXML
    private TableColumn<DuplicidadeId, String> columnTabelaDuplicidade;
    @FXML
    private TableColumn<OracleHive, String> columnArquivoOracle;
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
    // Operações no banco de dados Hive
    private HiveDecDatabaseOperations hiveDatabaseOperations;
    private ObservableList<OracleHive> originalOracleHiveList = FXCollections.observableArrayList(); // Lista para armazenar os resultados originais
    // Configurações do banco de dados
    private DatabaseConfig databaseConfig = new DatabaseConfig();

    // Método de inicialização chamado pelo JavaFX quando o arquivo FXML é carregado
    public void initialize() {
        // Inicializa as ComboBoxes na interface
        initializeComboBoxes();

        // Configura as colunas das tabelas na interface
        configureTableColumns();

        // Configura as operações de banco de dados Hive
        configureHiveDatabaseOperations();

        // Carrega os dados dos arquivos CSV para as tabelas na interface
        loadCSVData();

        // Configura um ouvinte de alteração para a ComboBox comboTipoDoc
        setupComboBoxListener();

        // Copia a lista original de itens da tabela OracleHive
        copyOriginalList();

        // Executa a consulta inicial e atualiza a interface gráfica
        if (executeQuery) {
            executeQueryAndUpdateUI();
        }
        // Configura a ordenação inicial das tabelas na interface
        configureTableSorting();
    }

    // Método para configurar as colunas das tabelas na interface
    private void configureTableColumns() {
        // Configuração das colunas específicas para exibir diferentes dados
        configureColumn(columnArquivo, PendenciasHive::getArquivo);
        configureColumn(columnTabelaDetalhe, PendenciasHive::getTabelaDetalhe);
        configureColumn(columnQuantidadeNsuchave, PendenciasHive::getQuantidadeNsuchave);
        configureColumnDuplicidade(columnArquivoDuplicidade, DuplicidadeId::getArquivo);
        configureColumnDuplicidade(columnTabelaDuplicidade, DuplicidadeId::getTabela);
        configureColumOracleHive(columnArquivoOracle, OracleHive::getArquivo);
        configureColumOracleHive(columnTipoDoc, OracleHive::getTipoDoc);
        configureColumOracleHive(columnTabelaOracle, OracleHive::getTabelaOracle);
        configureColumOracleHive(columnTabelaHive, OracleHive::getTabelaHive);
        configureColumOracleHive(columnTotalOracle, OracleHive::getTotalOracle);
        configureColumOracleHive(columnTotalHive, OracleHive::getTotalHive);
        configureColumOracleHive(columnDiferenca, OracleHive::getDiferenca);
    }

    // Método para configurar as operações de banco de dados Hive
    private void configureHiveDatabaseOperations() {
        // Cria a instância das operações de banco de dados Hive
        hiveDatabaseOperations = createHiveDatabaseOperations();
    }

    // Método para carregar os dados dos arquivos CSV para as tabelas na interface
    private void loadCSVData() {
        // Carrega os dados dos arquivos CSV específicos para as tabelas na interface
        loadDuplicidadeIdData("DuplicidadeIdDetalhe.csv");
        loadDuplicidadeIdData("DuplicidadeIdPrincipal.csv");
        loadOracleHiveData("pendencias.csv");
        loadPendenciaPrincipalDetalhe("PendenciaPrincipalDetalhe.csv");
    }

    // Método para configurar um ouvinte de alteração para a ComboBox comboTipoDoc
    private void setupComboBoxListener() {
        // Configura um ouvinte de alteração para a ComboBox comboTipoDoc
        comboTipoDoc.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Filtra a tabela OracleHive com base no novo valor selecionado na ComboBox
            filterOracleHiveTable(newValue.toString());
        });
    }

    // Método para copiar a lista original de itens da tabela OracleHive
    private void copyOriginalList() {
        // Copia a lista original de itens da tabela OracleHive para uso posterior
        originalOracleHiveList.addAll(tableViewOracleHive.getItems());
    }

    // Método para configurar a ordenação inicial das tabelas na interface
    private void configureTableSorting() {
        // Configura a ordenação inicial das tabelas na interface
        tableViewOracleHive.getSortOrder().addAll(columnTipoDoc, columnArquivoOracle);
        tableDuplicidadeId.getSortOrder().addAll(columnTabelaDuplicidade, columnArquivoDuplicidade);
        tableViewDetNFeNFCeInf.getSortOrder().addAll(columnTabelaDetalhe, columnArquivo);
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
        try (BufferedReader br = new BufferedReader(new FileReader("\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\" + fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    String arquivo = parts[0].replaceAll("\"", "").trim();
                    String tabela = parts[1].replaceAll("\"", "").trim();
                    String compositeKey = arquivo + tabela;
                    if (!distinctKeys.contains(compositeKey)) {
                        distinctKeys.add(compositeKey);
                        tableDuplicidadeId.getItems().add(new DuplicidadeId(arquivo, tabela));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadOracleHiveData(String fileName) {
        Set<String> uniqueFiles = new HashSet<>(); // Conjunto para armazenar arquivos únicos pelo índice 0
        List<OracleHive> oracleHiveList = new ArrayList<>(); // Lista para os dados da tableViewOracleHive

        try (BufferedReader br = new BufferedReader(new FileReader("\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\" + fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 7) { // Verificar se a linha possui pelo menos 7 partes
                    String arquivo = parts[0].replaceAll("\"", "").trim();
                    // Verificar se o arquivo já foi adicionado, se sim, pular para a próxima linha
                    if (!uniqueFiles.add(arquivo)) {
                        continue;
                    }
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

        // Limpar os itens existentes na tabela e adicionar apenas os novos itens
        tableViewOracleHive.getItems().setAll(observableOracleHiveList);
    }

    // Método para carregar os dados do arquivo CSV para a tabela de pendências
    private void loadPendenciaPrincipalDetalhe(String fileName) {
        List<PendenciasHive> pendenciasHiveList = new ArrayList<>(); // Lista para os dados da tableViewOracleHive

        try (BufferedReader br = new BufferedReader(new FileReader("\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\" + fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) {
                    continue; // Ignorar linhas com menos de 3 partes
                }

                String arquivo = parts[0].replaceAll("\"", "").trim();
                String tabelaDetalhe = parts[1].replaceAll("\"", "").trim();

                // Tentar converter o valor para inteiro, se falhar, continue para a próxima linha
                int quantidadeNsuchave;
                try {
                    quantidadeNsuchave = Integer.parseInt(parts[2].replaceAll("\"", "").trim());
                } catch (NumberFormatException e) {
                    continue;
                }

                pendenciasHiveList.add(new PendenciasHive(arquivo, tabelaDetalhe, quantidadeNsuchave));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Converter a lista em ObservableList e adicionar à tabela
        tableViewDetNFeNFCeInf.getItems().setAll(FXCollections.observableArrayList(pendenciasHiveList));
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

            // Salvar a lista em um arquivo CSV
            CSVUtils.saveListPendenciaPrincipalDetalheToCSV(resultList);
        }
    }

    public void setExecuteQuery(boolean execute) {
        this.executeQuery = execute;
    }
}
