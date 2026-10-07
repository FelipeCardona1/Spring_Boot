package com.example.demo.Modelos.DAO;

import java.util.List;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Modelos.Entity.Detalle;

import jakarta.persistence.EntityManager;

@Repository
public class RepoDetalleDAO implements InterfaceDetalleDAO {
    // Spring inyecta el EntityManager de JPA.
    @jakarta.persistence.PersistenceContext
    private EntityManager em;

    @Override
    @Transactional
    public void save(Detalle detalle) {
        // Un detalle sin ID es nuevo; uno con ID ya existe.
        if (detalle.getId() != null && detalle.getId() > 0) {
            em.merge(detalle);
        } else {
            em.persist(detalle);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Detalle> findByEncabezadoId(Long encabezadoId) {
        // JPQL consulta la entidad y sus propiedades Java, no los nombres SQL.
        return em.createQuery(
                "SELECT d FROM Detalle d "
                        + "WHERE d.encabezado.id = :encabezadoId "
                        + "ORDER BY d.id",
                Detalle.class)
                .setParameter("encabezadoId", encabezadoId)
                .getResultList();
    }
}
