package com.example.demo.Modelos.DAO;

import java.util.List;
import com.example.demo.Modelos.Entity.Detalle;

public interface InterfaceDetalleDAO { // interface que metodos puede tener pero no como se implementan
    // Guarda un renglón de la compra.
    void save(Detalle detalle);

    // Busca todos los renglones pertenecientes a un encabezado.
    List<Detalle> findByEncabezadoId(Long encabezadoId);
}
