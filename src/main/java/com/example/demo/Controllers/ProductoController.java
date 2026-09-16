package com.example.demo.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.Modelos.DAO.InterfaceProductoDAO;
import com.example.demo.Modelos.Entity.Producto;

@Controller 
@RequestMapping ("/producto")
public class ProductoController {

    @Autowired 
    private InterfaceProductoDAO productoDAO;

    @GetMapping({"/stock"})
    public String listar(Model model) {
        model.addAttribute("titulo", "Listar Productos");
        model.addAttribute("productos", productoDAO.findAll());
        return "producto-listar";
    }

    @GetMapping("/formulario")
    public String formulario(Model model) {
        model.addAttribute("producto", new Producto());
        model.addAttribute("titulo", "Formulario de Producto");
        return "producto-formulario";
    }

    @GetMapping("/formulario/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Producto producto = productoDAO.findOne(id);
        if (producto == null) {
            return "redirect:/producto/listar";
        }
        model.addAttribute("producto", producto);
        model.addAttribute("titulo", "Editar Producto");
        return "producto-formulario";
    }

    @PostMapping("/formulario")
    public String guardar(Producto producto) {
        productoDAO.save(producto);
        return "redirect:/producto/listar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        productoDAO.delete(id);
        return "redirect:/producto/listar";
    }

}
