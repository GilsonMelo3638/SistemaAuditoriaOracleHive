package com.example.dechivejavafx.model.entities;
public class BpeComp {
    private String arquivo;
    private int totalInfBpe;
    private String comp;

    public BpeComp(String arquivo, int totalInfBpe, String comp) {
        this.arquivo = arquivo;
        this.totalInfBpe = totalInfBpe;
        this.comp = comp;
    }

    // Getters e setters
    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public int getTotalInfBpe() {
        return totalInfBpe;
    }

    public void setTotalInfBpe(int totalInfBpe) {
        this.totalInfBpe = totalInfBpe;
    }

    public String getComp() {
        return comp;
    }

    public void setComp(String comp) {
        this.comp = comp;
    }
}
