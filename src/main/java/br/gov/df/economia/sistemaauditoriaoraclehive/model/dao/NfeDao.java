package br.gov.df.economia.sistemaauditoriaoraclehive.model.dao;

import java.util.List;

import br.gov.df.economia.sistemaauditoriaoraclehive.model.entities.Evento;
import br.gov.df.economia.sistemaauditoriaoraclehive.model.entities.NFe;

public interface NfeDao {

	NFe findByChave(String id);
	List<NFe> findAll();
	List<NFe> findByEvento(Evento evento);
}