package com.tienda.controlador;

import com.tienda.modelo.Usuario;
import com.tienda.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistroController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Muestra la página de registro.
     * @param model El objeto Model para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para la página de registro.
     */
    @GetMapping("/registro")
    public String showRegistrationForm(Model model) {
        model.addAttribute("usuario", new Usuario()); // Objeto para enlazar los datos del formulario
        return "registro"; // Esto buscará src/main/resources/templates/registro.html
    }

    /**
     * Procesa el formulario de registro.
     * @param dni El DNI del usuario.
     * @param nombre El nombre del usuario.
     * @param apellido El apellido del usuario.
     * @param correo El correo electrónico del usuario.
     * @param contrasena La contraseña del usuario.
     * @param redirectAttributes Para añadir atributos a la URL de redirección (ej. mensajes flash).
     * @param model El objeto Model para añadir errores si el registro falla.
     * @return Una redirección a la página de login o de vuelta al registro con un error.
     */
    @PostMapping("/registro")
    public String registerUser(
            @RequestParam String dni,
            @RequestParam String nombre,
            @RequestParam String apellido,
            @RequestParam String correo,
            @RequestParam String contrasena,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Validaciones básicas antes de intentar guardar
        if (dni == null || dni.trim().isEmpty() || dni.length() < 8 || dni.length() > 20) { // Ejemplo de validación de longitud de DNI
            model.addAttribute("error", "El DNI debe tener entre 8 y 20 caracteres.");
            model.addAttribute("usuario", new Usuario(dni, nombre, apellido, correo, "", Usuario.TipoUsuario.usuario)); // Mantener datos
            return "registro";
        }
        if (contrasena == null || contrasena.length() < 6) { // Ejemplo de validación de longitud de contraseña
            model.addAttribute("error", "La contraseña debe tener al menos 6 caracteres.");
            model.addAttribute("usuario", new Usuario(dni, nombre, apellido, correo, "", Usuario.TipoUsuario.usuario)); // Mantener datos
            return "registro";
        }

        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setDni(dni);
        nuevoUsuario.setNombre(nombre);
        nuevoUsuario.setApellido(apellido);
        nuevoUsuario.setCorreo(correo);
        nuevoUsuario.setContrasena(contrasena); // La contraseña se hasheará en el servicio

        Usuario usuarioRegistrado = usuarioService.registerNewUser(nuevoUsuario);

        if (usuarioRegistrado != null) {
            redirectAttributes.addFlashAttribute("success", "¡Registro exitoso! Por favor, inicia sesión.");
            return "redirect:/login"; // Redirige al login después del registro exitoso
        } else {
            // Esto se activará si DNI o correo ya existen (manejado en el servicio)
            // El servicio ya imprime un error, aquí lo pasamos a la vista
            if (usuarioService.existsByDni(dni)) {
                model.addAttribute("error", "El DNI proporcionado ya está registrado.");
            } else if (usuarioService.existsByCorreo(correo)) {
                model.addAttribute("error", "El correo electrónico proporcionado ya está registrado.");
            } else {
                model.addAttribute("error", "Ocurrió un error durante el registro. Inténtalo de nuevo.");
            }
            model.addAttribute("usuario", nuevoUsuario); // Para repoblar el formulario
            return "registro"; // Vuelve a la página de registro con el error
        }
    }
}
