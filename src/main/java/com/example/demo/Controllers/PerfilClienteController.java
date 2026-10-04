package com.example.demo.Controllers;

import java.security.Principal;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Modelos.Entity.Usuario;
import com.example.demo.Servicios.UsuarioService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class PerfilClienteController {

    private final UsuarioService usuarioService; // el constructor inicializa la dependencia una sola vez y nunca más se
                                                 // puede cambiar.

    // Spring inyecta el servicio que busca la cuenta en la base de datos.
    public PerfilClienteController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/cliente/perfil")
    public String mostrarPerfil(Principal principal, Model model) {
        // principal.getName() es el email con el que inició sesión el usuario.
        Usuario usuario = usuarioService
                .buscarPorEmail(principal.getName())
                .orElse(null);

        // La cuenta aprobada debe tener un Cliente asociado.
        if (usuario == null || usuario.getCliente() == null) {
            return "redirect:/catalogo";
        }

        // Enviamos solamente el perfil vinculado a esta cuenta autenticada.
        model.addAttribute("cliente", usuario.getCliente());

        return "perfil-cliente";
    }

    @PostMapping("/cliente/perfil")
    public String guardarPerfil(
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String email,
            Principal principal,
            HttpServletRequest request,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {

        String emailActual = principal.getName();

        // Comparamos antes de actualizar para detectar si cambió el correo de login.
        boolean cambioEmail = !emailActual.equalsIgnoreCase(email.trim());

        try {
            usuarioService.actualizarPerfilCliente(
                    emailActual, nombre, apellido, email);

            if (cambioEmail) {
                // La sesión conserva el correo anterior; la cerramos para volver
                // a iniciar sesión con el correo nuevo.
                new SecurityContextLogoutHandler().logout(
                        request,
                        response,
                        SecurityContextHolder.getContext().getAuthentication());

                return "redirect:/login?correoActualizado";
            }

            // Si el correo no cambió, puede seguir usando la sesión actual.
            redirectAttributes.addFlashAttribute(
                    "mensaje", "Tus datos fueron actualizados correctamente.");

            return "redirect:/cliente/perfil";

        } catch (IllegalArgumentException error) {
            // Mostramos errores como correo duplicado o perfil no encontrado.
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/cliente/perfil";
        }
    }
}
