package com.tienda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()  // Permitir todas las rutas sin autenticación
            )
            .csrf(csrf -> csrf.disable())  // Desactivar CSRF para simplificar
            .formLogin(form -> form.disable())  // Desactivar el login de Spring Security
            .httpBasic(basic -> basic.disable());  // Desactivar HTTP Basic
        
        return http.build();
    }
}
