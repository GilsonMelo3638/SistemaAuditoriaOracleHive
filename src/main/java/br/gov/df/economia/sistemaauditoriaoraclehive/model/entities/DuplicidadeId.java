package br.gov.df.economia.sistemaauditoriaoraclehive.model.entities;

public class DuplicidadeId {

    private String arquivo;
    private String tabela;
    public DuplicidadeId() {
        // Pode inicializar atributos com valores padrão, se necessário
    }
    public DuplicidadeId(String arquivo, String tabela) {
        this.arquivo = arquivo;
        this.tabela = tabela;
    }

    public String getArquivo() {
        return arquivo;
    }
    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public String getTabela() {
        return tabela;
    }
    public void setTabela(String tabela) {
        this.arquivo = tabela;
    }

    @Override
    public String toString() {
        return "NFeInfo{" +
                "Arquivo=" + arquivo +
                ", Tabela ='" + tabela + '\'' +
                '}';
    }
}