package com.example.demo.Configuracion;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.example.demo.Modelos.DAO.InterfaceClienteDAO;
import com.example.demo.Modelos.Entity.Cliente;
import com.example.demo.Servicios.UsuarioService;

@Component
// CommanLineRunner Se usa para cargar datos iniciales, ejecutar
// configuraciones, o lanzar procesos al inicio.
public class DatosPruebaInitializer implements CommandLineRunner {
    // Estos son los tres correos que hay insertados en import.sql.
    private static final Set<String> CORREOS_CLIENTES_DEMO = Set.of("a@gmail.com", "b@gmail.com", "c@gmail.com");

    // Contraseñas de demostración para probar localmente.
    private static final String PASSWORD_CLIENTE = "Cliente123!";
    private static final String PASSWORD_SOLICITUD = "Solicitud123!";

    private final InterfaceClienteDAO clienteDAO;
    private final UsuarioService usuarioService;

    public DatosPruebaInitializer(
            InterfaceClienteDAO clienteDAO,
            UsuarioService usuarioService) {
        this.clienteDAO = clienteDAO;
        this.usuarioService = usuarioService;
    }

    @Override
    // Permite que el método reciba cero, uno o varios parámetros de tipo String.
    public void run(String... args) {
        // import.sql se ejecuta antes de este inicializador.
        // Asociamos una cuenta aprobada a cada uno de esos tres perfiles.
        for (Cliente cliente : clienteDAO.findAll()) {
            if (CORREOS_CLIENTES_DEMO.contains(cliente.getEmail().toLowerCase())) {
                usuarioService.crearCuentaInicialCliente(cliente, PASSWORD_CLIENTE);
            }
        }

        // Creamos tres solicitudes para que aparezcan en el panel del administrador.
        crearSolicitudSiNoExiste(
                "Solicitud Uno", "Prueba",
                "solicitud1@demo.com");

        crearSolicitudSiNoExiste(
                "Solicitud Dos", "Prueba",
                "solicitud2@demo.com");

        crearSolicitudSiNoExiste(
                "Solicitud Tres", "Prueba",
                "solicitud3@demo.com");
    }

    private void crearSolicitudSiNoExiste(
            String nombre, String apellido, String email) {

        // Evita duplicados si más adelante usas una base que conserve los datos.
        if (usuarioService.buscarPorEmail(email).isEmpty()) {
            usuarioService.registrar(
                    nombre, apellido, email, PASSWORD_SOLICITUD);
        }
    }
}
