package com.example.demo.Servicios;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.Modelos.DAO.InterfaceClienteDAO;
import com.example.demo.Modelos.DAO.InterfaceUsuarioDAO;
import com.example.demo.Modelos.Entity.Cliente;
import com.example.demo.Modelos.Entity.EstadoCuenta;
import com.example.demo.Modelos.Entity.Rol;
import com.example.demo.Modelos.Entity.Usuario;
import com.example.demo.Modelos.Entity.Cliente;

import org.springframework.transaction.annotation.Transactional;

/**
 * Servicio central de autenticación y gestión de usuarios.
 *
 * Implementa UserDetailsService, que es el "puente" entre nuestra base
 * de datos y Spring Security. Cuando alguien introduce email + contraseña
 * en el formulario de login, Spring Security llama a loadUserByUsername()
 * para obtener los datos del usuario y verificar la contraseña.
 *
 * @Service: marca esta clase como componente de lógica de negocio.
 *           Spring la detecta y la inyecta donde sea necesaria con @Autowired.
 */
@Service
public class UsuarioService implements UserDetailsService { // Hace que spring security pueda pedirla al servicio los
                                                            // datos de una cuenta en el login

    @Autowired
    private InterfaceUsuarioDAO usuarioDAO;
    /**
     * BCryptPasswordEncoder: algoritmo de cifrado para contraseñas.
     *
     * BCrypt genera un hash irreversible con una "sal" aleatoria.
     * Esto significa que la misma contraseña genera hashes distintos
     * cada vez, pero todos son válidos para verificar.
     *
     * NUNCA guardamos la contraseña en texto plano — siempre la ciframos
     * antes de persistir.
     */
    @Autowired
    private InterfaceClienteDAO clienteDAO;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    // ── Implementación de UserDetailsService ──────────────────────────

    /**
     * Spring Security llama a este método con el "username" del formulario.
     * En nuestro caso el username es el email.
     *
     * Flujo:
     * 1. Buscamos el Usuario por email
     * 2. Si no existe → excepción → login fallido
     * 3. Si existe pero NO está APROBADO → excepción con mensaje descriptivo
     * 4. Si está APROBADO → construimos un UserDetails con su rol
     *
     * @throws UsernameNotFoundException Spring Security la captura y redirige
     *                                   a /login?error con el mensaje incluido
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        // Buscamos el usuario en la BD por email
        Usuario usuario = usuarioDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "No existe una cuenta con el email: " + email));

        // Verificamos el estado de la cuenta antes de permitir el login
        if (usuario.getEstado() == EstadoCuenta.PENDIENTE) {
            throw new UsernameNotFoundException(
                    "Tu cuenta está pendiente de aprobación por un administrador.");
        }

        if (usuario.getEstado() == EstadoCuenta.RECHAZADO) {
            throw new UsernameNotFoundException(
                    "Tu solicitud de registro fue rechazada. Contacta al administrador.");
        }

        /*
         * SimpleGrantedAuthority: representa un permiso/rol en Spring Security.
         * Spring Security exige el prefijo "ROLE_" para que funcionen las
         * reglas hasRole("ADMIN") en SecurityConfig.
         * Ejemplo: Rol.ADMIN → "ROLE_ADMIN"
         */
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
                "ROLE_" + usuario.getRol().name());

        /*
         * User.builder() construye el objeto UserDetails que Spring Security
         * necesita internamente. Le pasamos:
         * - username: el email (identificador único de login)
         * - password: ya está cifrado en BD, Spring lo comparará con BCrypt
         * - authorities: la lista de roles/permisos
         */
        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getPassword())
                .authorities(List.of(authority))
                .build();
    }

    // ── Métodos de negocio ─────────────────────────────────────────────

    /**
     * Registra una nueva solicitud de cuenta.
     *
     * - El rol siempre es CLIENTE (el usuario no puede elegirlo)
     * - El estado siempre es PENDIENTE (espera aprobación del admin)
     * - La contraseña se cifra con BCrypt antes de guardar
     *
     * @throws IllegalArgumentException si el email ya está registrado
     */
    public void registrar(String nombre, String apellido, String email, String password) {
        // Verificamos que el email no esté ya registrado
        if (usuarioDAO.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException(
                    "Ya existe una cuenta con el email: " + email);
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setApellido(apellido);
        nuevo.setEmail(email);
        // Ciframos la contraseña antes de guardar — NUNCA guardamos texto plano
        nuevo.setPassword(passwordEncoder.encode(password));
        nuevo.setRol(Rol.CLIENTE); // siempre CLIENTE al registrarse
        nuevo.setEstado(EstadoCuenta.PENDIENTE); // siempre PENDIENTE
        nuevo.setFechaCreacion(new Date());

        usuarioDAO.save(nuevo);
    }

    /**
     * Busca un usuario por email. Usado por los controllers.
     */
    public java.util.Optional<Usuario> buscarPorEmail(String email) {
        return usuarioDAO.findByEmail(email);
    }

    /**
     * Retorna todas las solicitudes con estado PENDIENTE.
     * Usado por el AdminController para mostrar el panel.
     */
    public List<Usuario> obtenerPendientes() {
        return usuarioDAO.findByEstado(EstadoCuenta.PENDIENTE);
    }

    /**
     * Retorna todos los usuarios del sistema.
     */
    public List<Usuario> obtenerTodos() {
        return usuarioDAO.findAll();
    }

    /**
     * Busca un usuario por ID. Retorna null si no existe.
     */
    public Usuario buscarPorId(Long id) {
        return usuarioDAO.findOne(id);
    }

    /**
     * Guarda o actualiza un usuario directamente.
     * Usado por AdminController al aprobar/rechazar solicitudes.
     */
    public void guardar(Usuario usuario) {
        usuarioDAO.save(usuario);
    }

    /**
     * Cambia el rol y estado de una solicitud pendiente.
     * (Solo usuarios con rol ADMIN pueden llamar a este método).
     */
    @Transactional // Le dice a Spring que el método (o clase) debe ejecutarse dentro de una
                   // transacción de base de datos.

    public void aprobarSolicitud(Long id, Rol rol) {
        // Buscamos la cuenta que el administrador quiere procesar.
        Usuario solicitud = usuarioDAO.findOne(id);

        // No se puede aprobar una cuenta que no existe.
        if (solicitud == null) {
            throw new IllegalArgumentException("No se encontró la solicitud.");
        }

        // Solo las solicitudes pendientes pueden cambiar de estado.
        if (solicitud.getEstado() != EstadoCuenta.PENDIENTE) {
            throw new IllegalArgumentException("Esta solicitud ya fue procesada.");
        }

        // El administrador debe elegir ADMIN o CLIENTE antes de aprobar.
        if (rol == null) {
            throw new IllegalArgumentException("Debes elegir un rol.");
        }
        if (rol == Rol.CLIENTE) {
            // Creamos el perfil que aparecerá en la lista de clientes.
            Cliente cliente = new Cliente();
            cliente.setNombre(solicitud.getNombre());
            cliente.setApellido(solicitud.getApellido());
            cliente.setEmail(solicitud.getEmail());
            cliente.setCreateAt(new Date());

            // Guardamos primero el perfil para que la base de datos le asigne un ID.
            clienteDAO.save(cliente);

            // Vinculamos el perfil guardado con la cuenta de usuario.
            solicitud.setCliente(cliente);
        }

        // Guardamos la decisión del administrador.
        solicitud.setRol(rol);
        solicitud.setEstado(EstadoCuenta.APROBADO);

        // El DAO persiste los cambios en la base de datos.
        usuarioDAO.save(solicitud);
    }

    /**
     * Marca como rechazada una solicitud que todavía está pendiente.
     */
    public void rechazarSolicitud(Long id) {
        // Buscamos la cuenta que el administrador quiere rechazar.
        Usuario solicitud = usuarioDAO.findOne(id);

        // No se puede procesar una solicitud inexistente.
        if (solicitud == null) {
            throw new IllegalArgumentException("No se encontró la solicitud.");
        }

        // Evita volver a procesar una solicitud aprobada o rechazada.
        if (solicitud.getEstado() != EstadoCuenta.PENDIENTE) {
            throw new IllegalArgumentException("Esta solicitud ya fue procesada.");
        }

        // El usuario permanece guardado, pero no podrá iniciar sesión.
        solicitud.setEstado(EstadoCuenta.RECHAZADO);
        usuarioDAO.save(solicitud);
    }

}
