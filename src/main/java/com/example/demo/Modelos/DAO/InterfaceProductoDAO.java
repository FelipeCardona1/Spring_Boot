package com.example.demo.Modelos.DAO;

import java.util.List;

import com.example.demo.Modelos.Entity.Producto;

public interface InterfaceProductoDAO {
    public List<Producto> findAll();
    public void save(Producto producto);
    public Producto findOne(Long id);
    public void delete(Long id);
}
