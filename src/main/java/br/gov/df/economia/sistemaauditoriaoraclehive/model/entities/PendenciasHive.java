package br.gov.df.economia.sistemaauditoriaoraclehive.model.entities;

public class PendenciasHive {

    private String arquivo;
    private String tabelaDetalhe;
    private int quantidadeNsuchave;

    public PendenciasHive() {
        // Pode inicializar atributos com valores padrão, se necessário
    }

    public PendenciasHive(String arquivo, String tabelaDetalhe , int quantidadeNsuchave) {
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