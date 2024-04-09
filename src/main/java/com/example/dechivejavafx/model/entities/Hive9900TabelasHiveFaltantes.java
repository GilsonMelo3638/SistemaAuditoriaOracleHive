package com.example.dechivejavafx.model.entities;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class Hive9900TabelasHiveFaltantes {
    private BigInteger idBase;
    private LocalDateTime dhProcessamento;
    private Integer statusProcessamento;
    private Integer quantidadeRegBloco;
    private String registroBloco;

    public Hive9900TabelasHiveFaltantes() {
        // Pode inicializar atributos com valores padrão, se necessário
    }

    public Hive9900TabelasHiveFaltantes(BigInteger idBase, LocalDateTime dhProcessamento, Integer statusProcessamento, String registroBloco, Integer quantidadeRegBloco) {
        this.idBase = idBase;
        this.dhProcessamento = dhProcessamento;
        this.statusProcessamento = statusProcessamento;
        this.registroBloco = registroBloco;
        this.quantidadeRegBloco = quantidadeRegBloco;
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

    public String getRegistroBloco() {
        return registroBloco;
    }
    public void setRegistroBloco(String registroBloco) {
        this.registroBloco = registroBloco;
    }

    public Integer getQuantidadeRegBloco() {
        return quantidadeRegBloco;
    }
    public void setQuantidadeRegBloco(Integer quantidadeRegBloco) {
        this.quantidadeRegBloco = quantidadeRegBloco;
    }

    @Override
    public String toString() {
        return "NFeInfo{" +
                "idBase=" + idBase +
                ", dhProcessamento='" + dhProcessamento +
                ", statusProcessamento='" + statusProcessamento + '\'' +
                ", registroBloco='" + registroBloco + '\'' +
                ", quantidadeRegBloco='" + quantidadeRegBloco + '\'' +
                '}';
    }
}