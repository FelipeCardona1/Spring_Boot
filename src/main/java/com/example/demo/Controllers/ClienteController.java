package com.example.demo.Controllers;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.Modelos.Entity.Cliente;
import com.example.demo.Modelos.DAO.InterfaceClienteDAO;

@Controller
@RequestMapping({"/Cliente", "/cliente"})
public class ClienteController {

    @Autowired 
    private InterfaceClienteDAO clienteDAO;
    
    @GetMapping("/listar")
    public String Listar(Model model){

        model.addAttribute("titulo", "Listar Clientes");

        List<Cliente> clientes = clienteDAO.findAll();
        model.addAttribute("clientes", clientes);
        return "listar";
    }

    @GetMapping("/formulario")
    public String formulario(Model model) {
        model.addAttribute("cliente", new Cliente());
        model.addAttribute("titulo", "Formulario de Cliente");
        return "Formulario";
    }

    @GetMapping("/formulario/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Cliente cliente = clienteDAO.findOne(id);
        if (cliente == null) {
            return "redirect:/Cliente/listar";
        }
        model.addAttribute("cliente", cliente);
        model.addAttribute("titulo", "Editar Cliente");
        return "Formulario";
    }

    @PostMapping("/formulario")
    public String guardar(Cliente cliente) {
        if (cliente.getCreateAt() == null) {
            cliente.setCreateAt(new Date());
        }
        clienteDAO.save(cliente);
        return "redirect:/Cliente/listar";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        clienteDAO.delete(id);
        return "redirect:/Cliente/listar";
    }

    
}
