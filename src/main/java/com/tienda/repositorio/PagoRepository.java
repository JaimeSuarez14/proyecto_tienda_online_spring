// Paquete donde se ubicarán tus repositorios
package com.tienda.repositorio;

import com.tienda.modelo.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime; // Importar LocalDateTime
import java.util.List;
// import java.util.Map; // Eliminado: no se usa en esta interfaz

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    // Buscar todos los pagos de un usuario (ya existente)
    List<Pago> findByUsuario(com.tienda.modelo.Usuario usuario);

    /**
     * Busca pagos dentro de un rango de fechas usando LocalDateTime.
     * @param startDate Fecha y hora de inicio.
     * @param endDate Fecha y hora de fin.
     * @return Una lista de pagos dentro del rango especificado.
     */
    List<Pago> findByFechaVentaBetween(LocalDateTime startDate, LocalDateTime endDate); // CORREGIDO PARA LocalDateTime

    // Consulta para obtener el total de ventas por día en un rango de fechas (ya existente)
    @Query("SELECT FUNCTION('DATE', p.fechaVenta) AS saleDate, SUM(p.montoPagado) AS totalAmount " +
           "FROM Pago p " +
           "WHERE FUNCTION('DATE', p.fechaVenta) BETWEEN :startDate AND :endDate " +
           "GROUP BY FUNCTION('DATE', p.fechaVenta) " +
           "ORDER BY saleDate")
    List<Object[]> sumTotalVentasPorDia(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // Consulta para obtener el total de ventas por mes en un año específico (ya existente)
    @Query("SELECT FUNCTION('MONTH', p.fechaVenta) AS saleMonth, SUM(p.montoPagado) AS totalAmount " +
           "FROM Pago p " +
           "WHERE FUNCTION('YEAR', p.fechaVenta) = :year " +
           "GROUP BY FUNCTION('MONTH', p.fechaVenta) " +
           "ORDER BY saleMonth")
    List<Object[]> sumTotalVentasPorMes(@Param("year") int year);

    // La consulta de productos más vendidos se movió a DetalleVentaRepository
    // @Query("SELECT dv.producto.nombre, SUM(dv.cantidad) AS totalCantidad " +
    //        "FROM DetalleVenta dv " +
    //        "GROUP BY dv.producto " +
    //        "ORDER BY totalCantidad DESC")
    // List<Object[]> findProductosMasVendidos();
}