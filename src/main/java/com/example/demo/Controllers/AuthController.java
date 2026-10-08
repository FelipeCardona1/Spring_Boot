package com.example.demo.Controllers;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Servicios.UsuarioService;

/**
 * Controller para las páginas públicas de autenticación:
 * - GET /login → muestra la pantalla de login
 * - GET /registro → muestra el formulario de registro
 * - POST /registro → procesa la solicitud de registro
 *
 * El procesamiento del login (POST /login) lo maneja Spring Security
 * internamente — nosotros solo configuramos los parámetros en SecurityConfig.
 *
 * El logout (POST /logout) también lo maneja Spring Security directamente.
 */

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/registro")
    public String mostrarRegistro() {
        return "registro";
    }

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/registro") // Como es un formulario se utiliza postmapping para registrar
    public String procesarRegistro(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            @RequestParam String password,
            Model model, // Manda mensajes del back al front
            RedirectAttributes redirectAttributes) {

        if (nombre.isBlank() || email.isBlank() || password.length() < 6) {
            model.addAttribute("error", "Completa los campos y usa una contraseña de al menos 6 caracteres.");
            return "registro";
        }

        try {
            usuarioService.registrar(
                    nombre.trim(),
                    apellido.trim(),
                    email.trim().toLowerCase(Locale.ROOT),
                    password);

            redirectAttributes.addFlashAttribute(
                    "mensaje",
                    "Solicitud enviada. Un administrador debe aprobar tu cuenta.");

            return "redirect:/registro";

        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            return "registro";
        }
    }

}
