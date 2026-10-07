package com.example.demo.Modelos.DTO;

import java.util.List;

import com.example.demo.Modelos.Entity.Detalle;
import com.example.demo.Modelos.Entity.Encabezado;

public class FacturaDTO {

    private final Encabezado encabezado;
    private final List<Detalle> detalles;

    public FacturaDTO(Encabezado encabezado, List<Detalle> detalles) {
        this.encabezado = encabezado;
        this.detalles = detalles;
    }

    public Encabezado getEncabezado() {
        return encabezado;
    }

    public List<Detalle> getDetalles() {
        return detalles;
    }
}
