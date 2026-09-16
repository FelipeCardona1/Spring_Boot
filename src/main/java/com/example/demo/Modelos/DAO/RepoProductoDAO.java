package com.example.demo.Modelos.DAO;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Modelos.Entity.Producto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository 
public class RepoProductoDAO implements InterfaceProductoDAO {
    @PersistenceContext
    private EntityManager em;
    @SuppressWarnings ("unchecked")
    @Transactional (readOnly = true)
    @Override
    public List<Producto> findAll() {
        return em.createQuery("from Producto").getResultList();
    }
    
    @Transactional
    @Override 
    public void save(Producto producto) {
        if(producto.getId() != null && producto.getId() > 0){
            em.merge(producto);
        }else{
            em.persist(producto);
        }
    }

    @Transactional 
    @Override 
    public Producto findOne(Long id) {
        return em.find(Producto.class, id);
    }

    @Transactional 
    @Override 
    public void delete(Long id) {
        Producto producto = findOne(id);
        if (producto != null) {
            em.remove(producto);
        }
    }
}
