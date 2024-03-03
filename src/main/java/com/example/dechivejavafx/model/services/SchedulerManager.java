package com.example.dechivejavafx.model.services;

import com.example.dechivejavafx.gui.MainViewController;
import com.example.dechivejavafx.gui.util.Configuracao;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
}

