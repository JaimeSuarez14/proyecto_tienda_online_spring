// Paquete donde se ubicarán tus controladores
package com.tienda.controlador;

import com.tienda.modelo.Carrito;
import com.tienda.modelo.Usuario;
import com.tienda.servicio.CarritoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    /**
     * Muestra la página del carrito de compras del usuario autenticado.
     * Calcula el subtotal, descuento y total a pagar.
     * @param model El objeto Model para pasar datos a la vista.
     * @param session La sesión HTTP para obtener el usuario autenticado.
     * @return El nombre de la plantilla Thymeleaf para la página del carrito.
     */
    @GetMapping("/carrito")
    public String verCarrito(Model model, HttpSession session) {
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuarioAutenticado");

        if (usuarioAutenticado == null) {
            return "redirect:/login"; // Redirigir al login si no está autenticado
        }

        List<Carrito> carritoItems = carritoService.findByUsuario(usuarioAutenticado);
        model.addAttribute("carritoItems", carritoItems);

        // Calcular totales
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalDescuento = BigDecimal.ZERO;

        for (Carrito item : carritoItems) {
            BigDecimal precioUnitario = item.getProducto().getPrecio();
            BigDecimal descuentoUnitario = item.getProducto().getDescuento(); // Esto es un porcentaje, ej. 10.00 para 10%
            BigDecimal cantidad = BigDecimal.valueOf(item.getCantidad());

            // Calcular precio con descuento
            BigDecimal precioConDescuento = precioUnitario.multiply(BigDecimal.ONE.subtract(descuentoUnitario.divide(BigDecimal.valueOf(100))));

            subtotal = subtotal.add(precioUnitario.multiply(cantidad));
            totalDescuento = totalDescuento.add((precioUnitario.subtract(precioConDescuento)).multiply(cantidad));
        }

        BigDecimal totalConDescuento = subtotal.subtract(totalDescuento);

        model.addAttribute("totalCarrito", subtotal);
        model.addAttribute("totalDescuento", totalDescuento);
        model.addAttribute("totalConDescuento", totalConDescuento);

        // Actualizar el contador del carrito en la navbar
        int carritoCount = carritoService.countItemsInCarrito(usuarioAutenticado);
        model.addAttribute("carritoCount", carritoCount);

        return "carrito"; // Esto buscará src/main/resources/templates/carrito.html
    }
}
