package com.example.demo.Controllers;

import java.util.Date;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.demo.Modelos.Entity.Cliente;

@Controller
@RequestMapping("/Cliente")
public class ClienteController {
    
    @GetMapping("/listar")
    public String Listar(Model model){

        model.addAttribute("titulo", "Listar Clientes");

        Cliente C1 = new Cliente(152L, "Juan", "Cardona", "f1@gmail.com", new Date());
        model.addAttribute("cliente", C1);

        return "listar";
    }
}
