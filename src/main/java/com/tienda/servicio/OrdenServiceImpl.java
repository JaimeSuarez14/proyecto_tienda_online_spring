package com.tienda.servicio;

import com.tienda.modelo.*;
import com.tienda.repositorio.OrdenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrdenServiceImpl implements OrdenService {

    @Autowired
    private OrdenRepository ordenRepository;

    @Autowired
    private CarritoService carritoService;

    /**
     * Crea una nueva orden a partir del carrito del usuario.
     * @param usuario El usuario que crea la orden.
     * @return La orden creada.
     */
    @Override
    public Orden crearOrdenDesdeCarrito(Usuario usuario) {
        // Obtener los items del carrito del usuario
        List<Carrito> carritoItems = carritoService.findByUsuario(usuario);

        if (carritoItems.isEmpty()) {
            throw new IllegalArgumentException("No hay productos en el carrito para crear una orden");
        }

        // Generar número único de orden
        String numeroOrden = generarNumeroOrden();

        // Crear la orden
        Orden orden = new Orden(usuario, numeroOrden);

        // Calcular totales
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalDescuentoProductos = BigDecimal.ZERO;
        List<DetalleOrden> detalles = new ArrayList<>();

        for (Carrito item : carritoItems) {
            Producto producto = item.getProducto();
            int cantidad = item.getCantidad();
            BigDecimal precioUnitario = producto.getPrecio();
            BigDecimal descuentoUnitario = producto.getDescuento();

            // Crear detalle de orden
            DetalleOrden detalle = new DetalleOrden(orden, producto, cantidad, precioUnitario, descuentoUnitario);
            detalles.add(detalle);

            // Calcular subtotal y descuentos
            subtotal = subtotal.add(precioUnitario.multiply(BigDecimal.valueOf(cantidad)));
            BigDecimal descuentoItem = precioUnitario.multiply(descuentoUnitario).divide(BigDecimal.valueOf(100));
            totalDescuentoProductos = totalDescuentoProductos.add(descuentoItem.multiply(BigDecimal.valueOf(cantidad)));
        }

        orden.setDetalles(detalles);
        orden.setSubtotal(subtotal);
        orden.setTotalDescuentoProductos(totalDescuentoProductos);

        // Aplicar cupón si existe
        BigDecimal descuentoCupon = BigDecimal.ZERO;
        if (!carritoItems.isEmpty() && carritoItems.get(0).getCupon() != null) {
            Cupon cupon = carritoItems.get(0).getCupon();
            orden.setCupon(cupon);

            // Calcular descuento del cupón sobre el total con descuentos de productos
            BigDecimal totalConDescuentoProductos = subtotal.subtract(totalDescuentoProductos);
            descuentoCupon = totalConDescuentoProductos.multiply(BigDecimal.valueOf(cupon.getPorcentajeDescuento())).divide(BigDecimal.valueOf(100));
        }

        orden.setDescuentoCupon(descuentoCupon);
        BigDecimal totalFinal = subtotal.subtract(totalDescuentoProductos).subtract(descuentoCupon);
        orden.setTotal(totalFinal);

        // Guardar la orden
        Orden ordenGuardada = ordenRepository.save(orden);

        // Limpiar el carrito después de crear la orden
        carritoService.limpiarCarrito(usuario);

        return ordenGuardada;
    }

    /**
     * Busca una orden por su número único.
     * @param numeroOrden El número de la orden.
     * @return La orden encontrada, o Optional.empty() si no existe.
     */
    @Override
    public Optional<Orden> buscarPorNumeroOrden(String numeroOrden) {
        return ordenRepository.findByNumeroOrden(numeroOrden);
    }

    /**
     * Obtiene todas las órdenes de un usuario.
     * @param usuario El usuario cuyas órdenes se desean obtener.
     * @return Lista de órdenes del usuario.
     */
    @Override
    public List<Orden> obtenerOrdenesPorUsuario(Usuario usuario) {
        return ordenRepository.findByUsuarioOrderByFechaDesc(usuario);
    }

    /**
     * Actualiza el estado de una orden.
     * @param idOrden El ID de la orden.
     * @param estado El nuevo estado.
     */
    @SuppressWarnings("null")
    @Override
    public void actualizarEstado(Long idOrden, Orden.EstadoOrden estado) {
        Optional<Orden> ordenOptional = ordenRepository.findById(idOrden);
        if (ordenOptional.isPresent()) {
            Orden orden = ordenOptional.get();
            orden.setEstado(estado);
            ordenRepository.save(orden);
        }
    }

    /**
     * Genera un número de orden único.
     * @return Número de orden único.
     */
    @Override
    public String generarNumeroOrden() {
        // Formato: ORD-YYYYMMDD-HHMMSS-random
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = ahora.format(formatter);
        
        // Agregar un número aleatorio para más unicidad
        long random = (long)(Math.random() * 100000);
        
        return "ORD-" + timestamp + "-" + String.format("%05d", random);
    }

    /**
     * Obtiene una orden por ID.
     * @param id El ID de la orden.
     * @return La orden encontrada, o Optional.empty() si no existe.
     */
    @SuppressWarnings("null")
    @Override
    public Optional<Orden> obtenerPorId(Long id) {
        return ordenRepository.findById(id);
    }
}
