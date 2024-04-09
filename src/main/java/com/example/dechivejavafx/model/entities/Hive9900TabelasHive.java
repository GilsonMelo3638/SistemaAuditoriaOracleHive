package com.example.dechivejavafx.model.entities;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class Hive9900TabelasHive {
    private BigInteger idBase;
    private LocalDateTime dhProcessamento;
    private LocalDateTime dataHoraFin;
    private Integer statusProcessamento;
    private String registro;
    private String registroBloco;

    public Hive9900TabelasHive() {
        // Pode inicializar atributos com valores padrão, se necessário
    }

    public Hive9900TabelasHive(BigInteger idBase, LocalDateTime dhProcessamento, Integer statusProcessamento, LocalDateTime dataHoraFin, String registro, String registroBloco ) {
        this.idBase = idBase;
        this.dataHoraFin = dataHoraFin;
        this.statusProcessamento = statusProcessamento;
        this.dhProcessamento = dhProcessamento;
        this.registro = registro;
        this.registroBloco = registroBloco;
    }

    public BigInteger getIdBase() {
        return idBase;
    }
    public void setIdBase(BigInteger idBase) {
        this.idBase = idBase;
    }
    public LocalDateTime getDhProcessamento() {
        return dhProcessamento;
    }
    public void setDhProcessamento(LocalDateTime dhProcessamento) {
        this.dhProcessamento = dhProcessamento;
    }
    public Integer getStatusProcessamento() {
        return statusProcessamento;
    }
    public void setStatusProcessamento(Integer statusProcessamento) {
        this.statusProcessamento = statusProcessamento;
    }
    public LocalDateTime getDataHoraFin() {
        return dataHoraFin;
    }
    public void setDataHoraFin(LocalDateTime dataHoraFin) {
        this.dataHoraFin = dataHoraFin;
    }

    public String getRegistro() {
        return registro;
    }
    public void setRegistro(String registro) {
        this.registro = registro;
    }
    public String getRegistroBloco() {
        return registroBloco;
    }
    public void setRegistroBloco(String registroBloco) {
        this.registroBloco = registroBloco;
    }

    @Override
    public String toString() {
        return "NFeInfo{" +
                "idBase=" + idBase +
                ", dhProcessamento='" + dhProcessamento + '\'' +
                ", statusProcessamento='" + statusProcessamento + '\'' +
                ", dataHoraFin='" + dataHoraFin + '\'' +
                ", registro='" + registro + '\'' +
                ", registroBloco='" + registroBloco + '\'' +
                '}';
    }
}