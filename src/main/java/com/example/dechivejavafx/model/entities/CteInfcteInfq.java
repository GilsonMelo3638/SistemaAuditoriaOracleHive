package com.example.dechivejavafx.model.entities;

public class CteInfcteInfq {
    private String arquivo;
    private int totalInfCte;
    private String infnfe;
    private String infq;

    public CteInfcteInfq(String arquivo, int totalInfCte, String infnfe, String infq) {
        this.arquivo = arquivo;
        this.totalInfCte = totalInfCte;
        this.infnfe = infnfe;
        this.infq = infq;
    }

    // Getters e setters
    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public int getTotalInfCte() {
        return totalInfCte;
    }

    public void setTotalInfCte(int totalInfCte) {
        this.totalInfCte = totalInfCte;
    }

    public String getInfnfe() {
        return infnfe;
    }

    public void setInfnfe(String infnfe) {
        this.infnfe = infnfe;
    }

    public String getInfq() {
        return infq;
    }

    public void setInfq(String infq) {
        this.infq = infq;
    }
}
