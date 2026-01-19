// Paquete donde se ubicarán tus controladores
package com.tienda.controlador;

// import com.example.demo.servicio.UsuarioService; // Eliminado: no se usa directamente para autenticar
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam; // Se mantiene por si se necesita para algo más.
// import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Eliminado: no se usa

@Controller
public class LoginController {

    /**
     * Muestra la página de login.
     * @param model El objeto Model para pasar datos a la vista.
     * @param error Si hay un parámetro 'error' en la URL (indicando fallo de login).
     * @return El nombre de la plantilla Thymeleaf para la página de login.
     */
    @GetMapping("/login")
    public String showLoginForm(Model model, @RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            model.addAttribute("error", "Correo o contraseña incorrectos.");
        }
        return "login"; // Esto buscará src/main/resources/templates/login.html
    }

    /**
     * Cierra la sesión del usuario.
     * Este método es manejado por Spring Security. El controlador solo lo define.
     * @param session La sesión HTTP a invalidar.
     * @return Una redirección a la página de inicio.
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Spring Security maneja la invalidación de la sesión y la redirección.
        // Este método es principalmente para que el mapeo exista.
        return "redirect:/";
    }
}