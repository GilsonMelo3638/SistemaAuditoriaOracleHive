package com.example.dechivejavafx.model.services;

import com.example.dechivejavafx.gui.MainViewController;
import com.example.dechivejavafx.gui.util.Configuracao;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;

public class SchedulerManager {

    private ScheduledExecutorService scheduler;
    private MainViewController mainViewController;

    public SchedulerManager(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    public void ProcessarDuplicidadeId() {
        // Implementar lógica para agendar o processamento de duplicidade de IDs
        mainViewController.onMenuProcessarDuplicidadeIdAction();
    }

    public void iniciarAgendamento() {
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime proximaExecucao = agora.withMinute(Configuracao.minutoInicial).withSecond(0).withNano(0);

        if (agora.getMinute() >= Configuracao.minutoInicial) {
            proximaExecucao = proximaExecucao.plusHours((Configuracao.intervaloHora - agora.getHour() % Configuracao.intervaloHora) % Configuracao.intervaloHora);
        }

        long atrasoInicial = ChronoUnit.MILLIS.between(agora, proximaExecucao);
        long periodo = TimeUnit.HOURS.toMillis(Configuracao.intervaloHora);

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::executarTarefa, atrasoInicial, periodo, TimeUnit.MILLISECONDS);
    }

    public void pararAgendamento() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    private void executarTarefa() {
        try {
            // Lógica da tarefa a ser executada
            Platform.runLater(() -> {
                mainViewController.onMenuProcessarPendenciaAction();
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void agendarProcessamentoDuplicidadeId() {
        // Define o horário de início para o próximo domingo às 00:30 horas
        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime proximoDomingo = agora.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).withHour(0).withMinute(30).withSecond(0).withNano(0);

        // Calcula o atraso inicial até o próximo domingo às 00:30 horas
        long atrasoInicial = ChronoUnit.MILLIS.between(agora, proximoDomingo);

        // Agenda a tarefa para ser executada todo domingo às 00:30 horas
        scheduler.scheduleAtFixedRate(this::executarProcessamentoDuplicidadeId, atrasoInicial, TimeUnit.DAYS.toMillis(7), TimeUnit.MILLISECONDS);
    }
    private void executarProcessamentoDuplicidadeId() {
        try {
            // Lógica para processar duplicidade de IDs
            Platform.runLater(() -> {
                mainViewController.onMenuProcessarDuplicidadeIdAction(); // Chama o método ProcessarDuplicidadeId do MainViewController
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
