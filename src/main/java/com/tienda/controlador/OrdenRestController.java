package com.tienda.controlador;

import com.tienda.modelo.Orden;
import com.tienda.modelo.Usuario;
import com.tienda.servicio.OrdenService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/orden")
public class OrdenRestController {

    @Autowired
    private OrdenService ordenService;

    /**
     * Endpoint para crear una orden a partir del carrito del usuario.
     * @param session La sesión HTTP para obtener el usuario autenticado.
     * @return Una respuesta JSON con los detalles de la orden creada.
     */
    @PostMapping("/crear")
    public ResponseEntity<Map<String, Object>> crearOrden(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        Usuario usuarioAutenticado = (Usuario) session.getAttribute("usuarioAutenticado");
        
        // Si no encuentra con ese nombre, intenta con el otro
        if (usuarioAutenticado == null) {
            usuarioAutenticado = (Usuario) session.getAttribute("usuarioLogueado");
        }

        if (usuarioAutenticado == null) {
            response.put("success", false);
            response.put("message", "Usuario no autenticado.");
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        try {
            Orden orden = ordenService.crearOrdenDesdeCarrito(usuarioAutenticado);
            
            response.put("success", true);
            response.put("message", "Orden creada exitosamente.");
            response.put("ordenId", orden.getId());
            response.put("numeroOrden", orden.getNumeroOrden());
            response.put("total", orden.getTotal());
            response.put("redirectUrl", "/orden/" + orden.getId());
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            System.err.println("Error al crear orden: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error interno al crear la orden.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint para obtener una orden por número.
     * @param numeroOrden El número de la orden.
     * @return Una respuesta JSON con los detalles de la orden.
     */
    @GetMapping("/numero/{numeroOrden}")
    public ResponseEntity<Map<String, Object>> obtenerPorNumero(@PathVariable String numeroOrden) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<Orden> ordenOptional = ordenService.buscarPorNumeroOrden(numeroOrden);
            
            if (ordenOptional.isEmpty()) {
                response.put("success", false);
                response.put("message", "Orden no encontrada.");
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            
            Orden orden = ordenOptional.get();
            response.put("success", true);
            response.put("orden", orden);
            
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error al obtener orden: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Error interno al obtener la orden.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
