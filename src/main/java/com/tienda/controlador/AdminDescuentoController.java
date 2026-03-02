// Paquete donde se ubicarán tus controladores
package com.tienda.controlador;

import com.tienda.modelo.Producto;
import com.tienda.servicio.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes; // No se usa, se puede eliminar

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Clase DTO para recibir los datos del request de actualización de descuento
class UpdateDiscountRequest {
    private Long id;
    private BigDecimal descuento;
    private Boolean promocion; // Se mantiene como Boolean para permitir null en el JSON si no se envía

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getDescuento() {
        return descuento;
    }

    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }

    public Boolean getPromocion() {
        return promocion;
    }

    public void setPromocion(Boolean promocion) {
        this.promocion = promocion;
    }
}

@Controller
@RequestMapping("/admin") // Todas las rutas en este controlador comenzarán con /admin
public class AdminDescuentoController {

    @Autowired
    private ProductoService productoService;

    /**
     * Muestra la página de gestión de descuentos para el administrador.
     * @param model El objeto Model para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para la página de gestión de descuentos.
     */
    @GetMapping("/descuentos")
    public String gestionarDescuentos(Model model) {
        // Spring Security ya asegura que solo los ADMINs accedan a esta ruta.
        List<Producto> productos = productoService.findAllProductos();
        model.addAttribute("productos", productos);
        return "admin/admin_descuentos"; // Esto buscará src/main/resources/templates/admin_descuentos.html
    }

    /**
     * Actualiza el descuento y el estado de promoción de un producto.
     * Este endpoint es llamado vía AJAX desde el formulario modal.
     * @param request El objeto UpdateDiscountRequest con el ID del producto, nuevo descuento y estado de promoción.
     * @return Una respuesta JSON indicando éxito o fracaso.
     */
    @PostMapping("/descuentos/actualizar")
    @ResponseBody // Indica que el método devuelve directamente el cuerpo de la respuesta (JSON)
    public ResponseEntity<Map<String, Object>> actualizarDescuento(@RequestBody UpdateDiscountRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Producto> productoOptional = productoService.findProductoById(request.getId());
            if (productoOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "Producto no encontrado.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            Producto producto = productoOptional.get();
            producto.setDescuento(request.getDescuento());
            // Convertir Boolean a boolean primitivo de forma segura
            producto.setPromocion(request.getPromocion() != null ? request.getPromocion() : false); // CORREGIDO

            productoService.saveProducto(producto); // Reutilizamos el método save para actualizar
            response.put("success", true);
            response.put("message", "Descuento y promoción actualizados exitosamente.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error al actualizar descuento: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error interno al actualizar el descuento.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Obtiene los principales productos con descuentos activos.
     * Este endpoint es llamado vía AJAX para mostrar un modal con highlights de descuentos.
     * @return Una lista de productos con descuentos mayores a 0, limitada a los 5 principales (por descuento mayor).
     */
    @GetMapping("/descuentos/principales")
    @ResponseBody
    public ResponseEntity<List<Producto>> getPrincipalesDescuentos() {
        try {
            List<Producto> productos = productoService.findAllProductos();
            // Filtrar productos con descuento > 0 y ordenar por descuento descendente
            List<Producto> productosConDescuento = productos.stream()
                .filter(p -> p.getDescuento() != null && p.getDescuento().doubleValue() > 0)
                .sorted((p1, p2) -> p2.getDescuento().compareTo(p1.getDescuento()))
                .limit(5)
                .toList();
            
            return new ResponseEntity<>(productosConDescuento, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error al obtener descuentos principales: " + e.getMessage());
            return new ResponseEntity<>(List.of(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

 
}
