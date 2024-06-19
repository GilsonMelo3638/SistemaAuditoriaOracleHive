package com.example.dechivejavafx.gui.util;

import com.example.dechivejavafx.Validacoes.ConnectionStatus;

public class Configuracao {
    public static int dias = 30;
    public static int minutoInicial =59;
    public static int intervaloHora = 6;
    public static int diasSped = 168;
    public static int diasSpedHive = diasSped + 1;
    public static int diasSpedRecuo = 2;
    public static int inicioDuplicidade = 2024010130;
    public static int tamanhoLote = 60000;
    public static long esperaQuery = 0;
    public static ConnectionStatus connectionOraprd21Status = ConnectionStatus.ENABLED;
    public static String enumDocumentoPrincipal = "sim";
    public static String enumDocumentoCancelamento = "sim";
    public static String enumDocumentoEvento = "sim";
}
