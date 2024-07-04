package br.gov.df.economia.sistemaauditoriaoraclehive.model.entities;

public class OracleHive {

    private String arquivo;
    private String tipoDoc;
    private String tabelaOracle;
    private String tabelaHive;
    private int totalOracle;
    private int totalHive;
    private int diferenca;  // Novo atributo para armazenar a diferença

    public OracleHive() {
        // Pode inicializar atributos com valores padrão, se necessário
    }


    public OracleHive(String arquivo, String tipoDoc, String tabelaOracle, String tabelaHive, int totalOracle, int totalHive, int diferenca) {
        this.arquivo = arquivo;
        this.tipoDoc = tipoDoc;
        this.tabelaOracle = tabelaOracle;
        this.tabelaHive = tabelaHive;
        this.totalOracle = totalOracle;
        this.totalHive = totalHive;
        this.diferenca = diferenca;
    }


    public String getArquivo() {
        return arquivo;
    }

    public void setArquivo(String arquivo) {
        this.arquivo = arquivo;
    }

    public String getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    public String getTabelaOracle() {
        return tabelaOracle;
    }

    public void setTabelaOracle(String tabelaOracle) {
        this.tabelaOracle = tabelaOracle;
    }

    public String getTabelaHive() {
        return tabelaHive;
    }

    public void setTabelaHive(String tabelaHive) {
        this.tabelaHive = tabelaHive;
    }

    public int getTotalOracle() {
        return totalOracle;
    }

    public void setTotalOracle(int totalOracle) {
        this.totalOracle = totalOracle;
    }

    public int getTotalHive() {
        return totalHive;
    }

    public void setTotalHive(int totalHive) {
        this.totalHive = totalHive;
    }

    public int getDiferenca() {
        return diferenca;
    }

    public void setDiferenca(int diferenca) {
        this.diferenca = diferenca;
    }

    // Método para calcular e definir o valor da diferença
    public void calcularDiferenca() {
        if ("tb_nfe_evento".equals(tabelaHive)) {
            if (tabelaOracle != null && !tabelaOracle.isEmpty()) {
                double percentual = (double) totalHive / totalOracle;
                if (percentual >= 0.99) {
                    this.diferenca = 0;
                } else {
                    this.diferenca = totalOracle - totalHive;
                }
            } else {
                // Se tabelaOracle for nula ou vazia, usa o valor de tabelaHive
                this.diferenca = totalHive;
            }
        } else {
            // Quando tipoDoc não for igual a "NFeEvento", calcula a diferença normalmente
            this.diferenca = totalOracle - totalHive;
        }
    }



    @Override
    public String toString() {
        return "OracleHiveInfo{" +
                "Arquivo=" + arquivo +
                "Tipo de Documento=" + tipoDoc +
                "Tabela Oracle=" + tabelaOracle +
                "Tabela Hive=" + tabelaHive +
                "Total Oracle=" + totalOracle +
                "Total Hive=" + totalHive +
                "Diferença=" + diferenca + '\'' +
                '}';
    }
}
