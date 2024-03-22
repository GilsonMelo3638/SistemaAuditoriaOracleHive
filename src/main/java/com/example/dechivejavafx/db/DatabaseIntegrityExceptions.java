package com.example.dechivejavafx.db;

import com.example.dechivejavafx.model.entities.PendenciasHive;
import com.example.dechivejavafx.model.entities.TotalizacaoNfe;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DatabaseIntegrityExceptions {

    public static class DbIntegrityException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public DbIntegrityException(String msg) {
            super(msg);
        }
    }

    public static TotalizacaoNfe mapResultSetToTotalizacaoNfe(ResultSet resultSet) throws SQLException {
        TotalizacaoNfe nFeInfo = new TotalizacaoNfe();
        nFeInfo.setTotalNFe(resultSet.getInt("Total_NFe"));
        nFeInfo.setUf(resultSet.getString("uf"));
        nFeInfo.setVnf(resultSet.getDouble("vnf"));
        nFeInfo.setVserv(resultSet.getDouble("vserv"));
        nFeInfo.setViss(resultSet.getDouble("viss"));
        nFeInfo.setVbc(resultSet.getDouble("vbc"));
        nFeInfo.setVicms(resultSet.getDouble("vicms"));
        nFeInfo.setVst(resultSet.getDouble("vst"));
        nFeInfo.setVfcpst(resultSet.getDouble("vfcpst"));
        nFeInfo.setVfcpstret(resultSet.getDouble("vfcpstret"));
        nFeInfo.setVprod(resultSet.getDouble("vprod"));
        nFeInfo.setVfcpufdest(resultSet.getDouble("vfcpufdest"));
        nFeInfo.setArquivo(resultSet.getString("arquivo"));

        return nFeInfo;
    }

    public static PendenciasHive mapResultSetToDetNFeNFCeInf(ResultSet resultSetDet) throws SQLException {
        PendenciasHive detInfo = new PendenciasHive();
        detInfo.setArquivo(resultSetDet.getString("arquivo"));
        detInfo.setTabelaDetalhe(resultSetDet.getString("tabela")); // Ajuste para refletir o nome correto da coluna
        detInfo.setQuantidadeNsuchave(resultSetDet.getInt("quantidade_nsuchave")); // Ajuste para refletir o nome correto da coluna

        return detInfo;
    }

}
