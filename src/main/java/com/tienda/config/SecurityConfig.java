// Paquete donde se ubicarán tus configuraciones
package com.tienda.config;

import com.tienda.modelo.Usuario;
import com.tienda.servicio.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
// import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.util.Collections;
import java.util.Optional;

@Configuration
@EnableWebSecurity // Habilita la configuración de seguridad web de Spring Security
public class SecurityConfig {

    @Autowired
    private UsuarioService usuarioService; // Inyectamos nuestro servicio de usuario

    /**
     * Define un Bean para BCryptPasswordEncoder.
     * Este codificador se utilizará para hashear y verificar contraseñas.
     * @return Una instancia de BCryptPasswordEncoder.
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura el proveedor de autenticación DAO.
     * CRÍTICO: Vincula UserDetailsService + PasswordEncoder
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService uds) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(uds); // usar ctor con UDS
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     * Define las reglas de autorización para diferentes rutas.
     * @param http El objeto HttpSecurity para configurar la seguridad.
     * @return La cadena de filtros de seguridad configurada.
     * @throws Exception Si ocurre un error durante la configuración.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider(userDetailsService()))
                .authorizeHttpRequests(authorize -> authorize
                        // Rutas públicas accesibles por cualquiera
                        .requestMatchers("/", "/login", "/registro", "/productos-publicos", "/images/**", "/css/**", "/js/**").permitAll()
                        // Rutas que requieren el rol de ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Todas las demás rutas requieren autenticación
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // Especifica la página de login personalizada
                        .loginProcessingUrl("/login") // URL que procesa el POST del formulario
                        .usernameParameter("correo") // Nombre del campo de correo en el formulario
                        .passwordParameter("contrasena") // Nombre del campo de contraseña en el formulario
                        .successHandler(authenticationSuccessHandler()) // Manejador de éxito de login
                        .failureUrl("/login?error=true") // URL a la que redirigir en caso de fallo
                        .permitAll() // Permitir acceso a la página de login para todos
                )
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL para cerrar sesión
                        .logoutSuccessUrl("/login?logout") // Cambiar para mostrar modal
                        .invalidateHttpSession(true) // Invalidar la sesión HTTP
                        .deleteCookies("JSESSIONID") // Eliminar cookies de sesión
                        .permitAll() // Permitir acceso a la URL de logout para todos
                )
                .csrf(csrf -> csrf.disable()); // Deshabilitar CSRF para simplificar en desarrollo (NO RECOMENDADO EN PRODUCCIÓN)

        return http.build();
    }

    /**
     * Define un UserDetailsService personalizado para cargar los detalles del usuario
     * desde nuestra base de datos.
     * @return Una instancia de UserDetailsService.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        return correo -> {
            Optional<Usuario> usuarioOptional = usuarioService.findByCorreo(correo); // Asume que tienes un findByCorreo en UsuarioService
            if (usuarioOptional.isEmpty()) {
                throw new UsernameNotFoundException("Usuario no encontrado con correo: " + correo);
            }
            Usuario usuario = usuarioOptional.get();
            // Mapear el tipo de usuario a un rol de Spring Security
            String role = "ROLE_" + usuario.getTipo().name().toUpperCase(); // Ej: ROLE_USUARIO, ROLE_ADMIN
            return new User(usuario.getCorreo(), usuario.getContrasena(), Collections.singletonList(new SimpleGrantedAuthority(role)));
        };
    }

    /**
     * Manejador de éxito de autenticación para redirigir según el rol del usuario.
     * @return Una instancia de AuthenticationSuccessHandler.
     */
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String correo = userDetails.getUsername();

            Optional<Usuario> usuarioOptional = usuarioService.findByCorreo(correo);
            if (usuarioOptional.isPresent()) {
                Usuario usuario = usuarioOptional.get();
                // usa la misma clave que leen tus controladores
                request.getSession().setAttribute("usuarioLogueado", usuario);
                // opcional: mantener también el anterior
                request.getSession().setAttribute("usuarioAutenticado", usuario);

                if (usuario.getTipo() == Usuario.TipoUsuario.admin) {
                    response.sendRedirect("/admin/bienvenida");
                } else {
                    response.sendRedirect("/bienvenida");
                }
            } else {
                response.sendRedirect("/login?error=true");
            }
        };
    }
}