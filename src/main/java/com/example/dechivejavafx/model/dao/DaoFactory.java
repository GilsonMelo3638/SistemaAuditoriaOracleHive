package com.example.dechivejavafx.model.dao;

import com.example.dechivejavafx.db.DatabaseConfig;
import com.example.dechivejavafx.model.dao.impl.AgendaDaoJDBC;

public class DaoFactory {

	public static AgendaDao createAgendaDao() {
		return new AgendaDaoJDBC(DatabaseConfig.getConnection());
	}
}
