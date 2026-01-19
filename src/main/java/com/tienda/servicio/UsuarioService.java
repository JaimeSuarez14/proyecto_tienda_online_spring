// Paquete donde se ubicarán tus servicios
package com.tienda.servicio;

import com.tienda.modelo.Usuario;
import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<Usuario> findAllUsuarios();

    Optional<Usuario> findUsuarioById(Long id);

    Optional<Usuario> authenticateUser(String correo, String contrasena);

    Usuario registerNewUser(Usuario usuario);

    boolean existsByDni(String dni);

    boolean existsByCorreo(String correo);

    Optional<Usuario> findByCorreo(String correo);

    /**
     * Guarda un usuario nuevo o actualiza uno existente.
     * La contraseña será hasheada si se proporciona una nueva.
     * 
     * @param usuario El objeto Usuario a guardar.
     * @return El usuario guardado.
     */
    Usuario saveUsuario(Usuario usuario); // NUEVO MÉTODO

    /**
     * Elimina un usuario por su ID.
     * 
     * @param id El ID del usuario a eliminar.
     */
    void deleteUsuario(Long id); // NUEVO MÉTODO
}
