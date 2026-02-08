// Paquete donde se ubicarán tus repositorios
package com.tienda.repositorio;

import com.tienda.modelo.Carrito;
import com.tienda.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarritoRepository extends JpaRepository<Carrito, Long> {
    List<Carrito> findByUsuario(Usuario usuario);
    Optional<Carrito> findByUsuarioAndProducto(Usuario usuario, com.tienda.modelo.Producto producto);
    void deleteByUsuarioAndProducto(Usuario usuario, com.tienda.modelo.Producto producto);
    int countByUsuario(Usuario usuario);
}
