package br.gov.df.economia.sistemaauditoriaoraclehive.Validacoes;

import br.gov.df.economia.sistemaauditoriaoraclehive.gui.util.Configuracao;

public enum TipoDoc {
    BPe("BPe", "tb_bpe_infbpe", "diario", Configuracao.enumDocumentoPrincipal),
    BPeCanc("BPeCanc", "tb_bpe_cancelamento", "diario", Configuracao.enumDocumentoCancelamento),
    BPeEvento("BPeEvento", "tb_bpe_evento", "diario", Configuracao.enumDocumentoEvento),
    BPeInut("BPeInut", "", "diario", "nao"),
    CTe("CTe", "tb_cte_infcte", "hora", Configuracao.enumDocumentoPrincipal),
    CTeCanc("CTeCanc", "tb_cte_cancelamento", "diario", Configuracao.enumDocumentoCancelamento),
    CTeEvento("CTeEvento", "tb_cte_evento", "hora", Configuracao.enumDocumentoEvento),
    CTeInut("CTeInut", "", "diario", "nao"),
    CTeRFB("", "", "hora", "nao"),
    CTeRFBCanc("", "", "diario", "nao"),
    CTeRFBEvento("", "", "hora", "nao"),
    MDFe("MDFe", "tb_mdfe_infmdfe", "hora", Configuracao.enumDocumentoPrincipal),
    MDFeCanc("MDFeCanc", "tb_mdfe_cancelamento", "diario", Configuracao.enumDocumentoCancelamento),
    MDFeEvento("MDFeEvento", "tb_mdfe_evento", "hora", Configuracao.enumDocumentoEvento),
    MDFEInut("", "", "diario", "nao"),
    MDFeRFB("", "", "diario", "nao"),
    MDFeRFBCanc("", "", "diario", "nao"),
    MDFeRFBEvento("", "", "diario", "nao"),
    NFe("NFe", "tb_nfe_infnfe", "hora", Configuracao.enumDocumentoPrincipal),
    NFeCanc("NFeCanc", "tb_nfe_cancelamento", "diario", Configuracao.enumDocumentoCancelamento),
    NFeEvento("NFeEvento", "tb_nfe_evento", "hora", Configuracao.enumDocumentoEvento),
    NFeInut("", "", "diario", "nao"),
    NF3e("NF3e", "tb_nf3e_infnf3e", "diario", Configuracao.enumDocumentoPrincipal),
    NF3eCanc("NF3eCanc", "tb_nf3e_cancelamento", "diario", Configuracao.enumDocumentoCancelamento),
    NF3eEvento("NF3eEvento", "tb_nf3e_evento", "diario", Configuracao.enumDocumentoEvento),
    NF3eInut("", "", "diario", "nao"),
    NFeRFB("", "", "diario", "nao"),
    NFeRFBCanc("", "", "diario", "nao"),
    NFeRFBEvento("", "", "diario", "nao"),
    NFCe("NFCe", "tb_nfce_infnfce", "hora", Configuracao.enumDocumentoPrincipal),
    NFCeCanc("NFCeCanc", "tb_nfce_cancelamento", "diario", Configuracao.enumDocumentoCancelamento),
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

