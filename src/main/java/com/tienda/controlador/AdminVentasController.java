// Paquete donde se ubicarán tus controladores
package com.tienda.controlador;

import com.tienda.modelo.Usuario;
import com.tienda.modelo.Pago;
import com.tienda.servicio.PagoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin") // Todas las rutas en este controlador comenzarán con /admin
public class AdminVentasController {

    @Autowired
    private PagoService pagoService;

    /**
     * Muestra la página de gestión de ventas para el administrador.
     * Incluye una tabla de todas las ventas y datos para gráficos de ganancias.
     * @param model El objeto Model para pasar datos a la vista.
     * @param session La sesión HTTP para obtener el nombre del administrador.
     * @return El nombre de la plantilla Thymeleaf para la página de gestión de ventas.
     */
    @GetMapping("/ventas")
    public String gestionarVentas(Model model, HttpSession session) {
        // Spring Security ya asegura que solo los ADMINs accedan a esta ruta.
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuarioAutenticado");
        if (usuarioAutenticado != null) {
            model.addAttribute("nombreAdmin", usuarioAutenticado.getNombre());
        }

        // Obtener todas las ventas
        List<Pago> ventas = pagoService.findAllPagos();
        model.addAttribute("ventas", ventas);

        // --- Datos para Gráficos ---

        // Gráfico de Ventas Diarias (últimos 7 días)
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(6); // Últimos 7 días incluyendo hoy
        Map<LocalDate, BigDecimal> salesByDay = pagoService.getTotalSalesByDay(startDate, endDate);
        model.addAttribute("salesByDay", salesByDay);

        // Gráfico de Ventas Mensuales (año actual)
        int currentYear = LocalDate.now().getYear();
        Map<Integer, BigDecimal> salesByMonth = pagoService.getTotalSalesByMonth(currentYear);
        model.addAttribute("salesByMonth", salesByMonth);

        return "admin/admin_ventas"; // Esto buscará src/main/resources/templates/admin_ventas.html
    }
}
