package com.example.demo.Modelos.DAO;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Modelos.Entity.Encabezado;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
public class RepoEncabezadoDAO implements InterfaceEncabezadoDAO {
    @PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void save(Encabezado encabezado) {
        // Si tiene ID, ya existe y se actualiza; si no, se inserta.
        if (encabezado.getId() != null && encabezado.getId() > 0) {
            em.merge(encabezado); // lo sincroniza con la bd y hace y hace una copia
        } else {
            em.persist(encabezado);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Encabezado findOne(Long id) {
        return em.find(Encabezado.class, id);
    }
}
