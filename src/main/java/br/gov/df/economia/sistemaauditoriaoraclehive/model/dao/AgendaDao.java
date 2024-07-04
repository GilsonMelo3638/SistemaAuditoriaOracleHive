package br.gov.df.economia.sistemaauditoriaoraclehive.model.dao;

import java.util.List;

import br.gov.df.economia.sistemaauditoriaoraclehive.Validacoes.TipoDoc;
import br.gov.df.economia.sistemaauditoriaoraclehive.model.entities.Agenda;
import br.gov.df.economia.sistemaauditoriaoraclehive.model.entities.Evento;

public interface AgendaDao {

    void insert(Agenda obj);

    void updateAgenda(Agenda obj);

    void deleteByCodAgenda(Long id);

    Agenda findByCodAgenda(Long codAgenda);

    List<Agenda> findAll();

    List<Agenda> findByEvento(Evento evento);
    
    List<Agenda> findAllByTipoDoc(TipoDoc tipoDoc, int dias);

    void commit(); // Adicione o método commit
}
