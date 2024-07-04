package br.gov.df.economia.sistemaauditoriaoraclehive.Validacoes;


public enum SituacaoProcessamento {
    AGENDADO,
    INICIADO,
    CONCLUIDO,
    FINALIZADO,
    EM_ANDAMENTO,
    INTERROMPE,
    INICIADO_PRIORIDADE,
    PRIORIDADE,
    ERRO;
}