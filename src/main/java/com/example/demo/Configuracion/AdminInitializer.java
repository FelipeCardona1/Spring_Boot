package com.example.demo.Configuracion;

import com.example.demo.Modelos.Entity.EstadoCuenta;
import com.example.demo.Modelos.Entity.Rol;
import com.example.demo.Modelos.Entity.Usuario;

import java.util.Date;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.Modelos.DAO.InterfaceUsuarioDAO;

/* En esta clase creamos y leemos el administrador
 */

@Component // Crea esta clase importante para utilizarla en cualquier momento
public class AdminInitializer implements CommandLineRunner {

    private final InterfaceUsuarioDAO usuarioDAO;
    private final BCryptPasswordEncoder passwordEncoder;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    public AdminInitializer(InterfaceUsuarioDAO usuarioDAO, BCryptPasswordEncoder passwordEncoder) {
        this.usuarioDAO = usuarioDAO;
        this.passwordEncoder = passwordEncoder;

    }

    @Override
    public void run(String... args) {
        String email = adminEmail.trim().toLowerCase(Locale.ROOT);

        if (usuarioDAO.findByEmail(email).isPresent()) {
            return;
        }
        Usuario admin = new Usuario();
        admin.setEmail(email);
        admin.setNombre("Administrador");
        admin.setApellido("Sistema");
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setEstado(EstadoCuenta.APROBADO);
        admin.setRol(Rol.ADMIN);
        admin.setFechaCreacion(new Date());
        usuarioDAO.save(admin);

    }

}
