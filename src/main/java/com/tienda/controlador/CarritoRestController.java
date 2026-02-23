// Paquete donde se ubicarán tus controladores
package com.tienda.controlador;

import com.tienda.modelo.Producto;
import com.tienda.modelo.Usuario;
import com.tienda.modelo.Carrito;
import com.tienda.servicio.CarritoService;
import com.tienda.servicio.ProductoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping; // Importar DeleteMapping
import org.springframework.web.bind.annotation.PathVariable; // Importar PathVariable

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List; // Importar List
import java.util.Map;
import java.util.Optional;

// Clase DTO para recibir los datos del request de agregar al carrito
class AddToCartRequest {
    private Long productoId;
    private int cantidad;

    // Getters y Setters
    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}

// Clase DTO para recibir los datos del request de actualizar cantidad en el carrito
class UpdateCartItemRequest {
    private Long itemId;
    private int cantidad;

    // Getters y Setters
    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}

@RestController // Indica que es un controlador REST que devuelve datos directamente (JSON, XML)
@RequestMapping("/carrito")
public class CarritoRestController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private ProductoService productoService;

    /**
     * Endpoint para agregar un producto al carrito del usuario autenticado.
     * Recibe un JSON con el ID del producto y la cantidad.
     * @param request El objeto AddToCartRequest con el ID del producto y la cantidad.
     * @param session La sesión HTTP para obtener el usuario autenticado.
     * @return Una respuesta JSON indicando éxito o fracaso, y la cantidad actualizada del carrito.
     */
    @PostMapping("/agregar")
    public ResponseEntity<Map<String, Object>> agregarProductoAlCarrito(@RequestBody AddToCartRequest request, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuarioAutenticado");

        if (usuarioAutenticado == null) {
            response.put("success", false);
            response.put("message", "Usuario no autenticado.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        Optional<Producto> productoOptional = productoService.findProductoById(request.getProductoId());
        if (productoOptional.isEmpty()) {
            response.put("success", false);
            response.put("message", "Producto no encontrado.");
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }

        Producto producto = productoOptional.get();
        if (producto.getStock() < request.getCantidad()) { // CORREGIDO: getStock() ya existe en Producto
            response.put("success", false);
            response.put("message", "No hay suficiente stock disponible para este producto.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            carritoService.agregarProductoAlCarrito(usuarioAutenticado, producto, request.getCantidad());
            int carritoCount = carritoService.countItemsInCarrito(usuarioAutenticado);

            response.put("success", true);
            response.put("message", "Producto agregado al carrito exitosamente.");
            response.put("carritoCount", carritoCount);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error al agregar producto al carrito: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error interno al agregar producto al carrito.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para actualizar la cantidad de un producto en el carrito.
     * @param request El objeto UpdateCartItemRequest con el ID del ítem del carrito y la nueva cantidad.
     * @param session La sesión HTTP para obtener el usuario autenticado.
     * @return Una respuesta JSON con los totales actualizados del carrito.
     */
    @PostMapping("/actualizarCantidad")
    public ResponseEntity<Map<String, Object>> actualizarCantidadEnCarrito(@RequestBody UpdateCartItemRequest request, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuarioAutenticado");

        if (usuarioAutenticado == null) {
            response.put("success", false);
            response.put("message", "Usuario no autenticado.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        try {
            Optional<Carrito> updatedItem = carritoService.actualizarCantidadEnCarrito(request.getItemId(), request.getCantidad());

            if (updatedItem.isPresent()) {
                // Recalcular totales del carrito
                List<Carrito> carritoItems = carritoService.findByUsuario(usuarioAutenticado);
                BigDecimal subtotal = BigDecimal.ZERO;
                BigDecimal totalDescuento = BigDecimal.ZERO;

                for (Carrito item : carritoItems) {
                    BigDecimal precioUnitario = item.getProducto().getPrecio();
                    BigDecimal descuentoUnitario = item.getProducto().getDescuento();
                    BigDecimal precioConDescuento = precioUnitario.multiply(BigDecimal.ONE.subtract(descuentoUnitario.divide(BigDecimal.valueOf(100))));

                    subtotal = subtotal.add(precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad())));
                    totalDescuento = totalDescuento.add((precioUnitario.subtract(precioConDescuento)).multiply(BigDecimal.valueOf(item.getCantidad())));
                }
                BigDecimal totalConDescuento = subtotal.subtract(totalDescuento);
                int carritoCount = carritoService.countItemsInCarrito(usuarioAutenticado);


                response.put("success", true);
                response.put("message", "Cantidad actualizada exitosamente.");
                response.put("newQuantity", updatedItem.get().getCantidad());
                response.put("newSubtotal", subtotal);
                response.put("newDiscount", totalDescuento);
                response.put("newTotal", totalConDescuento);
                response.put("carritoCount", carritoCount);
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                response.put("success", false);
                response.put("message", "Ítem del carrito no encontrado o eliminado.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            System.err.println("Error al actualizar cantidad en el carrito: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error interno al actualizar cantidad en el carrito.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
}
