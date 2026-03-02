// Paquete donde se ubicarán tus controladores
package com.tienda.controlador;

import com.tienda.modelo.Usuario;
import com.tienda.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Eliminado: no se usa

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/admin") // Todas las rutas en este controlador comenzarán con /admin
public class AdminUsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Muestra la página de gestión de usuarios para el administrador.
     * @param model El objeto Model para pasar datos a la vista.
     * @return El nombre de la plantilla Thymeleaf para la página de gestión de usuarios.
     */
    @GetMapping("/usuarios")
    public String gestionarUsuarios(Model model) {
        // Spring Security ya asegura que solo los ADMINs accedan a esta ruta.
        List<Usuario> usuarios = usuarioService.findAllUsuarios();
        model.addAttribute("usuarios", usuarios);
        return "admin/admin_usuarios"; // Esto buscará src/main/resources/templates/admin_usuarios.html
    }

    /**
     * Guarda un usuario (agrega uno nuevo o edita uno existente).
     * Este endpoint es llamado vía AJAX desde el formulario modal.
     * @param usuario El objeto Usuario enviado desde el formulario.
     * @return Una respuesta JSON indicando éxito o fracaso.
     */
    @PostMapping("/usuarios/guardar")
    @ResponseBody // Indica que el método devuelve directamente el cuerpo de la respuesta (JSON)
    public ResponseEntity<Map<String, Object>> guardarUsuario(@RequestBody Usuario usuario) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Convertir el tipo de usuario a su Enum correspondiente
            if (usuario.getTipo() != null) {
                // Asegurarse de que el valor del enum es válido
                usuario.setTipo(Usuario.TipoUsuario.valueOf(usuario.getTipo().name().toLowerCase()));
            }

            usuarioService.saveUsuario(usuario);
            response.put("success", true);
            response.put("message", "Usuario guardado exitosamente.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException e) { // Capturar excepciones de validación de DNI/Correo
            response.put("success", false);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Error al guardar usuario: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error interno al guardar el usuario.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Elimina un usuario por su ID.
     * Este endpoint es llamado vía AJAX.
     * @param id El ID del usuario a eliminar.
     * @return Una respuesta JSON indicando éxito o fracaso.
     */
    @DeleteMapping("/usuarios/eliminar/{id}")
    @ResponseBody // Indica que el método devuelve directamente el cuerpo de la respuesta (JSON)
    public ResponseEntity<Map<String, Object>> eliminarUsuario(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Optional<Usuario> usuarioOptional = usuarioService.findUsuarioById(id);
            if (usuarioOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "Usuario no encontrado.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            usuarioService.deleteUsuario(id);
            response.put("success", true);
            response.put("message", "Usuario eliminado exitosamente.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error al eliminar usuario: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error interno al eliminar el usuario.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
