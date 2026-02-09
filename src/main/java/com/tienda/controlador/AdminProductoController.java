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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin") // Todas las rutas en este controlador comenzarán con /admin
public class AdminProductoController {

    @Autowired
    private ProductoService productoService;

    /**
     * Muestra la página de gestión de productos para el administrador.
     * @param model El objeto Model para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para la página de gestión de productos.
     */
    @GetMapping("/productos")
    public String gestionarProductos(Model model) {
        // Spring Security ya asegura que solo los ADMINs accedan a esta ruta.
        List<Producto> productos = productoService.findAllProductos();
        model.addAttribute("productos", productos);
        return "/admin/admin_productos"; // Esto buscará src/main/resources/templates/admin/admin_productos.html
    }

    /**
     * Guarda un producto (agrega uno nuevo o edita uno existente).
     * Este endpoint es llamado vía AJAX desde el formulario modal.
     * @param producto El objeto Producto enviado desde el formulario.
     * @return Una respuesta JSON indicando éxito o fracaso.
     */
    @PostMapping("/productos/guardar")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> guardarProducto(@RequestBody Producto producto) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (producto.getGenero() == null) {
                producto.setGenero("");
            }
            if (producto.getTalla() == null) {
                producto.setTalla("");
            }
            if (producto.getTela() == null) {
                producto.setTela("");
            }
            productoService.saveProducto(producto);
            response.put("success", true);
            response.put("message", "Producto guardado exitosamente.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Error de datos: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Error al guardar producto: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error interno al guardar el producto.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }



}
