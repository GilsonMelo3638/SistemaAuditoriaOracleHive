package br.gov.df.economia.sistemaauditoriaoraclehive.gui.util;

import br.gov.df.economia.sistemaauditoriaoraclehive.Validacoes.ConnectionStatus;

public class Configuracao {
    public static int dias = 30;
    public static int diasLegado = 2005;
    public static int minutoInicial =59;
    public static int intervaloHora = 6;
    public static int diasSped = 8;
    public static int diasSpedHive = diasSped + 1;
    public static int diasSpedRecuo = 2;
    public static int inicioDuplicidade = 2024010100;
    public static int tamanhoLote = 60000;
    public static long esperaQuery = 0;
    public static ConnectionStatus connectionOraprd21Status = ConnectionStatus.ENABLED;
    public static String enumDocumentoPrincipal = "sim";
    public static String enumDocumentoCancelamento = "sim";
    public static String enumDocumentoEvento = "sim";
}
