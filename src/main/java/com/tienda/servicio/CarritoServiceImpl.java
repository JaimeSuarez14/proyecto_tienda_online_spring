// Paquete donde se ubicarán tus servicios
package com.tienda.servicio;

import com.tienda.modelo.Carrito;
import com.tienda.modelo.Cupon;
import com.tienda.modelo.Producto;
import com.tienda.modelo.Usuario;
import com.tienda.repositorio.CarritoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarritoServiceImpl implements CarritoService {

    @Autowired
    private CarritoRepository carritoRepository;

    /**
     * Agrega un producto al carrito de un usuario. Si el producto ya existe en el carrito,
     * aumenta la cantidad.
     * @param usuario El usuario al que se le agregará el producto.
     * @param producto El producto a agregar.
     * @param cantidad La cantidad a agregar.
     * @return El objeto Carrito actualizado o recién creado.
     */
    @Override
    public Carrito agregarProductoAlCarrito(Usuario usuario, Producto producto, int cantidad) {
        // Buscar si el producto ya está en el carrito del usuario
        Optional<Carrito> existingItem = carritoRepository.findByUsuarioAndProducto(usuario, producto);

        Carrito carritoItem;
        if (existingItem.isPresent()) {
            // Si ya existe, actualiza la cantidad
            carritoItem = existingItem.get();
            carritoItem.setCantidad(carritoItem.getCantidad() + cantidad);
        } else {
            // Si no existe, crea un nuevo ítem en el carrito
            carritoItem = new Carrito();
            carritoItem.setUsuario(usuario);
            carritoItem.setProducto(producto);
            carritoItem.setCantidad(cantidad);
        }
        return carritoRepository.save(carritoItem);
    }

    /**
     * Obtiene todos los ítems del carrito de un usuario.
     * @param usuario El usuario cuyo carrito se desea obtener.
     * @return Una lista de ítems del carrito.
     */
    @Override
    public List<Carrito> findByUsuario(Usuario usuario) {
        return carritoRepository.findByUsuario(usuario);
    }

    /**
     * Cuenta la cantidad total de ítems en el carrito de un usuario.
     * @param usuario El usuario cuyo carrito se desea contar.
     * @return La cantidad total de ítems.
     */
    @Override
    public int countItemsInCarrito(Usuario usuario) {
        return carritoRepository.findByUsuario(usuario).stream()
                .mapToInt(Carrito::getCantidad)
                .sum();
    }

    

    
    /**
     * Limpia completamente el carrito de un usuario.
     * @param usuario El usuario cuyo carrito se limpiará.
     */
    @Override
    public void limpiarCarrito(Usuario usuario) {
        System.out.println("Limpiando carrito para usuario: " + usuario.getCorreo());
        List<Carrito> items = carritoRepository.findByUsuario(usuario);
        carritoRepository.deleteAll(items);
    }

    /**
     * Elimina un ítem del carrito por su ID.
     * @param carritoId El ID del ítem del carrito a eliminar.
     */
    @Override
    public void eliminarItemDelCarrito(Long carritoId) {
        carritoRepository.deleteById(carritoId);
    }

    /**
     * Actualiza la cantidad de un producto en el carrito.
     * @param carritoId El ID del ítem del carrito a actualizar.
     * @param cantidad La nueva cantidad.
     * @return El objeto Carrito actualizado, o Optional.empty() si no se encuentra.
     */
    @Override
    public Optional<Carrito> actualizarCantidadEnCarrito(Long carritoId, int cantidad) {
        Optional<Carrito> carritoOptional = carritoRepository.findById(carritoId);
        if (carritoOptional.isPresent()) {
            Carrito carrito = carritoOptional.get();
            if (cantidad > 0) {
                carrito.setCantidad(cantidad);
                return Optional.of(carritoRepository.save(carrito));
            } else {
                // Si la cantidad es 0 o menos, eliminar el ítem del carrito
                carritoRepository.delete(carrito);
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    /**
     * Aplica un cupón al carrito de un usuario.
     * @param usuario El usuario al que se le aplicará el cupón.
     * @param cupon El cupón a aplicar.
     */
    @Override
    public void aplicarCupon(Usuario usuario, Cupon cupon) {
        List<Carrito> items = carritoRepository.findByUsuario(usuario);
        for (Carrito item : items) {
            item.setCupon(cupon);
        }
        carritoRepository.saveAll(items);
    }

    /**
     * Remueve el cupón del carrito de un usuario.
     * @param usuario El usuario al que se le removerá el cupón.
     */
    @Override
    public void removerCupon(Usuario usuario) {
        List<Carrito> items = carritoRepository.findByUsuario(usuario);
        for (Carrito item : items) {
            item.setCupon(null);
        }
        carritoRepository.saveAll(items);
    }
}
