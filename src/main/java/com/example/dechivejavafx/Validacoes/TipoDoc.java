package com.example.dechivejavafx.Validacoes;

public enum TipoDoc {
    BPe("BPe", "tb_bpe_infbpe", "diario", "sim"),
    BPeCanc("BPeCanc", "tb_bpe_cancelamento", "diario", "sim"),
    BPeEvento("BPeEvento", "tb_bpe_evento", "diario", "sim"),
    BPeInut("BPeInut", "", "diario", "nao"),
    CTe("CTe", "tb_cte_infcte", "hora", "sim"),
    CTeCanc("CTeCanc", "tb_cte_cancelamento", "diario", "sim"),
    CTeEvento("CTeEvento", "tb_cte_evento", "hora", "sim"),
    CTeInut("CTeInut", "", "diario", "nao"),
    CTeRFB("", "", "hora", "nao"),
    CTeRFBCanc("", "", "diario", "nao"),
    CTeRFBEvento("", "", "hora", "nao"),
    MDFe("MDFe", "tb_mdfe_infmdfe", "hora", "sim"),
    MDFeCanc("MDFeCanc", "tb_mdfe_cancelamento", "diario", "sim"),
    MDFeEvento("MDFeEvento", "tb_mdfe_evento", "hora", "sim"),
    MDFEInut("", "", "diario", "nao"),
    MDFeRFB("", "", "diario", "nao"),
    MDFeRFBCanc("", "", "diario", "nao"),
    MDFeRFBEvento("", "", "diario", "nao"),
    NFe("NFe", "tb_nfe_infnfe", "hora", "sim"),
    NFeCanc("NFeCanc", "tb_nfe_cancelamento", "diario", "sim"),
    NFeEvento("NFeEvento", "tb_nfe_evento", "hora", "sim"),
    NFeInut("", "", "diario", "nao"),
    NF3e("NF3e", "tb_nf3e_infnf3e", "diario", "sim"),
    NF3eCanc("NF3eCanc", "tb_nf3e_cancelamento", "diario", "sim"),
    NF3eEvento("NF3eEvento", "tb_nf3e_evento", "diario", "sim"),
    NF3eInut("", "", "diario", "nao"),
    NFeRFB("", "", "diario", "nao"),
    NFeRFBCanc("", "", "diario", "nao"),
    NFeRFBEvento("", "", "diario", "nao"),
    NFCe("NFCe", "tb_nfce_infnfce", "hora", "sim"),
    NFCeCanc("NFCeCanc", "tb_nfce_cancelamento", "diario", "sim"),
    NFCeEvento("", "", "diario", "nao"),
    NFCeInut("", "", "diario", "nao");

    private final String tabelaOracle;
    private final String tabelaHive;
    private final String frequencia;

    private final String ativo;

    TipoDoc(String tabelaOracle, String tabelaHive, String frequencia, String ativo) {
        this.tabelaOracle = tabelaOracle;
        this.tabelaHive = tabelaHive;
        this.frequencia = frequencia;
        this.ativo = ativo;
    }

    public String getFrequencia() {
        return frequencia;
    }

    public String getTabelaOracle() {
        return tabelaOracle;
    }

    public String getTabelaHive() {
        return tabelaHive;
    }

    public String getAtivo() {
        return ativo;
    }
}

