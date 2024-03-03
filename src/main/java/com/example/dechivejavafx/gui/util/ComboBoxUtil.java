package com.example.dechivejavafx.gui.util;

import com.example.dechivejavafx.Validacoes.TipoDoc;
import com.example.dechivejavafx.model.entities.Agenda;
import com.example.dechivejavafx.model.services.AgendaService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ComboBoxUtil {
    private static final Logger LOGGER = Logger.getLogger(ComboBoxUtil.class.getName());
    private static final String DATE_FORMAT = "yyyyMMdd";

    private ComboBoxUtil() {
        // Prevent instantiation
    }

    public interface OnItemSelectedListener<T> {
        void onItemSelected(T selectedItem);
    }

    public static <T> void adicionarEventListeners(ComboBox<T> comboBox, OnItemSelectedListener<T> listener) {
        comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                listener.onItemSelected(newValue);
            }
        });
    }

    public static void pesquisarTipoDoc(AgendaService service, TipoDoc tipoDoc, int dias,
                                        TableView<Agenda> tableView, Label txtTotalArquivo, Label txtComDados) {
        try {
            List<Agenda> agendas = service.findAllByTipoDoc(tipoDoc, dias);
            tableView.getItems().clear();

            if (!agendas.isEmpty()) {
                tableView.getItems().addAll(agendas);
            } else {
                LOGGER.log(Level.INFO, "Nenhuma agenda encontrada para o tipo de documento: {0}", tipoDoc);
            }

            calculateAndSetTotalLines(tableView, txtTotalArquivo);
            calculateAndSetTotalLinesComDados(tableView, txtComDados);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao pesquisar tipo de documento", e);
        }
    }

    private static void calculateAndSetTotalLines(TableView<Agenda> tableView, Label txtTotalArquivo) {
        int totalLines = tableView.getItems().size();
        txtTotalArquivo.setText(String.valueOf(totalLines));
    }

    private static void calculateAndSetTotalLinesComDados(TableView<Agenda> tableView, Label txtComDados) {
        long totalLinesComDados = tableView.getItems().stream()
                .filter(agenda -> agenda.getQuantidade() != null && agenda.getQuantidade().compareTo(BigDecimal.ZERO) > 0)
                .count();
        txtComDados.setText(String.valueOf(totalLinesComDados));
    }

    public static void initializeComboBoxTipoDoc(ComboBox<TipoDoc> comboBox, List<TipoDoc> tipoDocs) {
        ObservableList<TipoDoc> tabelaOptionsAtivas = FXCollections.observableArrayList(
                tipoDocs.stream()
                        .filter(tipoDoc -> "sim".equalsIgnoreCase(tipoDoc.getAtivo()))
                        .collect(Collectors.toList())
        );

        comboBox.setItems(tabelaOptionsAtivas);
    }
    public static void initializeComboBoxData(ComboBox<String> comboBox, int dias) {
        ObservableList<String> datas = FXCollections.observableArrayList();
        LocalDate dataAtual = LocalDate.now();

        for (int i = 0; i <= dias; i++) {
            LocalDate dataSubtraida = dataAtual.minusDays(i);
            datas.add(dataSubtraida.format(DateTimeFormatter.ofPattern(DATE_FORMAT)));
        }
        comboBox.setItems(datas);
        comboBox.getSelectionModel().selectLast();
    }
}
