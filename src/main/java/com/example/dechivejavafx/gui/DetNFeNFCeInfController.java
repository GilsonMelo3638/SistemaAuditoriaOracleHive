package com.example.dechivejavafx.gui;

import com.example.dechivejavafx.db.DatabaseConfig;
import com.example.dechivejavafx.db.HiveDatabaseOperations;
import com.example.dechivejavafx.model.entities.DetNFeNFCeInf;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.function.Function;

public class DetNFeNFCeInfController {

    // Labels para exibição de resultados individuais
    @FXML private Label labelArquivo;
    @FXML private Label labelcolumnTabelaDetalhe;
    @FXML private Label labelQuantidadeNsuchave;
    // Tabela para exibição de resultados em formato tabular
    @FXML private TableView<DetNFeNFCeInf> tableViewDetNFeNFCeInf;
    // Colunas da tabela para diferentes propriedades da entidade TotalizacaoNfe
    @FXML private TableColumn<DetNFeNFCeInf, String> columnArquivo;
    @FXML private TableColumn<DetNFeNFCeInf, String> columnTabelaDetalhe;
    @FXML private TableColumn<DetNFeNFCeInf, Integer> columnQuantidadeNsuchave;
    // Operações no banco de dados Hive
    private HiveDatabaseOperations hiveDatabaseOperations;
    // Configurações do banco de dados
    private DatabaseConfig databaseConfig = new DatabaseConfig();
    // Método de inicialização chamado pelo JavaFX quando o arquivo FXML é carregado
    @FXML
    public void initialize() {
        // Configurar as colunas da tabela
        configureColumn(columnArquivo, DetNFeNFCeInf::getArquivo);
        configureColumn(columnTabelaDetalhe, DetNFeNFCeInf::getTabelaDetalhe);
        configureColumn(columnQuantidadeNsuchave, DetNFeNFCeInf::getQuantidadeNsuchave);
        // Configurar a instância HiveDatabaseOperations
        hiveDatabaseOperations = createHiveDatabaseOperations();
        // Executar a consulta e atualizar a interface gráfica
        executeQueryAndUpdateUI();
    }

    // Método para configurar coluna de texto na tabela
    private <T> void configureColumn(TableColumn<DetNFeNFCeInf, T> column, Function<DetNFeNFCeInf, T> valueExtractor) {
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
    private HiveDatabaseOperations createHiveDatabaseOperations() {
        String jdbcUrl = System.getenv("HIVE_JDBC_URL");
        String username = System.getenv("HIVE_USERNAME");
        String password = System.getenv("HIVE_PASSWORD");
        // Verificar se as variáveis de ambiente estão configuradas corretamente
        if (jdbcUrl == null || username == null || password == null) {
            System.err.println("Erro: As variáveis de ambiente não estão configuradas corretamente.");
            return null;
        }
        return new HiveDatabaseOperations(jdbcUrl, username, password);
    }
    private void executeQueryAndUpdateUI() {
        if (hiveDatabaseOperations != null) {
            hiveDatabaseOperations.executeQueryAndPopulateResultsDet();
            // Obter os resultados da consulta
            List<DetNFeNFCeInf> resultList = hiveDatabaseOperations.getQueryResultsDet();
            // Converter a lista em ObservableList
            ObservableList<DetNFeNFCeInf> observableResultList = FXCollections.observableArrayList(resultList);
            // Preencher a tabela com os resultados
            tableViewDetNFeNFCeInf.setItems(observableResultList);
        }
    }
}
