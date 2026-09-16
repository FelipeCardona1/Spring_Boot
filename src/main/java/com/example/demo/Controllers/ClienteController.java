package com.example.demo.Controllers;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.Modelos.Entity.Cliente;
import com.example.demo.Modelos.DAO.InterfaceClienteDAO;

@Controller
@RequestMapping("/Cliente")
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

    
}
