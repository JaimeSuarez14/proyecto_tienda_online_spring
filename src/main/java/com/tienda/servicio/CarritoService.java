package com.tienda.servicio;

import com.tienda.modelo.Carrito;
import com.tienda.modelo.Producto;
import com.tienda.modelo.Usuario;
import java.util.List;
import java.util.Optional;

public interface CarritoService {

    /**
     * Agrega un producto al carrito de un usuario. Si el producto ya existe en el carrito,
     * aumenta la cantidad.
     * @param usuario El usuario al que se le agregará el producto0.
     * @param producto El producto a agregar.
     * @param cantidad La cantidad a agregar.
     * @return El objeto Carrito actualizado o recién creado.
     */
    Carrito agregarProductoAlCarrito(Usuario usuario, Producto producto, int cantidad);

    /**
     * Obtiene todos los ítems del carrito de un usuario.
     * @param usuario El usuario cuyo carrito se desea obtener.
     * @return Una lista de ítems del carrito.
     */
    List<Carrito> findByUsuario(Usuario usuario);

    /**
     * Cuenta la cantidad total de ítems en el carrito de un usuario.
     * @param usuario El usuario cuyo carrito se desea contar.
     * @return La cantidad total de ítems.
     */
    int countItemsInCarrito(Usuario usuario);

    /**
     * Elimina un ítem del carrito por su ID.
     * @param carritoId El ID del ítem del carrito a eliminar.
     */
    void eliminarItemDelCarrito(Long carritoId);

    /**
     * Actualiza la cantidad de un producto en el carrito.
     * @param carritoId El ID del ítem del carrito a actualizar.
     * @param cantidad La nueva cantidad.
     * @return El objeto Carrito actualizado, o Optional.empty() si no se encuentra.
     */
    Optional<Carrito> actualizarCantidadEnCarrito(Long carritoId, int cantidad);

    /**
     * Limpia completamente el carrito de un usuario.
     * @param usuario El usuario cuyo carrito se limpiará.
     */
    void limpiarCarrito(Usuario usuario);
}
