// Paquete donde se ubicarán tus servicios
package com.tienda.servicio;

import com.tienda.modelo.CategoriaDTO;
import com.tienda.modelo.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Producto> findAllProductos();
    Optional<Producto> findProductoById(Long id);

    /**
     * Guarda un producto nuevo o actualiza uno existente.
     * @param producto El objeto Producto a guardar.
     * @return El producto guardado.
     */
    Producto saveProducto(Producto producto);

    /**
     * Elimina un producto por su ID.
     * @param id El ID del producto a eliminar.
     */
    void deleteProducto(Long id);

    Page<Producto> findProductosByFilters(String nombre, List<Producto.CategoriaProducto> categorias, Pageable pageable);

    List<CategoriaDTO> getCategoriasWithCounts();
}