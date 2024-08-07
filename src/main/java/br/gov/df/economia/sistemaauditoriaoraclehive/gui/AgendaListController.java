package br.gov.df.economia.sistemaauditoriaoraclehive.gui;

import br.gov.df.economia.sistemaauditoriaoraclehive.application.Main;
import br.gov.df.economia.sistemaauditoriaoraclehive.gui.util.*;
import br.gov.df.economia.sistemaauditoriaoraclehive.Validacoes.SituacaoProcessamento;
import br.gov.df.economia.sistemaauditoriaoraclehive.Validacoes.TipoDoc;
import br.gov.df.economia.sistemaauditoriaoraclehive.db.DatabaseIntegrityExceptions;
import br.gov.df.economia.sistemaauditoriaoraclehive.gui.listeners.DataChangeListener;
import br.gov.df.economia.sistemaauditoriaoraclehive.model.entities.Agenda;
import br.gov.df.economia.sistemaauditoriaoraclehive.model.services.AgendaService;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

// Controlador para a tela de listagem de agendas
public class AgendaListController implements Initializable, DataChangeListener {

	@FXML private Label tituloTableViews;
    /**
	 * Controlador para a lista de agendas.
	 */
	private AgendaService service; // Serviço que manipula as agendas

	@FXML
	private TableView<Agenda> tableViewAgenda; // Tabela que exibe as agendas

	@FXML
	private TableColumn<Agenda, Long> tableColumnCodigo; // Coluna do código

	// Outras colunas da tabela
	@FXML
	private TableColumn<Agenda, String> tableColumnTipo;
	@FXML
	private TableColumn<Agenda, String> tableColumnInicio;
	@FXML
	private TableColumn<Agenda, String> tableColumnFim;
	@FXML
	private TableColumn<Agenda, String> tableColumnSituacao;
	@FXML
	private TableColumn<Agenda, String> tableColumnArquivo;
	@FXML
	private TableColumn<Agenda, String> tableColumnQuantidade;
	@FXML
	private ComboBox<TipoDoc> comboTipoDoc; // ComboBox para selecionar o tipo de documento

	// Colunas de botões para edição e remoção
	@FXML
	private TableColumn<Agenda, Agenda> tableColumnEDIT;
	@FXML
	private TableColumn<Agenda, Agenda> tableColumnREMOVE;

	// Outros elementos de interface do usuário
	@FXML
	private TextField txtCodAgenda;
	@FXML
	private TextField txtDias;
	@FXML
	private Label txtTotalArquivo;
	@FXML
	private Label txtComDados;
	@FXML
	private Label txtQuantidadeProcessado;
	@FXML
	private Button btNew; // Botão para adicionar nova agenda
	@FXML
	private Button btFechar; // Botão para fechar a aplicação

	private ObservableList<Agenda> obsList; // Lista observável para a tabela
	@FXML
	private PieChart pieChart;
	@FXML
	private LineChart<String, Number> lineChart;
	private ChartsUtils chartsUtils;
	@FXML
	private List<Agenda> originalAgendaList; // Lista original de agendas

	/**
	 * Chamado quando o botão "New" é acionado para adicionar uma nova agenda.
	 */
	@FXML
	public void onBtNewAction(ActionEvent event) {
		// Obtém a referência à janela pai
		Stage parentStage = Utils.currentStage(event);
		// Cria uma nova agenda
		Agenda obj = new Agenda();
		// Chama o método para exibir o formulário de diálogo
		createDialogForm(obj, "/Fxml/AgendaForm.fxml", parentStage);
		// Após adicionar um novo item, atualiza o valor do Label
		Utils.calculateAndSetTotalLines();
		// Após adicionar um novo item, atualiza o valor do Label com dados
		Utils.calculateAndSetTotalLinesComDados();
	}

	/**
	 * Define o serviço de agenda.
	 *
	 * @param service  Serviço de agenda a ser definido.
	 */
	public void setAgendaService(AgendaService service) {
		this.service = service;
	}

	/**
	 * Atualiza o ComboBox de acordo com o tipo de documento selecionado.
	 *
	 * @param tipoDoc  Tipo de documento selecionado.
	 */
	public void updateComboBox(TipoDoc tipoDoc) {
		// Atualize o ComboBox na classe AgendaListController conforme necessário
		System.out.println("AgendaListController atualizado para o Tipo de Documento: " + tipoDoc);
		// Adicione aqui a lógica para lidar com os resultados conforme a seleção do ComboBox
		// Exemplo: atualize uma tabela, chame outros métodos, etc.
		System.out.println("Processando resultados para TipoDoc: " + tipoDoc);
		updateCharts(filterAgendaListByTipo(tipoDoc));
	}

	/**
	 * Inicialização do controlador.
	 */
	@Override
	public void initialize(URL url, ResourceBundle rb) {
		chartsUtils = new ChartsUtils();
		ChartsUtils.setPieChart(pieChart);
		ChartsUtils.setLineChart(lineChart);
		initializeNodes(); // Inicialização dos nós da interface gráfica
		Configuracao config = new Configuracao(); // Instanciação da classe de configurações gerais (supondo que Configuracao seja uma classe válida)
		// Define o valor padrão para o campo de dias
		txtDias.setText(String.valueOf(Configuracao.dias)); // Configura o campo de texto com o valor padrão de dias obtido da classe de configurações
		comboTipoDoc.setItems(FXCollections.observableArrayList(TipoDoc.values())); // Preenche o ComboBox com os valores do enum TipoDoc
		// Adiciona um ouvinte para detectar alterações na seleção do ComboBox e atualizar o conteúdo filtrado e os rótulos
		comboTipoDoc.valueProperty().addListener((obs, oldVal, newVal) -> updateFilteredContentAndLabels());
		comboTipoDoc.valueProperty().addListener((observable, oldValue, newValue) -> {
			// O código dentro deste bloco será executado quando o valor do comboTipoDoc for alterado
			updateFilteredContent(); // Atualiza a tabela com base no tipo de documento selecionado
			updateQuantidadeProcessado(); // Atualiza o valor do txtQuantidadeProcessado
		});
	}
	/**
	 * Atualiza o conteúdo filtrado com base no tipo de documento selecionado.
	 */
	private void updateFilteredContent() {
		TipoDoc tipoDoc = comboTipoDoc.getValue(); // Obtém o tipo de documento selecionado no ComboBox
		List<Agenda> filteredList = filterAgendaListByTipo(tipoDoc); // Filtra a lista de agendas com base no tipo de documento
		obsList.setAll(filteredList); // Atualiza a lista observável com a lista filtrada
		updateCharts(filteredList); // Atualiza os gráficos com a lista filtrada
		calculateAndSetLabels(); // Calcula e define os rótulos (labels) na interface gráfica
	}

	/**
	 * Calcula e define os rótulos (labels) na interface gráfica.
	 */
	/**
	 * Este método calcula e define os rótulos (labels) na interface gráfica.
	 */
	private void calculateAndSetLabels() {
		// Define o formato desejado para números, com separadores de milhar
		DecimalFormat decimalFormat = new DecimalFormat("#,###");

		// Calcula a soma da quantidade de todos os itens da lista observável 'obsList'
		// Filtra itens que têm quantidade não nula, mapeia para suas quantidades e reduz (soma) os valores
		BigDecimal sumQuantity = obsList.stream()
				.filter(item -> item.getQuantidade() != null)
				.map(Agenda::getQuantidade)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		// Define o texto do rótulo 'txtTotalArquivo' com o tamanho da lista formatado
		txtTotalArquivo.setText(decimalFormat.format(obsList.size()));

		// Conta o número de itens na lista que têm quantidade não nula e maior que zero
		long totalLinesComDados = obsList.stream()
				.filter(item -> item.getQuantidade() != null && item.getQuantidade().compareTo(BigDecimal.ZERO) > 0)
				.count();

		// Define o texto do rótulo 'txtComDados' com o número de itens com dados formatado
		txtComDados.setText(decimalFormat.format(totalLinesComDados));

		// Define o texto do rótulo 'txtQuantidadeProcessado' com a soma das quantidades formatada
		txtQuantidadeProcessado.setText(decimalFormat.format(sumQuantity));
	}

	/**
	 * Este método atualiza o conteúdo filtrado da tabela e os rótulos associados.
	 * É chamado ao alterar o valor do ComboBox ou ao perder o foco do TextField.
	 */
	private void updateFilteredContentAndLabels() {
		try {
			// Verifica se o campo txtDias contém um valor numérico válido
			int dias = Integer.parseInt(txtDias.getText());

			// Obtém o tipo de documento selecionado no ComboBox
			TipoDoc tipoDoc = comboTipoDoc.getValue();

			// Filtra a lista de agendas com base no tipo de documento selecionado
			List<Agenda> filteredList = filterAgendaListByTipo(tipoDoc);

			// Atualiza a lista observável 'obsList' com a lista filtrada
			obsList.setAll(filteredList);

			// Atualiza os gráficos com a lista filtrada
			updateCharts(filteredList);

			// Calcula e define os rótulos (labels) na interface gráfica com base na lista filtrada
			calculateAndSetLabels();
		} catch (NumberFormatException e) {
			// Lida com a exceção se o valor em txtDias não for um número inteiro válido
			Alerts.showAlert("Erro", null, "Por favor, inserir um valor válido para dias.", AlertType.ERROR);
		}
	}

	/**
	 * Este método filtra a lista de agendas com base no tipo de documento selecionado.
	 * @param tipo O tipo de documento pelo qual filtrar a lista.
	 * @return Uma lista de agendas filtradas.
	 */
	private List<Agenda> filterAgendaListByTipo(TipoDoc tipo) {
		// Filtra a lista original de agendas, retornando apenas os itens que correspondem ao tipo de documento selecionado
		return originalAgendaList.stream()
				.filter(agenda -> agenda.getTipo_doc() == tipo)
				.collect(Collectors.toList());
	}


	/**
	 * Inicializa os nós da tabela.
	 */
	private void initializeNodes() {
		TableViewManager.popularTabelaAgenda(tableColumnCodigo, tableColumnTipo, tableColumnInicio, tableColumnFim, tableColumnArquivo, tableColumnQuantidade, tableColumnSituacao); // Chamando o método da classe ManagerTable
		Stage stage = (Stage) Main.getMainScene().getWindow(); // Obtém a referência à janela principal
		tableViewAgenda.prefHeightProperty().bind(stage.heightProperty()); // Ajusta a altura da tabela
		initEditAndRemoveButtons(); // Inicializa os botões de edição e remoção
	}
	/**
	 * Inicializa os botões de edição e remoção na tabela.
	 */
	private void initEditAndRemoveButtons() {
		// Inicialização dos botões de edição
		tableColumnEDIT.setCellFactory(param -> new TableCell<Agenda, Agenda>() {
			private final Button btn = new Button("Edit");

			@Override
			protected void updateItem(Agenda item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) {
					setGraphic(null); // Se a célula estiver vazia, limpa o conteúdo gráfico
					return;
				}

				setGraphic(btn); // Define o botão gráfico na célula
				btn.setOnAction(event -> createDialogForm(item, "/Fxml/AgendaForm.fxml", Utils.currentStage(event))); // Configura a ação do botão para abrir o formulário de edição
			}
		});

		// Inicialização dos botões de remoção
		tableColumnREMOVE.setCellFactory(param -> new TableCell<Agenda, Agenda>() {
			private final Button btn = new Button("Remove");

			@Override
			protected void updateItem(Agenda item, boolean empty) {
				super.updateItem(item, empty);

				if (empty || item == null) {
					setGraphic(null); // Se a célula estiver vazia, limpa o conteúdo gráfico
					return;
				}

				setGraphic(btn); // Define o botão gráfico na célula
				btn.setOnAction(event -> removeEntity(item)); // Configura a ação do botão para remover a entidade
			}
		});
	}
	/**
	 * Atualiza a tabela de agendas.
	 */
	public void updateTableView() {
		if (service != null) {
			originalAgendaList = service.findAll();
			obsList = FXCollections.observableArrayList(originalAgendaList);
			tableViewAgenda.setItems(obsList);

			// Adiciona o observador aqui
			obsList.addListener((ListChangeListener<Agenda>) change -> calculateAndSetLabels());

			tableViewAgenda.setItems(obsList);

			// Inicializa os botões de edição e remoção
			initEditButtons();
			initRemoveButtons();

			// Calcula e define o total de linhas
			Utils.calculateAndSetTotalLines();

			// Calcula e define o total de linhas com dados
			Utils.calculateAndSetTotalLinesComDados();

			// Calcula a soma da quantidade e a define no txtQuantidadeProcessado
			BigDecimal somaQuantidade = calcularSomaQuantidade();
			txtQuantidadeProcessado.setText(String.valueOf(somaQuantidade));

			// Atualiza os gráficos
			updateCharts(originalAgendaList);

			// Calcula e define os rótulos (labels) na interface gráfica
			calculateAndSetLabels();

			// Salva todos os dados em um único arquivo
			CSVUtils.saveAllDataToFile(originalAgendaList);
		}
	}
	/**
	 * Cria um formulário de diálogo para adicionar ou editar uma agenda.
	 *
	 * @param obj           Agenda a ser editada ou adicionada.
	 * @param absoluteName  Nome absoluto do arquivo FXML do formulário.
	 * @param parentStage   Janela pai.
	 */
	private void createDialogForm(Agenda obj, String absoluteName, Stage parentStage) {
		try {
			// Carrega o arquivo FXML do formulário
			FXMLLoader loader = new FXMLLoader(getClass().getResource(absoluteName));
			// Carrega a pane do formulário
			Pane pane = loader.load();

			// Obtém o controlador do formulário
			AgendaFormController controller = loader.getController();
			// Seta a agenda no controlador
			controller.setAgenda(obj);
			// Seta o serviço de agenda no controlador
			controller.setAgendaService(new AgendaService());
			// Registra o listener de alteração de dados no controlador
			controller.subscribeDataChangeListener(this);
			// Atualiza os dados do formulário
			controller.updateFormData();

			// Cria uma nova janela para exibir o formulário
			Stage dialogStage = new Stage();
			dialogStage.setTitle("Enter Agenda data");
			dialogStage.setScene(new Scene(pane));
			dialogStage.setResizable(false);
			dialogStage.initOwner(parentStage);
			dialogStage.initModality(Modality.WINDOW_MODAL);
			dialogStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
			// Exibe um alerta em caso de erro ao carregar a view
			Alerts.showAlert("IO Exception", "Error loading view", e.getMessage(), AlertType.ERROR);
		}
	}

	/**
	 * Método chamado quando o botão de pesquisa por código é acionado.
	 */
	@FXML
	public void handleSearch(ActionEvent event) {
		try {
			// Obtém o código da agenda a ser pesquisada
			Long codAgenda = Long.valueOf(txtCodAgenda.getText());
			// Encontra a agenda pelo código
			Agenda agenda = service.findByCodAgenda(codAgenda);
			// Limpa a lista de dados da tabela
			tableViewAgenda.getItems().clear();

			// Verifica se a agenda foi encontrada
			if (agenda != null) {
				// Adiciona o resultado da pesquisa à lista de itens da tabela
				tableViewAgenda.getItems().add(agenda);
			} else {
				// A agenda não foi encontrada, exibe uma mensagem ao usuário
				System.out.println("Agenda não encontrada para o código: " + codAgenda);
			}

		} catch (NumberFormatException e) {
			// Lida com a exceção se o texto não for um número
			// (pode ser substituído por uma lógica apropriada)
			e.printStackTrace();
		}
	}

	/**
	 * Método chamado quando o botão de pesquisa por tipo de documento é acionado.
	 */
	@FXML
	public void PesquisarTipoDoc(ActionEvent event) {
		try {
			// Obtém o tipo de documento selecionado no ComboBox
			TipoDoc tipoDoc = comboTipoDoc.getValue();
			// Obtém o número de dias do campo txtDias como um inteiro
			int dias = Integer.parseInt(txtDias.getText());

			// Chame o método pesquisarTipoDoc da AgendaUtil passando o Label
			ComboBoxUtil.pesquisarTipoDoc(service, tipoDoc, dias, tableViewAgenda, txtTotalArquivo, txtComDados);

		} catch (NumberFormatException e) {
			// Lida com a exceção se o valor em txtDias não for um número inteiro válido
			Alerts.showAlert("Error", null, "Please enter a valid number for days.", AlertType.ERROR);
		}
	}

	/**
	 * Chamado quando os dados são alterados.
	 */
	@Override
	public void onDataChanged() {
		// Atualiza a tabela ao detectar alterações nos dados
		updateTableView();
	}

	/**
	 * Inicializa os botões de edição.
	 */
	private void initEditButtons() {
		// Define a fábrica de células para a coluna de edição
		tableColumnEDIT.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));

		tableColumnEDIT.setCellFactory(param -> new TableCell<Agenda, Agenda>() {
			private final Button button = new Button("edit");

			@Override
			protected void updateItem(Agenda obj, boolean empty) {
				super.updateItem(obj, empty);
				// Verifica se o objeto não é nulo e está agendado para edição
				if (obj == null || obj.getInd_situacao() != SituacaoProcessamento.AGENDADO) {
					setGraphic(null);
					return;
				}
				// Configura o botão e ação para abrir o formulário de edição
				setGraphic(button);
				button.setOnAction(event -> createDialogForm(obj, "/Fxml/AgendaForm.fxml", Utils.currentStage(event)));
			}
		});

		// Define a largura da coluna
		tableColumnEDIT.setPrefWidth(60);
	}
	/**
	 * Fecha a aplicação.
	 */
	@FXML
	private void fecharAplicacao(ActionEvent event) {
		Platform.exit();
	}

	/**
	 * Atualiza os gráficos com base na lista de agendas fornecida.
	 *
	 * @param agendaList Lista de agendas para atualizar os gráficos.
	 */
	private void updateCharts(List<Agenda> agendaList) {
		ChartsUtils.updateLineChart(agendaList);
		ChartsUtils.updatePieChart(agendaList);
	}
	/**
	 * Exibe um diálogo de confirmação e remove a agenda se o usuário confirmar.
	 *
	 * @param obj  Agenda a ser removida.
	 */
	private void removeEntity(Agenda obj) {
		Alerts.showConfirmation("Confirmation", "Are you sure to delete?")
				.ifPresent(result -> {
					if (result == ButtonType.OK) {
						// Remove a agenda do serviço
						removeAgenda(obj);
					}
				});
	}
	/**
	 * Remove uma agenda do serviço e atualiza a tabela após a remoção.
	 *
	 * @param obj  Agenda a ser removida.
	 */
	private void removeAgenda(Agenda obj) {
		// Verifica se o serviço está inicializado
		if (service == null) {
			throw new IllegalStateException("Service was null");
		}
		try {
			// Remove a agenda do serviço
			service.remove(obj);
			// Atualiza a tabela após a remoção
			updateTableView();
		} catch (DatabaseIntegrityExceptions.DbIntegrityException e) {
			// Exibe um alerta em caso de erro na remoção
			Alerts.showAlert("Error removing object", null, e.getMessage(), AlertType.ERROR);
		}
	}
	/**
	 * Inicializa os botões de remoção na tabela.
	 * Define a fábrica de células, botão e ação para remover uma agenda agendada.
	 */
	private void initRemoveButtons() {
		tableColumnREMOVE.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));

		tableColumnREMOVE.setCellFactory(param -> new TableCell<Agenda, Agenda>() {
			private final Button button = new Button("remove");

			@Override
			protected void updateItem(Agenda obj, boolean empty) {
				super.updateItem(obj, empty);

				// Verifica se o objeto não é nulo e está agendado para remoção
				if (obj == null || obj.getInd_situacao() != SituacaoProcessamento.AGENDADO) {
					setGraphic(null);
					return;
				}

				// Configura o botão e a ação para remover a agenda
				setGraphic(button);
				button.setOnAction(event -> removeEntity(obj));
			}
		});

		// Define a largura da coluna
		tableColumnREMOVE.setPrefWidth(120);
	}

	/**
	 * Seleciona o tipo de documento no comboTipoDoc.
	 *
	 * @param tipoDoc  Tipo de documento a ser selecionado.
	 */
	public void selecionarTipoDoc(TipoDoc tipoDoc) {
		comboTipoDoc.setValue(tipoDoc);
	}

	private BigDecimal calcularSomaQuantidade() {
		return tableViewAgenda.getItems().stream()
				.map(Agenda::getQuantidade)
				.filter(Objects::nonNull)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
	}
	private void updateQuantidadeProcessado() {
		BigDecimal sumQuantity = obsList.stream()
				.filter(item -> item.getQuantidade() != null)
				.map(Agenda::getQuantidade)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		DecimalFormat decimalFormat = new DecimalFormat("#,###");
		txtQuantidadeProcessado.setText(decimalFormat.format(sumQuantity));
	}
}