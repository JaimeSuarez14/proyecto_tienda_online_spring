// Paquete donde se ubicarán tus servicios
package com.tienda.servicio;

import com.tienda.modelo.Carrito;
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

    public void limpiarCarrito(Usuario usuario) {
        System.out.println("Limpiando carrito para usuario: " + usuario.getCorreo());
        List<Carrito> items = carritoRepository.findByUsuario(usuario);
        carritoRepository.deleteAll(items);
    }

    @Override
    public Carrito agregarProductoAlCarrito(Usuario usuario, Producto producto, int cantidad) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'agregarProductoAlCarrito'");
    }

    @Override
    public List<Carrito> findByUsuario(Usuario usuario) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'findByUsuario'");
    }

    @Override
    public int countItemsInCarrito(Usuario usuario) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'countItemsInCarrito'");
    }

    @Override
    public void eliminarItemDelCarrito(Long carritoId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'eliminarItemDelCarrito'");
    }

    @Override
    public Optional<Carrito> actualizarCantidadEnCarrito(Long carritoId, int cantidad) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'actualizarCantidadEnCarrito'");
    }
}
