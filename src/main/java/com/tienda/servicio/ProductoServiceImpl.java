// Paquete donde se ubicarán tus servicios
package com.tienda.servicio;

import com.tienda.modelo.CategoriaDTO;
import com.tienda.modelo.Producto;
import com.tienda.repositorio.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Producto> findAllProductos() {
        return productoRepository.findAll();
    }

    @Override
    public Optional<Producto> findProductoById(Long id) {
        return productoRepository.findById(id);
    }

    /**
     * Guarda un producto nuevo o actualiza uno existente.
     * @param producto El objeto Producto a guardar.
     * @return El producto guardado.
     */
    @Override
    public Producto saveProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    /**
     * Elimina un producto por su ID.
     * @param id El ID del producto a eliminar.
     */
    @Override
    public void deleteProducto(Long id) {
        productoRepository.deleteById(id);
    }

    @Override
    public Page<Producto> findProductosByFilters(String nombre, List<Producto.CategoriaProducto> categorias, Pageable pageable) {
        return productoRepository.findByFilters(nombre, categorias, pageable);
    }

    @Override
    public List<CategoriaDTO> getCategoriasWithCounts() {
        List<Object[]> results = productoRepository.getCategoryCounts();
        return results.stream()
            .map(obj -> {
                Producto.CategoriaProducto cat = (Producto.CategoriaProducto) obj[0];
                long count = (long) obj[1];
                int id = cat.ordinal() + 1;
                return new CategoriaDTO(id, cat.getDescripcion(), count);
            })
            .collect(Collectors.toList());
    }
}