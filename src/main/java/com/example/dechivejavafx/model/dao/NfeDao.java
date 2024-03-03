package com.example.dechivejavafx.model.dao;

import java.util.List;

import com.example.dechivejavafx.model.entities.Evento;
import com.example.dechivejavafx.model.entities.NFe;

public interface NfeDao {

	NFe findByChave(String id);
	List<NFe> findAll();
	List<NFe> findByEvento(Evento evento);
}