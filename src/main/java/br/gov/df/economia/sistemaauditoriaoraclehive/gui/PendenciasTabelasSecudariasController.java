package br.gov.df.economia.sistemaauditoriaoraclehive.gui;

import br.gov.df.economia.sistemaauditoriaoraclehive.db.DatabaseConfig;
import br.gov.df.economia.sistemaauditoriaoraclehive.db.HiveQueryExecutorTabelasDependentes;
import br.gov.df.economia.sistemaauditoriaoraclehive.model.entities.*;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PendenciasTabelasSecudariasController {

    public StackPane pendenciasHivePane;
    // Declaração do conjunto para armazenar os arquivos distintos
    private boolean executeQuery;    @FXML
    private Label tituloTableViews;
    // Labels para exibição de resultados individuais
    @FXML
    private Label labelArquivo;
    @FXML
    private Label labelcolumnTabelaDetalhe;
    @FXML
    private Label labelQuantidadeNsuchave;
    // Tabela para exibição de resultados em formato tabular
    @FXML
    private TableView<BpeComp> tableViewBpeComp;
    @FXML
    private TableView<CteInfcteInfq> tableViewCteInfcteInfq;
    @FXML
    private TableView<MdfeInfMdfeInfCteInfNfe> tableViewMdfeInfMdfeInfCteInfNfe;
    @FXML
    private TableView<Nf3eGrandfatGrcontrat> tableViewNf3eGrandfatGrcontrat;
    @FXML
    private TableView<NfceDetpag> tableViewNfceDetpag;
    @FXML
    private TableView<NfeDetpagVolNfref> tableViewNfeDetpagVolNfref;
    @FXML
    private TableView<NfeItensAverbados> tableViewNfeItensAverbados;

    // Colunas da tabela para diferentes propriedades da entidade TotalizacaoNfe
    @FXML
    private TableColumn<BpeComp, String> columnBpeArquivo;
    @FXML
    private TableColumn<BpeComp, Integer> columnTotalInfBpe;
    @FXML
    private TableColumn<BpeComp, String> columnBpeComp;
    @FXML
    private TableColumn<CteInfcteInfq, String> columnCteArquivo;
    @FXML
    private TableColumn<CteInfcteInfq, Integer> columnTotalInfCte;
    @FXML
    private TableColumn<CteInfcteInfq, String> columnCteinfnfe;
    @FXML
    private TableColumn<CteInfcteInfq, String> columnCteinfq;
    @FXML
    private TableColumn<MdfeInfMdfeInfCteInfNfe, String> columnMdfeArquivo;
    @FXML
    private TableColumn<MdfeInfMdfeInfCteInfNfe, Integer> columntotalInfMdfe;
    @FXML
    private TableColumn<MdfeInfMdfeInfCteInfNfe, String> columnMdfeInfCte;
    @FXML
    private TableColumn<MdfeInfMdfeInfCteInfNfe, String> columnMdfeInfNfe;
    @FXML
    private TableColumn<Nf3eGrandfatGrcontrat, String> columnNf3eArquivo;
    @FXML
    private TableColumn<Nf3eGrandfatGrcontrat, Integer> columnTotalInfNf3e;
    @FXML
    private TableColumn<Nf3eGrandfatGrcontrat, String> columnNf3eGgrandfat;
    @FXML
    private TableColumn<Nf3eGrandfatGrcontrat, String> columnNf3eGgrcontrat;
    @FXML
    private TableColumn<NfceDetpag, String> columnNfceArquivo;
    @FXML
    private TableColumn<NfceDetpag, Integer> columnTotalInfNfce;
    @FXML
    private TableColumn<NfceDetpag, String> columnNfceDetPag;
    @FXML
    private TableColumn<NfeDetpagVolNfref, String> columnNfeArquivo;
    @FXML
    private TableColumn<NfeDetpagVolNfref, Integer> columnTotalInfNfe;
    @FXML
    private TableColumn<NfeDetpagVolNfref, String> columnNfeDetpag;
    @FXML
    private TableColumn<NfeDetpagVolNfref, String> columnNfeVol;
    @FXML
    private TableColumn<NfeDetpagVolNfref, String> columnNfeNfRef;
    @FXML
    private TableColumn<NfeItensAverbados, String> columnNfeItensArquivo;
    @FXML
    private TableColumn<NfeItensAverbados, Integer> columnNfeTotalItensAverbados;
    @FXML
    private TableColumn<NfeItensAverbados, String> columnItensAverbados;
    // Operações no banco de dados Hive
    private HiveQueryExecutorTabelasDependentes hiveQueryExecutorTabelasDependentes;
    // Configurações do banco de dados
    private DatabaseConfig databaseConfig = new DatabaseConfig();

    // Método de inicialização chamado pelo JavaFX quando o arquivo FXML é carregado
    public void initialize() {

        HiveQueryExecutorTabelasDependentes executor = new HiveQueryExecutorTabelasDependentes();
        executor.executeAllQueries();

        // Configura as colunas das tabelas na interface
        configureTableColumns();

        // Carrega os dados dos arquivos CSV para as tabelas na interface
        loadCSVData();



        // Executa a consulta inicial e atualiza a interface gráfica
        if (executeQuery) {
            executeQueryAndUpdateUI();
        }
    }

    // Método para configurar as colunas das tabelas na interface
    private void configureTableColumns() {
        // Configuração das colunas específicas para exibir diferentes dados
        configureColumnBpeComp(columnBpeArquivo, BpeComp::getArquivo);
        configureColumnBpeComp(columnTotalInfBpe, BpeComp::getTotalInfBpe);
        configureColumnBpeComp(columnBpeComp, BpeComp::getComp);

        configureColumnCteInfcteInfq(columnCteArquivo, CteInfcteInfq::getArquivo);
        configureColumnCteInfcteInfq(columnTotalInfCte, CteInfcteInfq::getTotalInfCte);
        configureColumnCteInfcteInfq(columnCteinfnfe, CteInfcteInfq::getInfnfe);
        configureColumnCteInfcteInfq(columnCteinfq, CteInfcteInfq::getInfq);

        configureColumMdfeInfMdfeInfCteInfNfe(columnMdfeArquivo, MdfeInfMdfeInfCteInfNfe::getArquivo);
        configureColumMdfeInfMdfeInfCteInfNfe(columntotalInfMdfe, MdfeInfMdfeInfCteInfNfe::getTotalInfMdfe);
        configureColumMdfeInfMdfeInfCteInfNfe(columnMdfeInfCte, MdfeInfMdfeInfCteInfNfe::getInfcte);
        configureColumMdfeInfMdfeInfCteInfNfe(columnMdfeInfNfe, MdfeInfMdfeInfCteInfNfe::getInfnfe);

        configureColumNf3eGrandfatGrcontrat(columnNf3eArquivo, Nf3eGrandfatGrcontrat::getArquivo);
        configureColumNf3eGrandfatGrcontrat(columnTotalInfNf3e, Nf3eGrandfatGrcontrat::getTotalInfNf3e);
        configureColumNf3eGrandfatGrcontrat(columnNf3eGgrandfat, Nf3eGrandfatGrcontrat::getGgrandfat);
        configureColumNf3eGrandfatGrcontrat(columnNf3eGgrcontrat, Nf3eGrandfatGrcontrat::getGgrcontrat);

        configureColumNfceDetpag(columnNfceArquivo, NfceDetpag::getArquivo);
        configureColumNfceDetpag(columnTotalInfNfce, NfceDetpag::getTotalInfNfce);
        configureColumNfceDetpag(columnNfceDetPag, NfceDetpag::getDetpag);

        configureColumNfeItensAverbados(columnNfeItensArquivo, NfeItensAverbados::getArquivo);
        configureColumNfeItensAverbados(columnNfeTotalItensAverbados, NfeItensAverbados::getTotalItensAverbados);
        configureColumNfeItensAverbados(columnItensAverbados, NfeItensAverbados::getItensAverbados);

        configureColumNfeDetpagVolNfref(columnNfeArquivo, NfeDetpagVolNfref::getArquivo);
        configureColumNfeDetpagVolNfref(columnTotalInfNfe, NfeDetpagVolNfref::getTotalInfNfe);
        configureColumNfeDetpagVolNfref(columnNfeDetpag, NfeDetpagVolNfref::getDetpag);
        configureColumNfeDetpagVolNfref(columnNfeVol, NfeDetpagVolNfref::getVol);
        configureColumNfeDetpagVolNfref(columnNfeNfRef, NfeDetpagVolNfref::getNfref);


    }
    // Método para carregar os dados dos arquivos CSV para as tabelas na interface
    private void loadCSVData() {
        // Carrega os dados dos arquivos CSV específicos para as tabelas na interface
        loadBpeComp("BpeComp.csv");
        loadCteInfcteInfq("CteInfcteInfq.csv");
        loadMdfeInfMdfeInfCteInfNfe("MdfeInfMdfeInfCteInfNfe.csv");
        loadNf3eGrandfatGrcontrat("Nf3egrandfatGrcontrat.csv");
        loadNfeDetpagVolNfref("NfeDetpagVolNfref.csv");
        loadNfeItensAverbados("nfeItensAverbados.csv");
        loadNfceDetpag("NfceDetpag.csv");

    }

    // Método para configurar coluna de texto na tabela
    private <T> void configureColumnBpeComp(TableColumn<BpeComp, T> column, Function<BpeComp, T> valueExtractor) {
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

    private <T> void configureColumnCteInfcteInfq(TableColumn<CteInfcteInfq, T> column, Function<CteInfcteInfq, T> valueExtractor) {
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
    private <T> void configureColumMdfeInfMdfeInfCteInfNfe(TableColumn<MdfeInfMdfeInfCteInfNfe, T> column, Function<MdfeInfMdfeInfCteInfNfe, T> valueExtractor) {
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


    private <T> void configureColumNf3eGrandfatGrcontrat(TableColumn<Nf3eGrandfatGrcontrat, T> column, Function<Nf3eGrandfatGrcontrat, T> valueExtractor) {
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

    private <T> void configureColumNfceDetpag(TableColumn<NfceDetpag, T> column, Function<NfceDetpag, T> valueExtractor) {
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

    private <T> void configureColumNfeDetpagVolNfref(TableColumn<NfeDetpagVolNfref, T> column, Function<NfeDetpagVolNfref, T> valueExtractor) {
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

    private <T> void configureColumNfeItensAverbados(TableColumn<NfeItensAverbados, T> column, Function<NfeItensAverbados, T> valueExtractor) {
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

    // Método para carregar os dados do arquivo CSV para a tabela de pendências
    private void loadCteInfcteInfq(String fileName) {
        List<CteInfcteInfq> cteInfcteInfqList = new ArrayList<>(); // Lista para os dados da tableViewOracleHive

        try (BufferedReader br = new BufferedReader(new FileReader("\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\" + fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) {
                    continue; // Ignorar linhas com menos de 3 partes
                }

                String arquivo = parts[0].replaceAll("\"", "").trim();
                // Tentar converter o valor para inteiro, se falhar, continue para a próxima linha
                int totalDocumentos;
                try {
                    totalDocumentos = Integer.parseInt(parts[1].replaceAll("\"", "").trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                String infnfe = parts[2].replaceAll("\"", "").trim();
                String infq = parts[3].replaceAll("\"", "").trim();



                cteInfcteInfqList.add(new CteInfcteInfq(arquivo, totalDocumentos, infnfe, infq));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Converter a lista em ObservableList e adicionar à tabela
        tableViewCteInfcteInfq.getItems().setAll(FXCollections.observableArrayList(cteInfcteInfqList));
    }

    private void loadMdfeInfMdfeInfCteInfNfe(String fileName) {
        List<MdfeInfMdfeInfCteInfNfe> mdfeInfMdfeInfCteInfNfeList = new ArrayList<>(); // Lista para os dados da tableViewOracleHive

        try (BufferedReader br = new BufferedReader(new FileReader("\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\" + fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) {
                    continue; // Ignorar linhas com menos de 3 partes
                }

                String arquivo = parts[0].replaceAll("\"", "").trim();
                // Tentar converter o valor para inteiro, se falhar, continue para a próxima linha
                int totalDocumentos;
                try {
                    totalDocumentos = Integer.parseInt(parts[1].replaceAll("\"", "").trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                String infcte = parts[2].replaceAll("\"", "").trim();
                String infnfe = parts[3].replaceAll("\"", "").trim();



                mdfeInfMdfeInfCteInfNfeList.add(new MdfeInfMdfeInfCteInfNfe(arquivo, totalDocumentos, infcte, infnfe));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Converter a lista em ObservableList e adicionar à tabela
        tableViewMdfeInfMdfeInfCteInfNfe.getItems().setAll(FXCollections.observableArrayList(mdfeInfMdfeInfCteInfNfeList));
    }

    private void loadNf3eGrandfatGrcontrat(String fileName) {
        List<Nf3eGrandfatGrcontrat> nf3eGrandfatGrcontratList = new ArrayList<>(); // Lista para os dados da tableViewOracleHive

        try (BufferedReader br = new BufferedReader(new FileReader("\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\" + fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 4) {
                    continue; // Ignorar linhas com menos de 3 partes
                }

                String arquivo = parts[0].replaceAll("\"", "").trim();
                // Tentar converter o valor para inteiro, se falhar, continue para a próxima linha
                int totalDocumentos;
                try {
                    totalDocumentos = Integer.parseInt(parts[1].replaceAll("\"", "").trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                String ggrandfat = parts[2].replaceAll("\"", "").trim();
                String ggrcontrat = parts[3].replaceAll("\"", "").trim();



                nf3eGrandfatGrcontratList.add(new Nf3eGrandfatGrcontrat(arquivo, totalDocumentos, ggrandfat, ggrcontrat));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Converter a lista em ObservableList e adicionar à tabela
        tableViewNf3eGrandfatGrcontrat.getItems().setAll(FXCollections.observableArrayList(nf3eGrandfatGrcontratList));
    }

    private void loadBpeComp(String fileName) {
        List<BpeComp> bpeCompList = new ArrayList<>(); // Lista para os dados da tableViewOracleHive

        try (BufferedReader br = new BufferedReader(new FileReader("\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\" + fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) {
                    continue; // Ignorar linhas com menos de 3 partes
                }

                String arquivo = parts[0].replaceAll("\"", "").trim();
                // Tentar converter o valor para inteiro, se falhar, continue para a próxima linha
                int totalDocumentos;
                try {
                    totalDocumentos = Integer.parseInt(parts[1].replaceAll("\"", "").trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                String tabela = parts[2].replaceAll("\"", "").trim();



                bpeCompList.add(new BpeComp(arquivo, totalDocumentos, tabela));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Converter a lista em ObservableList e adicionar à tabela
        tableViewBpeComp.getItems().setAll(FXCollections.observableArrayList(bpeCompList));
    }

    private void loadNfeItensAverbados(String fileName) {
        List<NfeItensAverbados> nfeItensAverbadosList = new ArrayList<>(); // Lista para os dados da tableViewOracleHive

        try (BufferedReader br = new BufferedReader(new FileReader("\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\" + fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) {
                    continue; // Ignorar linhas com menos de 3 partes
                }

                String arquivo = parts[0].replaceAll("\"", "").trim();
                // Tentar converter o valor para inteiro, se falhar, continue para a próxima linha
                int totalDocumentos;
                try {
                    totalDocumentos = Integer.parseInt(parts[1].replaceAll("\"", "").trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                String itensAverbados = parts[2].replaceAll("\"", "").trim();



                nfeItensAverbadosList.add(new NfeItensAverbados(arquivo, totalDocumentos, itensAverbados));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Converter a lista em ObservableList e adicionar à tabela
        tableViewNfeItensAverbados.getItems().setAll(FXCollections.observableArrayList(nfeItensAverbadosList));
    }

    private void loadNfceDetpag(String fileName) {
        List<NfceDetpag> nfceDetpagList = new ArrayList<>(); // Lista para os dados da tableViewOracleHive

        try (BufferedReader br = new BufferedReader(new FileReader("\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\" + fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) {
                    continue; // Ignorar linhas com menos de 3 partes
                }

                String arquivo = parts[0].replaceAll("\"", "").trim();
                // Tentar converter o valor para inteiro, se falhar, continue para a próxima linha
                int totalDocumentos;
                try {
                    totalDocumentos = Integer.parseInt(parts[1].replaceAll("\"", "").trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                String detpag = parts[2].replaceAll("\"", "").trim();



                nfceDetpagList.add(new NfceDetpag(arquivo, totalDocumentos, detpag));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Converter a lista em ObservableList e adicionar à tabela
        tableViewNfceDetpag.getItems().setAll(FXCollections.observableArrayList(nfceDetpagList));
    }

    private void loadNfeDetpagVolNfref(String fileName) {
        List<NfeDetpagVolNfref> nfeDetpagVolNfrefList = new ArrayList<>(); // Lista para os dados da tableViewOracleHive

        try (BufferedReader br = new BufferedReader(new FileReader("\\\\svmcifs\\ExtracaoXML\\NovoDEC\\Pendencia\\" + fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) {
                    continue; // Ignorar linhas com menos de 3 partes
                }

                String arquivo = parts[0].replaceAll("\"", "").trim();
                // Tentar converter o valor para inteiro, se falhar, continue para a próxima linha
                int totalDocumentos;
                try {
                    totalDocumentos = Integer.parseInt(parts[1].replaceAll("\"", "").trim());
                } catch (NumberFormatException e) {
                    continue;
                }
                String detpag = parts[2].replaceAll("\"", "").trim();
                String vol = parts[3].replaceAll("\"", "").trim();
                String nfref = parts[4].replaceAll("\"", "").trim();



                nfeDetpagVolNfrefList.add(new NfeDetpagVolNfref(arquivo, totalDocumentos, detpag, vol, nfref));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Converter a lista em ObservableList e adicionar à tabela
        tableViewNfeDetpagVolNfref.getItems().setAll(FXCollections.observableArrayList(nfeDetpagVolNfrefList));
    }

    public void executeQueryAndUpdateUI() {
        try {
            // Executa todas as consultas
            hiveQueryExecutorTabelasDependentes.executeAllQueries();

            // Carrega os dados dos arquivos CSV nas TableViews
            loadCSVData();
        } catch (Exception e) {
            e.printStackTrace();
            // Exibe uma mensagem de erro na interface do usuário
            showError("Erro ao executar a consulta e atualizar a interface: " + e.getMessage());
        }
    }

    // Método para exibir mensagens de erro
    public void showError(String errorMessage) {
        // Cria uma nova caixa de diálogo de alerta
        Alert alert = new Alert(Alert.AlertType.ERROR);

        // Define o título da caixa de diálogo
        alert.setTitle("Erro");

        // Define o cabeçalho da caixa de diálogo
        alert.setHeaderText(null);

        // Define a mensagem de erro a ser exibida
        alert.setContentText(errorMessage);

        // Exibe a caixa de diálogo e aguarda o usuário fechá-la
        alert.showAndWait();
    }


    public void setExecuteQuery(boolean execute) {
        this.executeQuery = execute;
    }
}
