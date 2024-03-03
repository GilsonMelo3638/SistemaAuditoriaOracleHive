package com.example.dechivejavafx.model.dao;

import java.util.List;

import com.example.dechivejavafx.Validacoes.TipoDoc;
import com.example.dechivejavafx.model.entities.Agenda;
import com.example.dechivejavafx.model.entities.Evento;

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
