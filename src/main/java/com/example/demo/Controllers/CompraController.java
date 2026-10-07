package com.example.demo.Controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.Modelos.DTO.LineaCompraDTO;
import com.example.demo.Servicios.CompraService;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.example.demo.Modelos.DTO.FacturaDTO;

@Controller
public class CompraController {
    private final CompraService compraService;

    public CompraController(CompraService compraService) {
        this.compraService = compraService;
    }

    @PostMapping("/compra/confirmar")
    public String confirmarCompra(
            @RequestParam(value = "productoIds", required = false) List<Long> productoIds,
            @RequestParam(value = "cantidades", required = false) List<Integer> cantidades,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        // Los dos listados vienen en el mismo orden desde las filas del catálogo.
        if (productoIds == null || cantidades == null
                || productoIds.size() != cantidades.size()) {
            redirectAttributes.addFlashAttribute(
                    "error", "No se pudieron leer los productos seleccionados.");
            return "redirect:/catalogo";
        }

        List<LineaCompraDTO> lineas = new ArrayList<>();

        for (int i = 0; i < productoIds.size(); i++) {
            // Usamos Integer para poder detectar si el parámetro llegó vacío.
            Integer cantidadSeleccionada = cantidades.get(i);

            if (cantidadSeleccionada == null || cantidadSeleccionada < 0) {
                redirectAttributes.addFlashAttribute(
                        "error", "Revisa las cantidades seleccionadas.");
                return "redirect:/catalogo";
            }

            int cantidad = cantidadSeleccionada;

            // Cero significa que el cliente no seleccionó este producto.
            if (cantidad > 0) {
                LineaCompraDTO linea = new LineaCompraDTO();
                linea.setProductoId(productoIds.get(i));
                linea.setCantidad(cantidad);
                lineas.add(linea);
            }
        }

        try {
            // El servicio obtiene el cliente desde este email y verifica el stock.
            Long encabezadoId = compraService.confirmarCompra(
                    principal.getName(), lineas);

            // En el siguiente paso crearemos la página que muestra esa factura.
            return "redirect:/compra/factura/" + encabezadoId;

        } catch (IllegalArgumentException error) {
            // Errores como stock insuficiente vuelven al catálogo con un mensaje.
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/catalogo";
        }
    }

    @GetMapping("/compra/factura/{id}")
    public String mostrarFactura(
            @PathVariable Long id,
            Principal principal,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            // El servicio comprueba que la factura pertenezca al cliente conectado.
            FacturaDTO factura = compraService.obtenerFactura(id, principal.getName());

            // Thymeleaf recibirá el encabezado y sus líneas de detalle.
            model.addAttribute("factura", factura);

            return "factura";

        } catch (IllegalArgumentException error) {
            // Si no existe o pertenece a otro cliente, volvemos al catálogo.
            redirectAttributes.addFlashAttribute("error", error.getMessage());
            return "redirect:/catalogo";
        }
    }
}
