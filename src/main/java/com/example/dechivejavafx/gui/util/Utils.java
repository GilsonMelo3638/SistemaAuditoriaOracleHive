package com.example.dechivejavafx.gui.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Predicate;

import com.example.dechivejavafx.model.entities.Agenda;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import javafx.util.StringConverter;

public class Utils {
    private static ObservableList<Agenda> obsList; // Lista observável para a tabela
    private static Label txtTotalArquivo;
    private static Label txtComDados;
    @FXML

    // Método que retorna o palco (Stage) atual com base em um evento (ActionEvent)
    public static Stage currentStage(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    // Tenta converter uma String em um Integer; retorna null em caso de falha.
    public static Long tryParseToInt(String str) {
        try {
            return (long) Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    /**
     * Calcula e define o total de linhas em um rótulo com base em uma condição.
     *
     * @param label      Rótulo onde o total de linhas será exibido.
     * @param condition  Condição para filtrar as agendas.
     */
    public static void calculateAndSetTotalLines(Label label, Predicate<Agenda> condition) {
        if (obsList != null) {
            // Conta o número total de linhas que atendem à condição
            long totalLines = obsList.stream().filter(condition).count();
            label.setText(String.valueOf(totalLines));
        }
    }
    /**
     * Calcula e define o total de linhas para o rótulo txtTotalArquivo.
     */
    public static void calculateAndSetTotalLines() {
        // Chama o método com a condição padrão (todas as agendas)
        calculateAndSetTotalLines(txtTotalArquivo, agenda -> true);
    }
    /**
     * Calcula e define o total de linhas com dados para o rótulo txtComDados.
     */
    public static void calculateAndSetTotalLinesComDados() {
        // Chama o método com a condição específica (quantidade maior que zero)
        calculateAndSetTotalLines(txtComDados, item -> {
            BigDecimal quantidade = item.getQuantidade();
            return quantidade != null && quantidade.compareTo(BigDecimal.ZERO) > 0;
        });
    }
    /**
     * Divide um conjunto de IDs em lotes de tamanho especificado.
     *
     * @param ids O conjunto de IDs a ser dividido em lotes.
     * @param batchSize O tamanho de cada lote.
     * @return Uma lista de listas de IDs divididos em lotes.
     */
    public static List<List<String>> partitionIdsIntoBatches(Set<String> ids, int batchSize) {
        // Lista para armazenar os lotes de IDs
        List<List<String>> batches = new ArrayList<>();

        // Lista temporária para armazenar IDs de um único lote
        List<String> currentBatch = new ArrayList<>();

        // Itera sobre cada ID no conjunto fornecido
        for (String id : ids) {
            // Adiciona o ID à lista do lote atual
            currentBatch.add(id);

            // Verifica se o tamanho do lote atual atingiu o tamanho máximo especificado
            if (currentBatch.size() == batchSize) {
                // Se o tamanho do lote atual atingiu o tamanho máximo, adiciona uma cópia dele à lista de lotes e limpa a lista do lote atual
                batches.add(new ArrayList<>(currentBatch));
                currentBatch.clear();
            }
        }

        // Verifica se ainda existem IDs na lista do lote atual após o loop
        if (!currentBatch.isEmpty()) {
            // Se houver IDs restantes na lista do lote atual, adiciona uma cópia dela à lista de lotes
            batches.add(new ArrayList<>(currentBatch));
        }

        // Retorna a lista de lotes de IDs
        return batches;
    }
}
