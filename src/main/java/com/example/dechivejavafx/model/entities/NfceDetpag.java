package com.example.dechivejavafx.model.entities;

public class NfceDetpag {
    private String arquivo;
    private int totalInfNfce;
    private String detpag;

    public NfceDetpag(String arquivo, int totalInfNfce, String detpag) {
        this.arquivo = arquivo;
        this.totalInfNfce = totalInfNfce;
        this.detpag = detpag;
    }

    // Getters e setters
    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public int getTotalInfNfce() {
        return totalInfNfce;
    }

    public void setTotalInfNfce(int totalInfNfce) {
        this.totalInfNfce = totalInfNfce;
    }

    public String getDetpag() {
        return detpag;
    }

    public void setDetpag(String detpag) {
        this.detpag = detpag;
    }
}
