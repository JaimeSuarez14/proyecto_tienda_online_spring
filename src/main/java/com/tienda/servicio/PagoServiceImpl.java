// Paquete donde se ubicarán tus servicios
package com.tienda.servicio;

import com.tienda.modelo.Carrito;
import com.tienda.modelo.DetalleOrden;
import com.tienda.modelo.DetalleVenta;
import com.tienda.modelo.Pago;
import com.tienda.modelo.Producto;
import com.tienda.modelo.Usuario;
import com.tienda.repositorio.PagoRepository;
import com.tienda.repositorio.DetalleVentaRepository;
import com.tienda.repositorio.ProductoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
// import java.sql.Timestamp; // Ya no es necesario si no se usa para conversiones explícitas
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PagoServiceImpl implements PagoService {

    @Autowired
    private PagoRepository pagoRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private CarritoService carritoService;

    @Override
    @Transactional
    public Pago procesarPago(Usuario usuario, Pago.MetodoPago metodoPago, BigDecimal totalPagar, String numeroBoleta,
            List<Carrito> carritoItems) {
        System.out.println("Procesando pago para usuario: " + usuario.getCorreo());
        if (carritoItems.isEmpty()) {
            throw new RuntimeException("El carrito está vacío, no se puede procesar el pago.");
        }

        for (Carrito item : carritoItems) {
            Producto producto = item.getProducto();
            int cantidadSolicitada = item.getCantidad();

            Optional<Producto> productoDbOptional = productoRepository.findById(producto.getId());
            if (productoDbOptional.isEmpty()) {
                throw new RuntimeException("Producto con ID " + producto.getId() + " no encontrado.");
            }
            Producto productoDb = productoDbOptional.get();

            if (productoDb.getStock() < cantidadSolicitada) {
                throw new RuntimeException("Stock insuficiente para el producto: " + productoDb.getNombre()
                        + ". Stock disponible: " + productoDb.getStock());
            }
            productoDb.setStock(productoDb.getStock() - cantidadSolicitada);
            productoRepository.save(productoDb);
        }

        Pago pago = new Pago();
        pago.setUsuario(usuario);
        pago.setMontoPagado(totalPagar);
        pago.setMetodoPago(metodoPago);
        pago.setFechaVenta(LocalDateTime.now()); // Usar LocalDateTime directamente
        pago.setNumeroBoleta(numeroBoleta);
        pago.setEstado(Pago.EstadoPago.completado);

        Pago savedPago = pagoRepository.save(pago);

        for (Carrito item : carritoItems) {
            DetalleVenta detalleVenta = new DetalleVenta();
            detalleVenta.setPago(savedPago);
            detalleVenta.setProducto(item.getProducto());
            detalleVenta.setCantidad(item.getCantidad());
            BigDecimal precioUnitarioFinal = item.getProducto().getPrecio()
                    .multiply(
                            BigDecimal.ONE.subtract(item.getProducto().getDescuento().divide(BigDecimal.valueOf(100))));
            detalleVenta.setPrecioUnitario(precioUnitarioFinal);
            detalleVentaRepository.save(detalleVenta);
        }

        carritoService.limpiarCarrito(usuario);

        return savedPago;
    }

    @Override
    public Optional<Pago> findPagoById(Long id) {
        return pagoRepository.findById(id);
    }

    @Override
    public List<Pago> findPagosByUsuario(Usuario usuario) {
        return pagoRepository.findByUsuario(usuario);
    }

    /**
     * Obtiene el total de ventas por día en un rango de fechas.
     * 
     * @param startDate Fecha de inicio del rango.
     * @param endDate   Fecha de fin del rango.
     * @return Un mapa donde la clave es la fecha (LocalDate) y el valor es el total
     *         de ventas (BigDecimal).
     */
    @Override
    public Map<LocalDate, BigDecimal> getTotalSalesByDay(LocalDate startDate, LocalDate endDate) {
        List<Pago> pagos = pagoRepository.findByFechaVentaBetween(
                startDate.atStartOfDay(), // Pasar LocalDateTime directamente
                endDate.atStartOfDay().plusDays(1).minusNanos(1) // Hasta el final del día
        );

        // Agrupar por fecha y sumar montos, añadiendo null check para fechaVenta
        Map<LocalDate, BigDecimal> salesByDay = pagos.stream()
                .filter(pago -> pago.getFechaVenta() != null) // Asegurarse de que fechaVenta no sea null
                .collect(Collectors.groupingBy(
                        pago -> pago.getFechaVenta().toLocalDate(), // Usar toLocalDate() directamente
                        Collectors.reducing(BigDecimal.ZERO, Pago::getMontoPagado, BigDecimal::add)));

        // Asegurarse de que todas las fechas en el rango tengan un valor (0 si no hay
        // ventas)
        Map<LocalDate, BigDecimal> fullRangeSales = new LinkedHashMap<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            fullRangeSales.put(date, salesByDay.getOrDefault(date, BigDecimal.ZERO));
        }
        return fullRangeSales;
    }

    /**
     * Obtiene el total de ventas por mes en un año específico.
     * 
     * @param year El año para el que se desean las ventas.
     * @return Un mapa donde la clave es el número del mes (Integer, 1-12) y el
     *         valor es el total de ventas (BigDecimal).
     */
    @Override
    public Map<Integer, BigDecimal> getTotalSalesByMonth(int year) {
        // Obtener todos los pagos del año
        LocalDateTime startOfYear = LocalDateTime.of(year, 1, 1, 0, 0);
        LocalDateTime endOfYear = LocalDateTime.of(year, 12, 31, 23, 59, 59);
        List<Pago> pagos = pagoRepository.findByFechaVentaBetween(
                startOfYear, // Pasar LocalDateTime directamente
                endOfYear // Pasar LocalDateTime directamente
        );

        // Agrupar por mes y sumar montos, añadiendo null check para fechaVenta
        Map<Integer, BigDecimal> salesByMonth = pagos.stream()
                .filter(pago -> pago.getFechaVenta() != null) // Asegurarse de que fechaVenta no sea null
                .collect(Collectors.groupingBy(
                        pago -> pago.getFechaVenta().getMonthValue(), // Usar getMonthValue() directamente
                        Collectors.reducing(BigDecimal.ZERO, Pago::getMontoPagado, BigDecimal::add)));

        // Rellenar con 0 para los meses sin ventas
        Map<Integer, BigDecimal> fullYearSales = new LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) {
            fullYearSales.put(i, salesByMonth.getOrDefault(i, BigDecimal.ZERO));
        }
        return fullYearSales;
    }

    /**
     * Obtiene los productos más vendidos (top N) en un período.
     * Esto requiere una consulta personalizada.
     * 
     * @param limit El número máximo de productos a retornar.
     * @return Una lista de objetos que representan los productos más vendidos (ej.
     *         Producto con cantidad vendida).
     */
    @Override
    public List<Object[]> getMostSoldProducts(int limit) {
        return detalleVentaRepository.findTopSellingProducts(PageRequest.of(0, limit));
    }

    @Override
    public List<Pago> findAllPagos() {
        return pagoRepository.findAll();
    }

    @Override
    @Transactional
    public Pago procesarPagoConOrden(Usuario usuario, Pago.MetodoPago metodoPago, com.tienda.modelo.Orden orden) {
        System.out.println("LOG-SERVICE: Iniciando procesarPagoConOrden para Orden N°: " + orden.getNumeroOrden());

        // 1. Validar stock y actualizarlo
        System.out.println("LOG-SERVICE: Validando y actualizando stock...");
        for (DetalleOrden detalleOrden : orden.getDetalles()) {
            Producto producto = detalleOrden.getProducto();
            int cantidadSolicitada = detalleOrden.getCantidad();
            System.out.println("LOG-SERVICE: > Procesando producto: " + producto.getNombre() + " (ID: " + producto.getId() + "), Cantidad: " + cantidadSolicitada);

            Optional<Producto> productoDbOptional = productoRepository.findById(producto.getId());
            if (productoDbOptional.isEmpty()) {
                System.out.println("LOG-SERVICE: ERROR - Producto con ID " + producto.getId() + " no encontrado.");
                throw new RuntimeException("Producto con ID " + producto.getId() + " no encontrado.");
            }
            Producto productoDb = productoDbOptional.get();

            if (productoDb.getStock() < cantidadSolicitada) {
                System.out.println("LOG-SERVICE: ERROR - Stock insuficiente para " + productoDb.getNombre() + ". Solicitado: " + cantidadSolicitada + ", Disponible: " + productoDb.getStock());
                throw new RuntimeException("Stock insuficiente para el producto: " + productoDb.getNombre() + ". Stock disponible: " + productoDb.getStock());
            }
            productoDb.setStock(productoDb.getStock() - cantidadSolicitada);
            productoRepository.save(productoDb);
            System.out.println("LOG-SERVICE: > Stock actualizado para " + producto.getNombre() + ". Nuevo stock: " + productoDb.getStock());
        }
        System.out.println("LOG-SERVICE: Stock validado y actualizado correctamente.");

        // 2. Crear el registro de Pago
        System.out.println("LOG-SERVICE: Creando registro de Pago...");
        Pago pago = new Pago();
        pago.setUsuario(usuario);
        pago.setMontoPagado(orden.getTotal());
        pago.setMetodoPago(metodoPago);
        pago.setFechaVenta(LocalDateTime.now());
        pago.setNumeroBoleta(orden.getNumeroOrden()); // Usar el número de orden como referencia
        pago.setEstado(Pago.EstadoPago.completado);
        Pago savedPago = pagoRepository.save(pago);
        System.out.println("LOG-SERVICE: Registro de Pago creado con ID: " + savedPago.getId());

        // 3. Crear DetalleVenta a partir de DetalleOrden
        System.out.println("LOG-SERVICE: Creando registros de DetalleVenta...");
        for (DetalleOrden detalleOrden : orden.getDetalles()) {
            // ¡CORRECCIÓN! Usar el producto completo de la BD
            Producto productoDb = productoRepository.findById(detalleOrden.getProducto().getId()).orElseThrow();

            DetalleVenta detalleVenta = new DetalleVenta();
            detalleVenta.setPago(savedPago);
            detalleVenta.setProducto(productoDb); // Usar el producto completo
            detalleVenta.setCantidad(detalleOrden.getCantidad());
            
            // Calcular el precio final con descuento para guardarlo en el detalle de la venta
            BigDecimal precioConDescuento = detalleOrden.getPrecioUnitario()
                .multiply(BigDecimal.ONE.subtract(detalleOrden.getDescuentoUnitario().divide(BigDecimal.valueOf(100))));
            detalleVenta.setPrecioUnitario(precioConDescuento);
            
            detalleVentaRepository.save(detalleVenta);
            System.out.println("LOG-SERVICE: > DetalleVenta creado para Producto ID: " + detalleOrden.getProducto().getId());
        }
        System.out.println("LOG-SERVICE: Registros de DetalleVenta creados correctamente.");

        System.out.println("LOG-SERVICE: Proceso de pago para Orden N° " + orden.getNumeroOrden() + " completado.");
        return savedPago;
    }
}