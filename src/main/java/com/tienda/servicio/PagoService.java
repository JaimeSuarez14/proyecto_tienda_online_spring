// Paquete donde se ubicarán tus servicios
package com.tienda.servicio;

import com.tienda.modelo.Pago;
import com.tienda.modelo.Usuario;
import com.tienda.modelo.Carrito;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface PagoService {

    Pago procesarPago(Usuario usuario, Pago.MetodoPago metodoPago, BigDecimal totalPagar, String numeroBoleta, List<Carrito> carritoItems);
    Optional<Pago> findPagoById(Long id);
    List<Pago> findPagosByUsuario(Usuario usuario);

    /**
     * Obtiene el total de ventas por día en un rango de fechas.
     * @param startDate Fecha de inicio del rango.
     * @param endDate Fecha de fin del rango.
     * @return Un mapa donde la clave es la fecha (LocalDate) y el valor es el total de ventas (BigDecimal).
     */
    Map<LocalDate, BigDecimal> getTotalSalesByDay(LocalDate startDate, LocalDate endDate);

    /**
     * Obtiene el total de ventas por mes en un año específico.
     * @param year El año para el que se desean las ventas.
     * @return Un mapa donde la clave es el número del mes (Integer, 1-12) y el valor es el total de ventas (BigDecimal).
     */
    Map<Integer, BigDecimal> getTotalSalesByMonth(int year);

    /**
     * Obtiene los productos más vendidos (top N) en un período.
     * @param limit El número máximo de productos a retornar.
     * @return Una lista de objetos que representan los productos más vendidos (ej. Producto con cantidad vendida).
     */
    List<Object[]> getMostSoldProducts(int limit);

    /**
     * Obtiene todas las ventas.
     * @return Una lista de todos los objetos Pago.
     */
    List<Pago> findAllPagos();
}
