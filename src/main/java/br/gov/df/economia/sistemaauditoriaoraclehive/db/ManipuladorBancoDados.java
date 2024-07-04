package br.gov.df.economia.sistemaauditoriaoraclehive.db;

import br.gov.df.economia.sistemaauditoriaoraclehive.gui.QuantidadeDocumentoArquivoController;
import br.gov.df.economia.sistemaauditoriaoraclehive.Validacoes.TipoDoc;
import br.gov.df.economia.sistemaauditoriaoraclehive.model.entities.QuantidadeDocumentoArquivo;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.logging.Level;
import java.util.List;
import java.util.logging.Logger;

public class ManipuladorBancoDados {
    // Logger para registro de mensagens
    private static final Logger LOGGER = Logger.getLogger(QuantidadeDocumentoArquivoController.class.getName());
    private HiveDecDatabaseOperations operacoesBancoDadosHive;

    public ManipuladorBancoDados(String jdbcURL, String username, String password) {
        operacoesBancoDadosHive = new HiveDecDatabaseOperations(jdbcURL, username, password);
    }

    public List<TipoDoc> obterTipoDocs() {
        return operacoesBancoDadosHive.obterTipoDocs();
    }

    public List<QuantidadeDocumentoArquivo> executarSQLQuantidadeDocumentos(String tabela, String dataSelecionada) {
        try {
            Connection connection = HiveDecDatabaseOperations.getHiveConnection(
                    operacoesBancoDadosHive.getJdbcURL(),
                    operacoesBancoDadosHive.getUsername(),
                    operacoesBancoDadosHive.getPassword()
            );
            return operacoesBancoDadosHive.executeSQLQuantidadeDocumentos(connection, tabela, dataSelecionada);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erro ao obter conexão Hive", e);
        }
        return Collections.emptyList();
    }
}
