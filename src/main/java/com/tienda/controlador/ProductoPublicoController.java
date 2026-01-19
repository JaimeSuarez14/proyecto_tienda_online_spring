// Paquete donde se ubicarán tus controladores
package com.tienda.controlador; // Asegúrate de cambiar 'com.tuproyecto.controlador' a tu paquete real

import com.tienda.modelo.Producto;
import com.tienda.servicio.ProductoService; // Necesitaremos crear este servicio
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ProductoPublicoController {

    @Autowired
    private ProductoService productoService; // Inyectamos el servicio de productos

    /**
     * Maneja las solicitudes GET para la página de productos públicos.
     * Permite filtrar productos por categoría, género, talla y una cadena de búsqueda.
     *
     * @param categoria El filtro de categoría (opcional).
     * @param search La cadena de búsqueda para nombre o descripción (opcional).
     * @param model El objeto Model para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para la página de productos públicos.
     */
    @GetMapping("/productos-publicos")
    public String verProductosPublicos(
            @RequestParam(value = "categoria", required = false) String categoria,
            @RequestParam(value = "genero", required = false) String genero,
            @RequestParam(value = "talla", required = false) String talla,
            @RequestParam(value = "search", required = false) String search,
            Model model) {

        List<Producto> productos = productoService.findAllProductos();

        if (categoria != null && !categoria.isEmpty()) {
            productos = productos.stream()
                    .filter(p -> p.getCategoria() != null && p.getCategoria().getDescripcion().equalsIgnoreCase(categoria))
                    .collect(Collectors.toList());
        }

        if (genero != null && !genero.isEmpty()) {
            productos = productos.stream()
                    .filter(p -> p.getGenero() != null && p.getGenero().equalsIgnoreCase(genero))
                    .collect(Collectors.toList());
        }

        if (talla != null && !talla.isEmpty()) {
            productos = productos.stream()
                    .filter(p -> p.getTalla() != null && p.getTalla().equalsIgnoreCase(talla))
                    .collect(Collectors.toList());
        }

        if (search != null && !search.isEmpty()) {
            String lowerCaseSearch = search.toLowerCase();
            productos = productos.stream()
                    .filter(p -> (p.getNombre() != null && p.getNombre().toLowerCase().contains(lowerCaseSearch)) ||
                                 (p.getDescripcion() != null && p.getDescripcion().toLowerCase().contains(lowerCaseSearch)))
                    .collect(Collectors.toList());
        }

        Set<String> categoriasUnicas = productoService.findAllProductos().stream()
                .map(p -> p.getCategoria().getDescripcion())
                .filter(c -> c != null && !c.isEmpty())
                .collect(Collectors.toSet());

        Set<String> generosUnicos = productoService.findAllProductos().stream()
                .map(Producto::getGenero)
                .filter(g -> g != null && !g.isEmpty())
                .collect(Collectors.toSet());

        Set<String> tallasUnicas = productoService.findAllProductos().stream()
                .map(Producto::getTalla)
                .filter(t -> t != null && !t.isEmpty())
                .collect(Collectors.toSet());

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriasUnicas);
        model.addAttribute("generos", generosUnicos);
        model.addAttribute("tallas", tallasUnicas);
        model.addAttribute("selectedCategoria", categoria);
        model.addAttribute("selectedGenero", genero);
        model.addAttribute("selectedTalla", talla);
        model.addAttribute("searchTerm", search);

        return "productos_publicos"; // Esto buscará src/main/resources/templates/productos_publicos.html
    }
}
