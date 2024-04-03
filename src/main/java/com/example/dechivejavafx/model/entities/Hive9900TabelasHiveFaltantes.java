package com.example.dechivejavafx.model.entities;

import java.math.BigInteger;
import java.time.LocalDateTime;

public class Hive9900TabelasHiveFaltantes {
    private BigInteger idBase;
    private String registroBloco;

    public Hive9900TabelasHiveFaltantes() {
        // Pode inicializar atributos com valores padrão, se necessário
    }

    public Hive9900TabelasHiveFaltantes(BigInteger idBase, String registroBloco ) {
        this.idBase = idBase;
        this.registroBloco = registroBloco;
    }

    public BigInteger getIdBase() {
        return idBase;
    }
    public void setIdBase(BigInteger idBase) {
        this.idBase = idBase;
    }

    public String getRegistroBloco() {
        return registroBloco;
    }
    public void setRegistroBloco(String registroBloco) {
        this.registroBloco = registroBloco;
    }

    @Override
    public String toString() {
        return "NFeInfo{" +
                "idBase=" + idBase +
                ", registroBloco='" + registroBloco + '\'' +
                '}';
    }
}