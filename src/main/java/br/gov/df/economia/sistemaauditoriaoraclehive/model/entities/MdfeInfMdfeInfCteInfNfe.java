package br.gov.df.economia.sistemaauditoriaoraclehive.model.entities;
public class MdfeInfMdfeInfCteInfNfe {
    private String arquivo;
    private int totalInfMdfe;
    private String infcte;
    private String infnfe;

    public MdfeInfMdfeInfCteInfNfe(String arquivo, int totalInfMdfe, String infcte, String infnfe) {
        this.arquivo = arquivo;
        this.totalInfMdfe = totalInfMdfe;
        this.infcte = infcte;
        this.infnfe = infnfe;
    }

    // Getters e setters
    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public int getTotalInfMdfe() {
        return totalInfMdfe;
    }

    public void setTotalInfMdfe(int totalInfMdfe) {
        this.totalInfMdfe = totalInfMdfe;
    }

    public String getInfcte() {
        return infcte;
    }

    public void setInfcte(String infcte) {
        this.infcte = infcte;
    }

    public String getInfnfe() {
        return infnfe;
    }

    public void setInfnfe(String infnfe) {
        this.infnfe = infnfe;
    }
}

