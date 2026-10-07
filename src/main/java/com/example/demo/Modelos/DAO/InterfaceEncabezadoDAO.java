package com.example.demo.Modelos.DAO;

import com.example.demo.Modelos.Entity.Encabezado;

public interface InterfaceEncabezadoDAO {

    // Guarda un encabezado nuevo o actualiza uno existente.
    void save(Encabezado encabezado);

    // Busca un encabezado por su ID.
    Encabezado findOne(Long id);
}