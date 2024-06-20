package com.example.dechivejavafx.model.entities;

public class NfeItensAverbados {
    private String arquivo;
    private int totalItensAverbados;
    private String itensAverbados;

    public NfeItensAverbados(String arquivo, int totalItensAverbados, String itensAverbados) {
        this.arquivo = arquivo;
        this.totalItensAverbados = totalItensAverbados;
        this.itensAverbados = itensAverbados;
    }

    // Getters e setters
    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public int getTotalItensAverbados() {
        return totalItensAverbados;
    }

    public void setTotalItensAverbados(int totalItensAverbados) {
        this.totalItensAverbados = totalItensAverbados;
    }

    public String getItensAverbados() {
        return itensAverbados;
    }

    public void setItensAverbados(String itensAverbados) {
        this.itensAverbados = itensAverbados;
    }
}

