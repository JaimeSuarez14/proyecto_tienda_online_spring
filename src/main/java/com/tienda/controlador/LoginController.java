// Paquete donde se ubicarán tus controladores
package com.tienda.controlador;

import com.tienda.modelo.Usuario;
import com.tienda.servicio.UsuarioService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Muestra la página de login.
     * 
     * @param model El objeto Model para pasar datos a la vista.
     * @param error Si hay un parámetro 'error' en la URL (indicando fallo de
     *              login).
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
     * Procesa el login del usuario (autenticación manual).
     * 
     * @param correo     El correo electrónico ingresado.
     * @param contrasena La contraseña ingresada.
     * @param session    La sesión HTTP para almacenar el usuario autenticado.
     * @param model      El objeto Model para pasar mensajes de error.
     * @return Redirección a la página principal si es exitoso, o vuelve al login
     *         con error.
     */
    @PostMapping("/login")
    public String processLogin(
            @RequestParam("correo") String correo,
            @RequestParam("contrasena") String contrasena,
            HttpSession session,
            Model model) {

        // Autenticar al usuario usando el servicio
        Optional<Usuario> usuarioOptional = usuarioService.authenticateUser(correo, contrasena);

        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            // Guardar el usuario en la sesión
            session.setAttribute("usuarioLogueado", usuario);
            // Redirigir a la página principal
            return "redirect:/";
        } else {
            // Credenciales incorrectas
            model.addAttribute("error", "Correo o contraseña incorrectos.");
            return "login";
        }
    }

    /**
     * Cierra la sesión del usuario manualmente.
     * 
     * @param session La sesión HTTP a invalidar.
     * @return Una redirección a la página de inicio.
     */
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // Invalidar la sesión manualmente
        session.invalidate();
        return "redirect:/";
    }
}
