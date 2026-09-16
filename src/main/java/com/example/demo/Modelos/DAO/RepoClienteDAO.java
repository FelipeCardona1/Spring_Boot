package com.example.demo.Modelos.DAO;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Modelos.Entity.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository 
public class RepoClienteDAO implements InterfaceClienteDAO {
    @PersistenceContext 
    private EntityManager em;

    @SuppressWarnings ("unchecked")
    @Transactional (readOnly = true)
    @Override
    public List<Cliente> findAll() {
        return em.createQuery("from Cliente c").getResultList();
    }

    @Transactional 
    @Override 
    public void save(Cliente cliente) {
        if(cliente.getId() != null && cliente.getId() > 0){
            em.merge(cliente);
        }else{
            em.persist(cliente);
        }
    }

    @Transactional (readOnly = true)
    @Override
    public Cliente findOne(Long id) {
        return em.find(Cliente.class, id);
    }

    @Transactional 
    @Override 
    public void delete(Long id) {
        Cliente cliente = findOne(id);
        if (cliente != null) {
            em.remove(cliente);
        }
    }

}
