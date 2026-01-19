// Paquete donde se ubicarán tus servicios
package com.tienda.servicio;

import com.tienda.modelo.Usuario;
import com.tienda.repositorio.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Usuario> findAllUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Optional<Usuario> findUsuarioById(Long id) {
        return usuarioRepository.findById(Objects.requireNonNull(id));
    }

    @Override
    public Optional<Usuario> authenticateUser(String correo, String contrasena) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findByCorreo(correo);
        if (usuarioOptional.isPresent()) {
            Usuario usuario = usuarioOptional.get();
            // Comparación directa de contraseñas (sin encriptación)
            if (contrasena.equals(usuario.getContrasena())) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }

    @Override
    public Usuario registerNewUser(Usuario usuario) {
        if (existsByDni(usuario.getDni())) {
            System.err.println("Error de registro: El DNI ya está registrado.");
            return null;
        }
        if (existsByCorreo(usuario.getCorreo())) {
            System.err.println("Error de registro: El correo electrónico ya está registrado.");
            return null;
        }

        // Guardar contraseña sin encriptar (solo para desarrollo)
        usuario.setTipo(Usuario.TipoUsuario.usuario); // Por defecto, el tipo de usuario es 'usuario'

        return usuarioRepository.save(usuario);
    }

    @Override
    public boolean existsByDni(String dni) {
        return usuarioRepository.findByDni(dni).isPresent();
    }

    @Override
    public boolean existsByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo).isPresent();
    }

    @Override
    public Optional<Usuario> findByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    /**
     * Guarda un usuario nuevo o actualiza uno existente.
     * Si se edita un usuario y la contraseña está vacía, se mantiene la contraseña
     * existente.
     * 
     * @param usuario El objeto Usuario a guardar.
     * @return El usuario guardado.
     */
    @Override
    public Usuario saveUsuario(Usuario usuario) {
        // Si el usuario tiene ID, es una edición
        if (usuario.getId() != null) {
            Optional<Usuario> existingUserOptional = usuarioRepository
                    .findById(Objects.requireNonNull(usuario.getId()));
            if (existingUserOptional.isPresent()) {
                Usuario existingUser = existingUserOptional.get();

                // Si no se proporciona una nueva contraseña, mantener la existente
                if (usuario.getContrasena() == null || usuario.getContrasena().isEmpty()) {
                    usuario.setContrasena(existingUser.getContrasena());
                }

                // Validar DNI y Correo solo si han cambiado y ya existen
                if (!existingUser.getDni().equals(usuario.getDni()) && existsByDni(usuario.getDni())) {
                    throw new RuntimeException("El DNI ya está registrado por otro usuario.");
                }
                if (!existingUser.getCorreo().equals(usuario.getCorreo()) && existsByCorreo(usuario.getCorreo())) {
                    throw new RuntimeException("El correo electrónico ya está registrado por otro usuario.");
                }
            } else {
                // Si el ID existe pero el usuario no se encuentra, es un caso inusual o error
                throw new RuntimeException("Usuario a editar no encontrado.");
            }
        } else {
            // Si es un nuevo usuario, validar DNI y Correo
            if (existsByDni(usuario.getDni())) {
                throw new RuntimeException("El DNI ya está registrado.");
            }
            if (existsByCorreo(usuario.getCorreo())) {
                throw new RuntimeException("El correo electrónico ya está registrado.");
            }
            // Guardar contraseña sin encriptar
        }

        return usuarioRepository.save(usuario);
    }

    /**
     * Elimina un usuario por su ID.
     * 
     * @param id El ID del usuario a eliminar.
     */
    @Override
    public void deleteUsuario(Long id) {
        usuarioRepository.deleteById(Objects.requireNonNull(id));
    }
}