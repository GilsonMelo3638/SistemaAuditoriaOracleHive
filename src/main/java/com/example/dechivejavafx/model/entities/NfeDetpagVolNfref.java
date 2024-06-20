package com.example.dechivejavafx.model.entities;
public class NfeDetpagVolNfref {
    private String arquivo;
    private int totalInfNfe;
    private String detpag;
    private String vol;
    private String nfref;

    public NfeDetpagVolNfref(String arquivo, int totalInfNfe, String detpag, String vol, String nfref) {
        this.arquivo = arquivo;
        this.totalInfNfe = totalInfNfe;
        this.detpag = detpag;
        this.vol = vol;
        this.nfref = nfref;
    }

    // Getters e setters
    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public int getTotalInfNfe() {
        return totalInfNfe;
    }

    public void setTotalInfNfe(int totalInfNfe) {
        this.totalInfNfe = totalInfNfe;
    }

    public String getDetpag() {
        return detpag;
    }

    public void setDetpag(String detpag) {
        this.detpag = detpag;
    }

    public String getVol() {
        return vol;
    }

    public void setVol(String vol) {
        this.vol = vol;
    }

    public String getNfref() {
        return nfref;
    }

    public void setNfref(String nfref) {
        this.nfref = nfref;
    }
}
