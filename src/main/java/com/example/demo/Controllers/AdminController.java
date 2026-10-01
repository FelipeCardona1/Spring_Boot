package com.example.demo.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Modelos.Entity.Rol;
import com.example.demo.Servicios.UsuarioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService usuarioService;

    public AdminController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String mostrarSolicitudes(Model model) {
        model.addAttribute("solicitudes", usuarioService.obtenerPendientes());
        return "admin-panel";
    }

    @PostMapping("/aprobar/{id}")
    public String aprobarSolicitud(
            @PathVariable Long id,
            @RequestParam Rol rol,
            RedirectAttributes redirectAttributes) {

        try {
            // El rol llega como texto, por ejemplo CLIENTE, y Spring lo convierte al enum.
            usuarioService.aprobarSolicitud(id, rol);

            redirectAttributes.addFlashAttribute(
                    "mensaje", "La solicitud fue aprobada correctamente.");
        } catch (IllegalArgumentException ex) {
            // Conservamos el motivo del error para mostrarlo al volver al panel.
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        // Volvemos al panel para que la lista se cargue nuevamente.
        return "redirect:/admin";
    }

    /** Ahora para rechazar una peticion: */
    @PostMapping("/rechazar/{id}")
    public String rechazarSolicitud(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            // El servicio verifica que la cuenta exista y siga pendiente.
            usuarioService.rechazarSolicitud(id);

            redirectAttributes.addFlashAttribute(
                    "mensaje", "La solicitud fue rechazada.");
        } catch (IllegalArgumentException ex) {
            // Informamos si no existe o ya se había procesado.
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }

        // Volvemos al panel para actualizar la lista de pendientes.
        return "redirect:/admin";
    }
}
