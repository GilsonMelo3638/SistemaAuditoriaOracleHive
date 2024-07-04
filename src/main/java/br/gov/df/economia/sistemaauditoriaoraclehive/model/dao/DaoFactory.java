package br.gov.df.economia.sistemaauditoriaoraclehive.model.dao;

import br.gov.df.economia.sistemaauditoriaoraclehive.db.DatabaseConfig;
import br.gov.df.economia.sistemaauditoriaoraclehive.model.dao.impl.AgendaDaoJDBC;

public class DaoFactory {

	public static AgendaDao createAgendaDao() {
		return new AgendaDaoJDBC(DatabaseConfig.getConnection());
	}
}
