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
            @RequestParam("numeroOrden") String numeroOrden,
            @RequestParam(value = "metodo", required = false) String metodo, 
            Model model, 
            HttpSession session, 
            RedirectAttributes redirectAttributes) {
        System.out.println("LOG: Mostrando página de pago para Orden N°: " + numeroOrden + " con método: " + metodo);

        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuarioAutenticado");

        if (usuarioAutenticado == null) {
            System.out.println("LOG: Usuario no autenticado. Redirigiendo a login.");
            return "redirect:/login"; // Redirigir al login si no está autenticado
        }

        // Validar que se haya enviado el método de pago
        if (metodo == null || metodo.trim().isEmpty()) {
            System.out.println("LOG: ERROR - Método de pago no especificado.");
            redirectAttributes.addFlashAttribute("error", "Debes seleccionar un método de pago.");
            return "redirect:/carrito";
        }

        // Si viene el número de orden, cargar la orden desde la base de datos
        Orden orden = null;
        if (numeroOrden != null && !numeroOrden.isEmpty()) {
            Optional<Orden> ordenOptional = ordenService.buscarPorNumeroOrden(numeroOrden);
            if (ordenOptional.isPresent()) {
                orden = ordenOptional.get();
                System.out.println("LOG: Orden encontrada en la BD: " + orden.getNumeroOrden());
            } else {
                System.out.println("LOG: ERROR - Orden N° " + numeroOrden + " no encontrada en la BD.");
            }
        }

        List<Carrito> carritoItems = carritoService.findByUsuario(usuarioAutenticado);

        if (carritoItems.isEmpty() && orden == null) {
            System.out.println("LOG: ERROR - Ni el carrito ni la orden tienen items.");
            redirectAttributes.addFlashAttribute("error", "Tu carrito está vacío. Agrega productos antes de pagar.");
            return "redirect:/carrito";
        }

        // Recalcular el total a pagar (para asegurar que sea el valor más reciente)
        BigDecimal totalConDescuento;
        
        if (orden != null) {
            // Usar el total de la orden
            totalConDescuento = orden.getTotal();
            System.out.println("LOG: Usando total de la orden: " + totalConDescuento);
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
            System.out.println("LOG: Usando total del carrito: " + totalConDescuento);
        }

        // Generar un número de boleta simple (puedes usar una lógica más robusta en producción)
        String numeroBoleta = "BOLETA-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.println("LOG: Número de boleta generado: " + numeroBoleta);

        // Procesar la pasarela de pago
        Pago.MetodoPago metodoPagoEnum = Pago.MetodoPago.valueOf(metodo.toLowerCase());
        Map<String, Object> resultadoPasarela = pasarelaPagoService.procesarPago(metodoPagoEnum, totalConDescuento, numeroBoleta);
        
        model.addAttribute("totalPagar", totalConDescuento);
        model.addAttribute("metodoPago", metodo);
        model.addAttribute("numeroBoleta", numeroBoleta);
        model.addAttribute("numeroOrden", numeroOrden);
        model.addAttribute("carritoCount", carritoService.countItemsInCarrito(usuarioAutenticado));
        model.addAttribute("resultadoPasarela", resultadoPasarela);

        System.out.println("LOG: Renderizando página de pago.");
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
            @RequestParam("numeroOrden") String numeroOrden,
            @RequestParam("metodoPago") String metodoPago,
            @RequestParam("totalPagar") BigDecimal totalPagar,
            @RequestParam("numeroBoleta") String numeroBoleta,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        System.out.println("LOG: Iniciando confirmación de pago.");
        System.out.println("LOG: > numeroOrden: " + numeroOrden);
        System.out.println("LOG: > metodoPago: " + metodoPago);
        System.out.println("LOG: > totalPagar: " + totalPagar);
        System.out.println("LOG: > numeroBoleta: " + numeroBoleta);

        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuarioAutenticado");

        if (usuarioAutenticado == null) {
            System.out.println("LOG: ERROR - Usuario no autenticado en confirmación. Redirigiendo a login.");
            return "redirect:/login";
        }

        // Buscar la orden por su número
        Optional<Orden> ordenOptional = ordenService.buscarPorNumeroOrden(numeroOrden);
        if (ordenOptional.isEmpty()) {
            System.out.println("LOG: ERROR - La orden N° " + numeroOrden + " no fue encontrada. No se pudo procesar el pago.");
            redirectAttributes.addFlashAttribute("error", "La orden no fue encontrada. No se pudo procesar el pago.");
            return "redirect:/carrito";
        }

        Orden orden = ordenOptional.get();
        System.out.println("LOG: Orden a procesar encontrada: " + orden.getNumeroOrden());

        // Validar que la orden no haya sido pagada ya
        if (orden.getEstado() == Orden.EstadoOrden.pagada) {
            System.out.println("LOG: ADVERTENCIA - La orden N° " + numeroOrden + " ya ha sido pagada.");
            redirectAttributes.addFlashAttribute("error", "Esta orden ya ha sido pagada.");
            return "redirect:/pago_exitoso"; // O a una página de "mis órdenes"
        }

        try {
            // Convertir el string del método de pago a su Enum correspondiente
            Pago.MetodoPago metodoPagoEnum = Pago.MetodoPago.valueOf(metodoPago.toLowerCase());

            // Procesar el pago usando los detalles de la orden
            System.out.println("LOG: Llamando a pagoService.procesarPagoConOrden...");
            pagoService.procesarPagoConOrden(usuarioAutenticado, metodoPagoEnum, orden);
            System.out.println("LOG: pagoService.procesarPagoConOrden ejecutado exitosamente.");

            // Actualizar el estado de la orden a PAGADA
            System.out.println("LOG: Actualizando estado de la orden a PAGADA.");
            ordenService.actualizarEstado(orden.getId(), Orden.EstadoOrden.pagada);

            redirectAttributes.addFlashAttribute("numeroBoleta", numeroBoleta);
            System.out.println("LOG: Redirigiendo a página de pago exitoso.");
            return "redirect:/pago_exitoso";
        } catch (RuntimeException e) {
            System.err.println("LOG: ERROR CRÍTICO al procesar el pago: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error al procesar el pago: " + e.getMessage());
            return "redirect:/carrito";
        }
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
