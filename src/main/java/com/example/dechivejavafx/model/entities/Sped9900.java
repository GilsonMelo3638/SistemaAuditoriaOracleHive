package com.example.dechivejavafx.model.entities;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class Sped9900 {
    private BigInteger idBase;
    private LocalDateTime dhProcessamento;
    private Integer statusProcessamento;
    private Integer linha;
    private Integer quantidadeRegBloco;
    private String registro;
    private String registroBloco;

    public Sped9900() {
        // Pode inicializar atributos com valores padrão, se necessário
    }

    public Sped9900(BigInteger idBase, LocalDateTime dhProcessamento, Integer statusProcessamento, Integer linha, Integer quantidadeRegBloco, String registro, String registroBloco ) {
        this.idBase = idBase;
        this.dhProcessamento = dhProcessamento;
        this.statusProcessamento = statusProcessamento;
        this.linha = linha;
        this.quantidadeRegBloco = quantidadeRegBloco;
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
    public Integer getLinha() {
        return linha;
    }
    public void setLinha(Integer linha) {
        this.linha = linha;
    }
    public Integer getQuantidadeRegBloco() {
        return quantidadeRegBloco;
    }
    public void setQuantidadeRegBloco(Integer quantidadeRegBloco) {
        this.quantidadeRegBloco = quantidadeRegBloco;
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
                ", linha='" + linha + '\'' +
                ", quantidadeRegBloco='" + quantidadeRegBloco + '\'' +
                ", registro='" + registro + '\'' +
                ", registroBloco='" + registroBloco + '\'' +
                '}';
    }
}