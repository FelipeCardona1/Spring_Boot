package com.example.demo.Modelos.Entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.RoundingMode;

@Entity
@Table(name = "Detalles")
public class Detalle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;

    // Varios detalles pueden formar parte del mismo encabezado de compra.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdEncabezado", nullable = false)
    private Encabezado encabezado;

    // Producto comprado en esta línea de la factura.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "IdProducto", nullable = false)
    private Producto producto;

    @Column(name = "Cantidad", nullable = false)
    private int cantidad;

    // Valor almacena el subtotal de esta línea: precio unitario × cantidad.
    @Column(name = "Valor", nullable = false, precision = 12, scale = 2)
    private BigDecimal valor;

    public Detalle() {
    }

    public Long getId() {
        return id;
    }

    public Encabezado getEncabezado() {
        return encabezado;
    }

    public void setEncabezado(Encabezado encabezado) {
        this.encabezado = encabezado;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    // Calcula el precio por unidad a partir del subtotal guardado en el detalle.
    public BigDecimal getPrecioUnitario() {
        if (valor == null || cantidad <= 0) {
            return BigDecimal.ZERO;
        }

        return valor.divide(
                BigDecimal.valueOf(cantidad),
                2,
                RoundingMode.HALF_UP);
    }
}
