package com.example.demo.Modelos.DAO;

import java.util.List;
import java.util.Optional;

import com.example.demo.Modelos.Entity.EstadoCuenta;
import com.example.demo.Modelos.Entity.Usuario;

public interface InterfaceUsuarioDAO {

    /** Guarda o actualiza un usuario */
    void save(Usuario usuario);

    /** Busca un usuario por su ID */
    Usuario findOne(Long id);

    /** Retorna todos los usuarios */
    List<Usuario> findAll();

    /**
     * Busca por email. Retorna Optional porque el email puede no existir.
     * Spring Security usa este método en UsuarioService para cargar
     * el usuario al hacer login.
     *
     * Optional<T> es una forma de decir "puede haber un valor o no"
     * sin usar null directamente — evita NullPointerException.
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Retorna todos los usuarios que tengan un estado específico.
     * El admin usa esto para ver las solicitudes PENDIENTES.
     */
    List<Usuario> findByEstado(EstadoCuenta estado);

    /** Elimina un usuario por ID */
    void delete(Long id);
}
