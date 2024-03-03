package com.example.dechivejavafx.model.entities;

public class DetNFeNFCeInf {

    private String arquivo;
    private String tabelaDetalhe;
    private int quantidadeNsuchave;

    public DetNFeNFCeInf() {
        // Pode inicializar atributos com valores padrão, se necessário
    }

    public DetNFeNFCeInf(String arquivo, String tabelaDetalhe , int quantidadeNsuchave) {
        this.arquivo = arquivo;
        this.tabelaDetalhe = tabelaDetalhe;
        this.quantidadeNsuchave = quantidadeNsuchave;
    }

    public String getArquivo() {
        return arquivo;
    }
    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public String getTabelaDetalhe() {
        return tabelaDetalhe;
    }
    public void setTabelaDetalhe(String tabelaDetalhe) {
        this.arquivo = tabelaDetalhe;
    }

    public int getQuantidadeNsuchave() {
        return quantidadeNsuchave;
    }
    public void setQuantidadeNsuchave(int quantidadeNsuchave) {
        this.quantidadeNsuchave = quantidadeNsuchave;
    }

    @Override
    public String toString() {
        return "NFeInfo{" +
                "totalNFe=" + arquivo +
                ", uf='" + tabelaDetalhe + '\'' +
                ", arquivo='" + quantidadeNsuchave + '\'' +
                '}';
    }
}