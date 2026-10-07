package com.example.demo.Servicios;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.Modelos.DAO.InterfaceDetalleDAO;
import com.example.demo.Modelos.DAO.InterfaceEncabezadoDAO;
import com.example.demo.Modelos.DAO.InterfaceProductoDAO;
import com.example.demo.Modelos.DTO.LineaCompraDTO;
import com.example.demo.Modelos.Entity.Cliente;
import com.example.demo.Modelos.Entity.Detalle;
import com.example.demo.Modelos.Entity.Encabezado;
import com.example.demo.Modelos.Entity.EstadoCuenta;
import com.example.demo.Modelos.Entity.Producto;
import com.example.demo.Modelos.Entity.Rol;
import com.example.demo.Modelos.Entity.Usuario;
import java.util.Objects;

import com.example.demo.Modelos.DTO.FacturaDTO;

@Service
public class CompraService {
    private final UsuarioService usuarioService;
    private final InterfaceProductoDAO productoDAO;
    private final InterfaceEncabezadoDAO encabezadoDAO;
    private final InterfaceDetalleDAO detalleDAO;

    public CompraService(
            UsuarioService usuarioService,
            InterfaceProductoDAO productoDAO,
            InterfaceEncabezadoDAO encabezadoDAO,
            InterfaceDetalleDAO detalleDAO) {
        this.usuarioService = usuarioService;
        this.productoDAO = productoDAO;
        this.encabezadoDAO = encabezadoDAO;
        this.detalleDAO = detalleDAO;
    }

    /**
     * Registra la compra completa y devuelve el ID del encabezado
     * para poder mostrar su factura después.
     */
    @Transactional
    public Long confirmarCompra(String emailAutenticado, List<LineaCompraDTO> lineas) {
        if (lineas == null || lineas.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar al menos un producto.");
        }

        // El cliente se obtiene de la sesión, nunca de un ID enviado por el formulario.
        Usuario usuario = usuarioService.buscarPorEmail(emailAutenticado)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cuenta."));

        if (usuario.getRol() != Rol.CLIENTE
                || usuario.getEstado() != EstadoCuenta.APROBADO) {
            throw new IllegalArgumentException("Solo un cliente aprobado puede comprar.");
        }

        Cliente cliente = usuario.getCliente();
        if (cliente == null) {
            throw new IllegalArgumentException("La cuenta no tiene un perfil de cliente.");
        }

        // El encabezado se crea primero para obtener su ID.
        Encabezado encabezado = new Encabezado();
        encabezado.setCliente(cliente);
        encabezado.setFecha(LocalDateTime.now());
        encabezado.setTotal(BigDecimal.ZERO.setScale(2));
        encabezadoDAO.save(encabezado);

        BigDecimal totalCompra = BigDecimal.ZERO.setScale(2);

        for (LineaCompraDTO linea : lineas) {
            if (linea == null || linea.getProductoId() == null || linea.getCantidad() <= 0) {
                throw new IllegalArgumentException("Cada producto debe tener una cantidad positiva.");
            }

            // El precio y el stock siempre se leen desde la base de datos.
            Producto producto = productoDAO.findOne(linea.getProductoId());
            if (producto == null) {
                throw new IllegalArgumentException("Uno de los productos ya no existe.");
            }

            if (linea.getCantidad() > producto.getStock()) {
                throw new IllegalArgumentException(
                        "No hay suficiente stock para " + producto.getNombre());
            }

            if (producto.getPrecio() == null) {
                throw new IllegalArgumentException("El producto no tiene un precio válido.");
            }

            BigDecimal precio = BigDecimal.valueOf(producto.getPrecio());
            BigDecimal valor = precio
                    .multiply(BigDecimal.valueOf(linea.getCantidad()))
                    .setScale(2, RoundingMode.HALF_UP);

            // Cada Detalle guarda el producto, la cantidad y el subtotal de esa línea.
            Detalle detalle = new Detalle();
            detalle.setEncabezado(encabezado);
            detalle.setProducto(producto);
            detalle.setCantidad(linea.getCantidad());
            detalle.setValor(valor);
            detalleDAO.save(detalle);

            // Descontamos el stock dentro de la misma transacción.
            producto.setStock(producto.getStock() - linea.getCantidad());
            productoDAO.save(producto);

            totalCompra = totalCompra.add(valor);
        }

        // Guardamos el total calculado con los precios reales de la base de datos.
        encabezado.setTotal(totalCompra);
        encabezadoDAO.save(encabezado);

        return encabezado.getId();
    }

    @Transactional(readOnly = true)
    public FacturaDTO obtenerFactura(Long encabezadoId, String emailAutenticado) {
        // Buscamos la cuenta usando el email de la sesión.
        Usuario usuario = usuarioService.buscarPorEmail(emailAutenticado)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró la cuenta."));

        Encabezado encabezado = encabezadoDAO.findOne(encabezadoId);

        // Usamos el mismo mensaje para no revelar si existe una factura ajena.
        if (encabezado == null
                || usuario.getCliente() == null
                || !Objects.equals(
                        encabezado.getCliente().getId(),
                        usuario.getCliente().getId())) {
            throw new IllegalArgumentException("No se encontró la factura.");
        }

        // Solo consultamos los detalles después de comprobar quién es el dueño.
        List<Detalle> detalles = detalleDAO.findByEncabezadoId(encabezadoId);

        return new FacturaDTO(encabezado, detalles);
    }
}
