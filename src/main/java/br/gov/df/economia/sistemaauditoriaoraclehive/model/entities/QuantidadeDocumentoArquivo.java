package br.gov.df.economia.sistemaauditoriaoraclehive.model.entities;

public class QuantidadeDocumentoArquivo {

    private String tabelaHive;
    private String arquivo;
    private String dia;
    private int total;

    public QuantidadeDocumentoArquivo() {
        // Pode inicializar atributos com valores padrão, se necessário
    }

    public QuantidadeDocumentoArquivo(String tabelaHive, String arquivo, String dia,  int total) {
        this.tabelaHive = tabelaHive;
        this.arquivo = arquivo;
        this.dia = dia;
        this.total = total;
    }

    public String getTabelaHive() {
        return tabelaHive;
    }
    public void setTabelaHive(String tabelaHive) {
        this.tabelaHive = tabelaHive;
    }
    public String getArquivo() {
        return arquivo;
    }
    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }
    public String getDia() {
        return dia;
    }
    public void setDia(String dia) {
        this.dia = dia;
    }
    public int getTotal() {
        return total;
    }
    public void setTotal(int total) {
        this.total = total;
    }
    @Override
    public String toString() {
        return "NFeInfo{" +
                "Tabela Hive=" +  tabelaHive +
                "Arquivo=" +  arquivo +
                ", Dia='" + dia + '\'' +
                ", Total=" + total  + '\'' +
                '}';
    }
}