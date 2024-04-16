package com.example.dechivejavafx.gui.util;

import com.example.dechivejavafx.Validacoes.ConnectionStatus;

public class Configuracao {
    public static int dias = 20;
    public static int minutoInicial =38;
    public static int intervaloHora = 6;
    public static int diasSped = 180;
    public static int diasSpedHive = diasSped + 1;
    public static int diasSpedRecuo = 2;
    public static int inicioDuplicidade = 2024010100;
    public static int tamanhoLote = 30000;
    public static long esperaQuery = 30;
    public static ConnectionStatus connectionOraprd21Status = ConnectionStatus.ENABLED;
    public static String enumDocumentoPrincipal = "sim";
    public static String enumDocumentoCancelamento = "sim";
    public static String enumDocumentoEvento = "nao";
}
