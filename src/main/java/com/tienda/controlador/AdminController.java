package com.tienda.controlador;

import com.tienda.modelo.Usuario;
import com.tienda.modelo.Pago;
import com.tienda.servicio.PagoService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Controller
public class AdminController {

    @Autowired
    private PagoService pagoService;

    @GetMapping("/admin/bienvenida")
    public String bienvenidaAdmin(Model model, HttpSession session) {
        // Obtener el nombre del administrador desde la sesión
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuarioAutenticado");
        if (usuarioAutenticado != null) {
            model.addAttribute("nombreAdmin", usuarioAutenticado.getNombre());
        } else {
            model.addAttribute("nombreAdmin", "Administrador");
        }

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

        // Gráfico de Productos Más Vendidos (top 5)
        List<Object[]> mostSoldProductsRaw = pagoService.getMostSoldProducts(5);
        // Convertir a formato esperado por el JavaScript
        List<java.util.Map<String, Object>> mostSoldProducts = mostSoldProductsRaw.stream()
                .map(row -> {
                    java.util.Map<String, Object> product = new java.util.HashMap<>();
                    if (row.length >= 2) {
                        com.tienda.modelo.Producto producto = (com.tienda.modelo.Producto) row[0];
                        Long cantidad = (Long) row[1];
                        product.put("nombreProducto", producto.getNombre());
                        product.put("cantidadVendida", cantidad);
                    }
                    return product;
                })
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("mostSoldProducts", mostSoldProducts);

        // Últimas ventas (últimas 10 ventas)
        List<Pago> ultimasVentas = pagoService.findAllPagos().stream()
                .sorted((p1, p2) -> p2.getFechaVenta().compareTo(p1.getFechaVenta()))
                .limit(10)
                .collect(java.util.stream.Collectors.toList());
        model.addAttribute("ultimasVentas", ultimasVentas);

        return "admin/bienvenida";
    }
}