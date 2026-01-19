// Paquete donde se ubicarán tus repositorios
package com.tienda.repositorio;

import com.tienda.modelo.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByNombreContainingIgnoreCase(String nombre);
    List<Producto> findByPrecioBetween(java.math.BigDecimal minPrecio, java.math.BigDecimal maxPrecio);

    @Query("SELECT p FROM Producto p WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:minPrecio IS NULL OR p.precio >= :minPrecio) " +
           "AND (:maxPrecio IS NULL OR p.precio <= :maxPrecio)")
    List<Producto> findAllByFilters(@Param("nombre") String nombre,
                                   @Param("minPrecio") java.math.BigDecimal minPrecio,
                                   @Param("maxPrecio") java.math.BigDecimal maxPrecio);

    // Ejemplo de consulta para productos con descuento
    List<Producto> findByDescuentoGreaterThan(java.math.BigDecimal descuento);

    // Ejemplo para obtener productos ordenados por precio
    List<Producto> findAllByOrderByPrecioAsc();
    List<Producto> findAllByOrderByPrecioDesc();

    @Query("SELECT p FROM Producto p WHERE (:nombre IS NULL OR LOWER(p.nombre) LIKE LOWER(CONCAT('%', :nombre, '%'))) " +
           "AND (:categorias IS NULL OR p.categoria IN :categorias)")
    Page<Producto> findByFilters(@Param("nombre") String nombre, @Param("categorias") List<Producto.CategoriaProducto> categorias, Pageable pageable);

    @Query("SELECT p.categoria, COUNT(p) FROM Producto p GROUP BY p.categoria")
    List<Object[]> getCategoryCounts();
}
