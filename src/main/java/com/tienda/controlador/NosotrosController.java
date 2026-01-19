// Paquete donde se ubicarán tus controladores
package com.tienda.controlador;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NosotrosController {


    /**
     * Muestra la página "Nosotros".
     * @param model El objeto Model para pasar datos a la vista.
     * @param session La sesión HTTP para obtener el usuario autenticado y el contador del carrito.
     * @return El nombre de la plantilla Thymeleaf para la página "Nosotros".
     */
    @GetMapping("/nosotros")
    public String showNosotros(Model model, HttpSession session) {
        // Obtener la cantidad de ítems en el carrito para la navbar (si el usuario está logueado)
        model.addAttribute("carritoCount", 0);
        return "nosotros"; // Esto buscará src/main/resources/templates/nosotros.html
    }
}