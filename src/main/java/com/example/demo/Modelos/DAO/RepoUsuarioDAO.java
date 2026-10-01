package com.example.demo.Modelos.DAO;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Modelos.Entity.EstadoCuenta;
import com.example.demo.Modelos.Entity.Usuario;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;

/**
 * Implementación concreta de InterfaceUsuarioDAO.
 * Usa EntityManager directamente — mismo enfoque que RepoClienteDAO y
 * RepoProductoDAO.
 *
 * @Repository: marca esta clase como componente de acceso a datos.
 *              Spring la detecta automáticamente y la registra en el contexto.
 *
 * @PersistenceContext: inyecta el EntityManager que gestiona la sesión
 *                      JPA/Hibernate.
 *                      Es thread-safe cuando se usa con esta anotación (Spring
 *                      crea un proxy).
 */
@Repository
public class RepoUsuarioDAO implements InterfaceUsuarioDAO {

    @PersistenceContext
    private EntityManager em;

    /**
     * Guarda (INSERT) o actualiza (UPDATE) según si el id ya existe.
     * - persist(): nuevo registro → INSERT
     * - merge(): registro existente → UPDATE
     */

    @Transactional
    @Override
    public void save(Usuario usuario) {
        if (usuario.getId() != null && usuario.getId() > 0) {
            em.merge(usuario);
        } else {
            em.persist(usuario);
        }
    }

    /** Busca por clave primaria. Retorna null si no existe. */
    @Transactional(readOnly = true)
    @Override
    public Usuario findOne(Long id) {
        return em.find(Usuario.class, id);
    }

    /** Trae todos los usuarios ordenados por fecha de creación descendente */
    @SuppressWarnings("unchecked") // Evita que el compilador saque avisos de advertencia y el unch... suprime
                                   // afvertencia de tipos no verificadas
    @Transactional(readOnly = true)
    @Override
    public List<Usuario> findAll() {
        return em.createQuery("from Usuario u order by u.fechaCreacion desc").getResultList();
    }

    /**
     * Busca un Usuario por email usando JPQL.
     *
     * JPQL (Java Persistence Query Language) es SQL orientado a objetos:
     * en vez de escribir "SELECT * FROM usuarios WHERE email = ?"
     * escribimos "FROM Usuario u WHERE u.email = :email"
     * usando el nombre de la clase Java, no el nombre de la tabla.
     *
     * getSingleResult() lanza NoResultException si no encuentra nada,
     * por eso lo capturamos y retornamos Optional.empty() en ese caso.
     */

    @Transactional(readOnly = true)
    @Override
    public Optional<Usuario> findByEmail(String email) {
        try {
            Usuario usuario = (Usuario) em
                    .createQuery("FROM Usuario u WHERE u.email = :email")
                    .setParameter("email", email)
                    .getSingleResult();
            return Optional.of(usuario);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    /**
     * Filtra usuarios por estado (PENDIENTE, APROBADO, RECHAZADO).
     * El admin lo usa para ver las solicitudes pendientes.
     */
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    @Override
    public List<Usuario> findByEstado(EstadoCuenta estado) {
        return em.createQuery("FROM Usuario u WHERE u.estado = :estado order by u.fechaCreacion desc")
                .setParameter("estado", estado)
                .getResultList();
    }

    /** Elimina el usuario si existe */
    @Transactional
    @Override
    public void delete(Long id) {
        Usuario usuario = findOne(id);
        if (usuario != null) {
            em.remove(usuario);
        }
    }
}
