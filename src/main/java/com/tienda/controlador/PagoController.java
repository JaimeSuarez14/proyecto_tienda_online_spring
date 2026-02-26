// Paquete donde se ubicarán tus controladores
package com.tienda.controlador;

import com.tienda.modelo.Carrito;
import com.tienda.modelo.Orden;
import com.tienda.modelo.Pago;
import com.tienda.modelo.Usuario;
import com.tienda.servicio.CarritoService;
import com.tienda.servicio.OrdenService;
import com.tienda.servicio.PagoService;
import com.tienda.servicio.PasarelaPagoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ModelAttribute; // Importar ModelAttribute

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID; // Para generar el número de boleta

@Controller
public class PagoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private PagoService pagoService;

    @Autowired
    private PasarelaPagoService pasarelaPagoService;

    @Autowired
    private OrdenService ordenService;

    /**
     * Endpoint para mostrar la página de pago con el resumen del pedido.
     * @param numeroOrden El número de orden desde la confirmación.
     * @param metodo El método de pago seleccionado desde el carrito.
     * @param model El objeto Model para pasar datos a la vista.
     * @param session La sesión HTTP para obtener el usuario autenticado y los ítems del carrito.
     * @param redirectAttributes Para añadir mensajes flash en caso de errores.
     * @return El nombre de la plantilla Thymeleaf para la página de pago.
     */
    @GetMapping("/pago")
    public String showPaymentPage(
            @RequestParam(value = "numeroOrden", required = false) String numeroOrden,
            @RequestParam(value = "metodo", required = false) String metodo, 
            Model model, 
            HttpSession session, 
            RedirectAttributes redirectAttributes) {
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuarioAutenticado");

        if (usuarioAutenticado == null) {
            return "redirect:/login"; // Redirigir al login si no está autenticado
        }

        // Validar que se haya enviado el método de pago
        if (metodo == null || metodo.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debes seleccionar un método de pago.");
            return "redirect:/carrito";
        }

        // Si viene el número de orden, cargar la orden desde la base de datos
        Orden orden = null;
        if (numeroOrden != null && !numeroOrden.isEmpty()) {
            Optional<Orden> ordenOptional = ordenService.buscarPorNumeroOrden(numeroOrden);
            if (ordenOptional.isPresent()) {
                orden = ordenOptional.get();
            }
        }

        List<Carrito> carritoItems = carritoService.findByUsuario(usuarioAutenticado);

        if (carritoItems.isEmpty() && orden == null) {
            redirectAttributes.addFlashAttribute("error", "Tu carrito está vacío. Agrega productos antes de pagar.");
            return "redirect:/carrito";
        }

        // Recalcular el total a pagar (para asegurar que sea el valor más reciente)
        BigDecimal totalConDescuento;
        
        if (orden != null) {
            // Usar el total de la orden
            totalConDescuento = orden.getTotal();
        } else {
            // Calcular desde el carrito
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

            totalConDescuento = subtotal.subtract(totalDescuento);
        }

        // Generar un número de boleta simple (puedes usar una lógica más robusta en producción)
        String numeroBoleta = "BOLETA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // Procesar la pasarela de pago
        Pago.MetodoPago metodoPagoEnum = Pago.MetodoPago.valueOf(metodo.toLowerCase());
        Map<String, Object> resultadoPasarela = pasarelaPagoService.procesarPago(metodoPagoEnum, totalConDescuento, numeroBoleta);
        
        model.addAttribute("totalPagar", totalConDescuento);
        model.addAttribute("metodoPago", metodo);
        model.addAttribute("numeroBoleta", numeroBoleta);
        model.addAttribute("numeroOrden", numeroOrden);
        model.addAttribute("carritoCount", carritoService.countItemsInCarrito(usuarioAutenticado));
        model.addAttribute("resultadoPasarela", resultadoPasarela);

        return "pago"; // Esto buscará src/main/resources/templates/pago.html
    }

    /**
     * Endpoint para procesar la confirmación del pago.
     * @param numeroOrden El número de orden (parámetro adicional).
     * @param metodoPago El método de pago.
     * @param totalPagar El monto total a pagar.
     * @param numeroBoleta El número de boleta.
     * @param session La sesión HTTP para obtener el usuario autenticado.
     * @param redirectAttributes Para añadir mensajes flash.
     * @return Una redirección a una página de confirmación o de error.
     */
    @PostMapping("/pago/confirmar")
    public String confirmPayment(
            @RequestParam(value = "numeroOrden", required = false) String numeroOrden,
            @RequestParam("metodoPago") String metodoPago,
            @RequestParam("totalPagar") BigDecimal totalPagar,
            @RequestParam("numeroBoleta") String numeroBoleta,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuarioAutenticado");

        if (usuarioAutenticado == null) {
            return "redirect:/login";
        }

        List<Carrito> carritoItems = carritoService.findByUsuario(usuarioAutenticado);

        if (carritoItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Tu carrito está vacío. No se pudo procesar el pago.");
            return "redirect:/carrito";
        }

        try {
            // Convertir el string del método de pago a su Enum correspondiente
            Pago.MetodoPago metodoPagoEnum = Pago.MetodoPago.valueOf(metodoPago.toLowerCase());

            pagoService.procesarPago(usuarioAutenticado, metodoPagoEnum, totalPagar, numeroBoleta, carritoItems);

            // Si hay número de orden, actualizarla a estado PAGADA
            if (numeroOrden != null && !numeroOrden.isEmpty()) {
                Optional<Orden> ordenOptional = ordenService.buscarPorNumeroOrden(numeroOrden);
                if (ordenOptional.isPresent()) {
                    Orden orden = ordenOptional.get();
                    ordenService.actualizarEstado(orden.getId(), Orden.EstadoOrden.pagada);
                }
            }

            redirectAttributes.addFlashAttribute("numeroBoleta", numeroBoleta); // Pasar la boleta a la página de éxito
            return "redirect:/pago_exitoso"; // Redirigir a la nueva página de éxito de pago
        } catch (RuntimeException e) { // Este catch ya maneja IllegalArgumentException
            System.err.println("Error al procesar el pago: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al procesar el pago: " + e.getMessage());
            return "redirect:/carrito"; // Volver al carrito con un mensaje de error
        }
        // El bloque catch (IllegalArgumentException e) ya no es necesario aquí
        // porque RuntimeException es su superclase y ya lo captura.
    }

    /**
     * Endpoint para mostrar la página de éxito de pago.
     * @param model El objeto Model para pasar datos a la vista.
     * @param session La sesión HTTP para obtener el usuario autenticado y el contador del carrito.
     * @param numeroBoleta El número de boleta de la transacción exitosa (viene como flash attribute).
     * @return El nombre de la plantilla Thymeleaf para la página de éxito de pago.
     */
    @GetMapping("/pago_exitoso")
    public String showPaymentSuccess(Model model, HttpSession session, @ModelAttribute("numeroBoleta") String numeroBoleta) {
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuarioAutenticado");
        if (usuarioAutenticado == null) {
            return "redirect:/login";
        }
        model.addAttribute("numeroBoleta", numeroBoleta);
        model.addAttribute("carritoCount", carritoService.countItemsInCarrito(usuarioAutenticado));
        return "pago_exitoso";
    }
}
