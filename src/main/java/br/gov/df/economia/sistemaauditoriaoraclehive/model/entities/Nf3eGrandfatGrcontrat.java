package br.gov.df.economia.sistemaauditoriaoraclehive.model.entities;

public class Nf3eGrandfatGrcontrat {
    private String arquivo;
    private int totalInfNf3e;
    private String ggrandfat;
    private String ggrcontrat;

    public Nf3eGrandfatGrcontrat(String arquivo, int totalInfNf3e, String ggrandfat, String ggrcontrat) {
        this.arquivo = arquivo;
        this.totalInfNf3e = totalInfNf3e;
        this.ggrandfat = ggrandfat;
        this.ggrcontrat = ggrcontrat;
    }

    // Getters e setters
    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public int getTotalInfNf3e() {
        return totalInfNf3e;
    }

    public void setTotalInfNf3e(int totalInfNf3e) {
        this.totalInfNf3e = totalInfNf3e;
    }

    public String getGgrandfat() {
        return ggrandfat;
    }

    public void setGgrandfat(String ggrandfat) {
        this.ggrandfat = ggrandfat;
    }

    public String getGgrcontrat() {
        return ggrcontrat;
    }

    public void setGgrcontrat(String ggrcontrat) {
        this.ggrcontrat = ggrcontrat;
    }

    // Método toString para representação textual do objeto
    @Override
    public String toString() {
        return "Nf3eGrandfatGrcontrat{" +
                "arquivo='" + arquivo + '\'' +
                ", totalInfNf3e=" + totalInfNf3e +
                ", ggrandfat='" + ggrandfat + '\'' +
                ", ggrcontrat='" + ggrcontrat + '\'' +
                '}';
    }
}
