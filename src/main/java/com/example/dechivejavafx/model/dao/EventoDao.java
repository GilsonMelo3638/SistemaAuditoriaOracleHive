package com.example.dechivejavafx.model.dao;

import com.example.dechivejavafx.model.entities.Evento;

import java.util.List;

public interface EventoDao {

    Evento findById(String id);

    List<Evento> findAll();
}