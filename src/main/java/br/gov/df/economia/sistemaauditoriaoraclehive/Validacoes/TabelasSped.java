package br.gov.df.economia.sistemaauditoriaoraclehive.Validacoes;
public enum TabelasSped {
    _0000, _0150, _0200, _0460, _9900, _9999, _B020, _B025, _B420, _B440,
    _B460, _B470, _C100, _C113, _C170, _C190, _C191, _C195, _C197, _C390, _C500, _C590,
    _C595, _D100, _D101, _D190, _D195, _D197, _D500, _D510, _D530, _D590, _D695, _D696,
    _E110, _E111, _E112, _E113, _E115, _E116, _E200, _E210, _E220, _E230, _E240, _E250,
    _E300, _E310, _E311, _E316, _E500, _E510, _E520, _E530, _E531;

    public String getFormattedName() {
        return this.name().substring(1); // Remove o primeiro caracter "_"
    }
}

