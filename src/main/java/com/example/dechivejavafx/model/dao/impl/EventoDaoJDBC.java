package com.example.dechivejavafx.model.dao.impl;

import com.example.dechivejavafx.db.DatabaseConfig;
import com.example.dechivejavafx.db.DatabaseExceptions;
import com.example.dechivejavafx.model.dao.EventoDao;
import com.example.dechivejavafx.model.entities.Evento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventoDaoJDBC implements EventoDao {

    private Connection conn;

    public EventoDaoJDBC(Connection conn) {
        this.conn = conn;
    }

    @Override
    public Evento findById(String id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT * FROM department WHERE Id = ?");
            st.setString(1, id);
            rs = st.executeQuery();
            if (rs.next()) {
                Evento obj = new Evento();
                obj.setChave(rs.getString("Id"));
                obj.setTpevento(rs.getInt("Name"));
                return obj;
            }
            return null;
        } catch (SQLException e) {
            throw new DatabaseExceptions.DbException(e.getMessage());
        } finally {
            DatabaseConfig.closeStatement(st);
            DatabaseConfig.closeResultSet(rs);
        }
    }

    @Override
    public List<Evento> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = conn.prepareStatement(
                    "SELECT * FROM department ORDER BY Name");
            rs = st.executeQuery();

            List<Evento> list = new ArrayList<>();

            while (rs.next()) {
                Evento obj = new Evento();
                obj.setChave(rs.getString("Id"));
                obj.setTpevento(rs.getInt("Name"));
                list.add(obj);
            }
            return list;
        } catch (SQLException e) {
            throw new DatabaseExceptions.DbException(e.getMessage());
        } finally {
            DatabaseConfig.closeStatement(st);
            DatabaseConfig.closeResultSet(rs);
        }
    }
}