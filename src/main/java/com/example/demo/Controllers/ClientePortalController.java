package com.example.demo.Controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.demo.Modelos.DAO.InterfaceProductoDAO;

@Controller
public class ClientePortalController {
    // Permite consultar productos mediante el DAO existente.
    private final InterfaceProductoDAO productoDAO;

    // Spring proporciona automáticamente la implementación de este DAO.
    public ClientePortalController(InterfaceProductoDAO productoDAO) {
        this.productoDAO = productoDAO;
    }

    // Atiende GET /catalogo, la página que verá el cliente.
    @GetMapping("/catalogo")
    public String mostrarCatalogo(Model model) {
        // La plantilla podrá recorrer esta lista con el nombre "productos".
        model.addAttribute("productos", productoDAO.findAll());

        // Thymeleaf buscará catalogo-cliente.html en templates.
        return "catalogo-cliente";
    }
}
