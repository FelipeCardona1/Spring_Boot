package com.example.demo.Modelos.Entity;

import java.util.Date;


public class Cliente {
    

    private Long Id;
    private String Nombre,Apellido,Email;
    private Date createAt;

    public Cliente(Long id, String nombre, String apellido, String email, Date createAt) {
        Id = id;
        Nombre = nombre;
        Apellido = apellido;
        Email = email;
        this.createAt = createAt;
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
    public String getApellido() {
        return Apellido;
    }
    public void setApellido(String apellido) {
        Apellido = apellido;
    }
    public String getEmail() {
        return Email;
    }
    public void setEmail(String email) {
        Email = email;
    }
    public Date getCreateAt() {
        return createAt;
    }
    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    

}
