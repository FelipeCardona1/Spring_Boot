package com.example.demo.Modelos.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity 
@Table (name = "productos")
public class Producto {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String Nombre,Descripcion;
    private Double Precio;
    private int Stock;

    
    public Producto(Long id, String nombre, String descripcion, Double precio, int stock) {
        Id = id;
        Nombre = nombre;
        Descripcion = descripcion;
        Precio = precio;
        Stock = stock;
    }

    public Producto() {
    }

    public Long getId() {
        return Id;
    }
    public void setId(Long id) {
        Id = id;
    }
    public String getNombre() {
        return Nombre;
    }
    public void setNombre(String nombre) {
        Nombre = nombre;
    }
    public String getDescripcion() {
        return Descripcion;
    }
    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }
    public Double getPrecio() {
        return Precio;
    }
    public void setPrecio(Double precio) {
        Precio = precio;
    }
    public int getStock() {
        return Stock;
    }
    public void setStock(int stock) {
        Stock = stock;
    }

    
}
