package com.example.demo.Modelos.DTO;

public class LineaCompraDTO {
    // El servicio buscará este producto en la base de datos.
    private Long productoId;

    // El servicio comprobará que sea positiva y no supere el stock.
    private int cantidad;

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
