package br.gov.df.economia.sistemaauditoriaoraclehive.gui.util;

import br.gov.df.economia.sistemaauditoriaoraclehive.model.entities.Agenda;
import br.gov.df.economia.sistemaauditoriaoraclehive.model.entities.OracleHive;
import br.gov.df.economia.sistemaauditoriaoraclehive.model.entities.QuantidadeDocumentoArquivo;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class TableViewManager {

    /**
     * Preenche a TableView com os dados do OracleHive, filtrando os resultados e ordenando pela coluna Oracle.
     *
     * @param tableView   A TableView que será populada com os dados.
     * @param resultados  Lista de resultados de OracleHive.
     */
    public void popularTabelaOracleHive(TableView<OracleHive> tableView, List<OracleHive> resultados) {
        // Configurar colunas (assumindo que as colunas já estão definidas na TableView)
        configurarColunas(tableView);

        // Criar e filtrar lista observável
        ObservableList<OracleHive> observableList = FXCollections.observableArrayList(resultados)
                .filtered(oracleHive -> oracleHive.getDiferenca() > 0);

        // Atualizar a TableView
        tableView.setItems(observableList);

        // Ordenar a TableView
        TableColumn<OracleHive, ?> columnTabelaOracle = tableView.getColumns().get(2); // Assumindo que a coluna Oracle é a terceira
        tableView.getSortOrder().clear();
        tableView.getSortOrder().add(columnTabelaOracle);
        tableView.sort();

        // Salvar os resultados filtrados em CSV
        salvarResultadosEmCSV(observableList);
    }

    /**
     * Configura as colunas da TableView para exibir os dados corretos de OracleHive.
     *
     * @param tableView A TableView que será configurada.
     */
    private void configurarColunas(TableView<OracleHive> tableView) {
        // Exemplo de configuração para uma coluna, repita para outras conforme necessário
        TableColumn<OracleHive, String> columnArquivo = new TableColumn<>("Arquivo");
        columnArquivo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getArquivo()));

        TableColumn<OracleHive, String> columnTipoDoc = new TableColumn<>("Tipo Documento");
        columnTipoDoc.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTipoDoc().toString()));

        TableColumn<OracleHive, String> columnTabelaOracle = new TableColumn<>("Tabela Oracle");
        columnTabelaOracle.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTabelaOracle()));

        TableColumn<OracleHive, String> columnTabelaHive = new TableColumn<>("Tabela Hive");
        columnTabelaHive.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTabelaHive()));

        TableColumn<OracleHive, Integer> columnTotalOracle = new TableColumn<>("Total Oracle");
        columnTotalOracle.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTotalOracle()).asObject());

        TableColumn<OracleHive, Integer> columnTotalHive = new TableColumn<>("Total Hive");
        columnTotalHive.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTotalHive()).asObject());

        TableColumn<OracleHive, Integer> columnDiferenca = new TableColumn<>("Diferença");
        columnDiferenca.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getDiferenca()).asObject());

        // Configuração de classificação para a coluna de tabelaOracle
        columnTabelaOracle.setSortType(TableColumn.SortType.ASCENDING);
        columnTabelaOracle.setComparator(String::compareToIgnoreCase);

        // Adiciona as colunas à TableView se ainda não foram adicionadas
        if (tableView.getColumns().isEmpty()) {
            tableView.getColumns().addAll(columnArquivo, columnTipoDoc, columnTabelaOracle, columnTabelaHive, columnTotalOracle, columnTotalHive, columnDiferenca);
        }
    }

    /**
     * Salva os resultados filtrados em CSV.
     *
     * @param resultados Lista de resultados a serem salvos.
     */
    private void salvarResultadosEmCSV(ObservableList<OracleHive> resultados) {
        try {
            CSVUtils.salvarResultadosCSV(resultados); // Adapte o método conforme necessário para aceitar ObservableList
        } catch (Exception e) {
            e.printStackTrace();
            // Tratamento de erro ou log conforme necessário
        }
    }

    /**
     * Configura as colunas da TableView para exibir os dados corretos de Agenda.
     *
     * @param tableColumnCodigo      TableColumn que exibe o código.
     * @param tableColumnTipo        TableColumn que exibe o tipo de documento.
     * @param tableColumnInicio      TableColumn que exibe a data de início.
     * @param tableColumnFim         TableColumn que exibe a data de fim.
     * @param tableColumnArquivo     TableColumn que exibe o nome do arquivo.
     * @param tableColumnQuantidade  TableColumn que exibe a quantidade.
     * @param tableColumnSituacao    TableColumn que exibe a situação.
     */
    public static void popularTabelaAgenda(TableColumn<Agenda, Long> tableColumnCodigo, TableColumn<Agenda, String> tableColumnTipo, TableColumn<Agenda, String> tableColumnInicio, TableColumn<Agenda, String> tableColumnFim, TableColumn<Agenda, String> tableColumnArquivo, TableColumn<Agenda, String> tableColumnQuantidade, TableColumn<Agenda, String> tableColumnSituacao) {
        tableColumnCodigo.setCellValueFactory(new PropertyValueFactory<>("cod_agenda_extracao"));
        tableColumnTipo.setCellValueFactory(new PropertyValueFactory<>("tipo_doc"));
        tableColumnInicio.setCellValueFactory(new PropertyValueFactory<>("par_inicio"));
        tableColumnFim.setCellValueFactory(new PropertyValueFactory<>("par_fim"));
        tableColumnArquivo.setCellValueFactory(new PropertyValueFactory<>("nome_arquivo"));
        tableColumnQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        tableColumnSituacao.setCellValueFactory(new PropertyValueFactory<>("ind_situacao"));
    }

    /**
     * Preenche a TableView com os dados de quantidade de documentos por arquivo.
     *
     * @param tableView          A TableView que será populada com os dados.
     * @param resultados         Lista de resultados de QuantidadeDocumentoArquivo.
     * @param columnTabelaHive   A TableColumn que exibe o nome da tabela Hive.
     * @param columnArquivo      A TableColumn que exibe o nome do arquivo.
     * @param columnDia          A TableColumn que exibe o dia.
     * @param columnTotal        A TableColumn que exibe o total de documentos.
     */
    public static void popularTabelaQuantidadeDocumentosArquivo(
            TableView<QuantidadeDocumentoArquivo> tableView,
            List<QuantidadeDocumentoArquivo> resultados,
            TableColumn<QuantidadeDocumentoArquivo, String> columnTabelaHive,
            TableColumn<QuantidadeDocumentoArquivo, String> columnArquivo,
            TableColumn<QuantidadeDocumentoArquivo, String> columnDia,
            TableColumn<QuantidadeDocumentoArquivo, Integer> columnTotal) {

        // Define os itens da tabela com a lista de resultados
        tableView.getItems().setAll(resultados);

        // Configura as propriedades das colunas para exibir os dados corretos
        columnTabelaHive.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTabelaHive()));
        columnArquivo.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getArquivo()));
        columnDia.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDia()));
        columnTotal.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getTotal()).asObject());
    }
}
