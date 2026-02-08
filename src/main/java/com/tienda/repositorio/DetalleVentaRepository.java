// Paquete donde se ubicarán tus repositorios
package com.tienda.repositorio;

import com.tienda.modelo.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {
    List<DetalleVenta> findByPagoId(Long pagoId);

    /**
     * Consulta para obtener los productos más vendidos.
     * Agrupa por producto y suma las cantidades vendidas, ordenando de forma descendente.
     * @param limit El número de productos más vendidos a retornar.
     * @return Una lista de arrays de objetos, donde cada array contiene [Producto, cantidadTotalVendida].
     */
    @Query(value = "SELECT dv.producto, SUM(dv.cantidad) FROM DetalleVenta dv GROUP BY dv.producto ORDER BY SUM(dv.cantidad) DESC LIMIT :limit")
    List<Object[]> findTopSellingProducts(int limit);
}
