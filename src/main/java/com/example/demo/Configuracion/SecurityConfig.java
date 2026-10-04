package com.example.demo.Configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Clase de configuración central de Spring Security.
 *
 * @Configuration: indica que esta clase produce beans de configuración.
 * @EnableWebSecurity: activa el módulo de seguridad web de Spring Security.
 *
 *                     Sin esta clase, Spring Security bloquearía TODAS las
 *                     rutas por defecto
 *                     y mostraría su pantalla de login genérica.
 */
@Configuration // funciona con relacion al bean
@EnableWebSecurity // Activa y habilita el soporte de seguridad werb en la app
public class SecurityConfig {
        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http)
                        throws Exception {

                http
                                .authorizeHttpRequests(auth -> auth
                                                // Login, registro, CSS y consola H2 son publicos
                                                .requestMatchers("/login", "/registro", "/css/**").permitAll()
                                                .requestMatchers("/h2-console/**").permitAll()
                                                // El panel y las operaciones de cliente son solo para ADMIN
                                                .requestMatchers("/admin", "/admin/**").hasRole("ADMIN")
                                                // Permite al cliente acceder a su propio perfil.
                                                .requestMatchers("/cliente/perfil", "/cliente/perfil/**")
                                                .hasRole("CLIENTE")
                                                // Las demás rutas de clientes siguen siendo solo para ADMIN.
                                                .requestMatchers("/Cliente/**", "/cliente/**").hasRole("ADMIN")
                                                // Las vistas y operaciones Administrativas de producto son solo para
                                                // ADMIN
                                                .requestMatchers("/producto/listar", "/Producto/listar",
                                                                "/producto/stock", "/Producto/stock",
                                                                "/producto/formulario", "/producto/formulario/**",
                                                                "/Producto/formulario", "/Producto/formulario/**",
                                                                "/producto/eliminar/**", "/Producto/eliminar/**")
                                                .hasRole("ADMIN") // Solo los administradores abren paginas con ruta
                                                                  // admin
                                                // Solo las cuentas con rol CLIENTE pueden abrir el catálogo.

                                                // El catalogo de solo lectura es para CLIENTE
                                                .requestMatchers("/catalogo").hasRole("CLIENTE")
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .loginProcessingUrl("/login")
                                                .usernameParameter("email")
                                                .passwordParameter("password")
                                                /*
                                                 * Request peticion http del usuario y response la respuesta http que se
                                                 * envia al navegador,
                                                 * authentication es el objeto que contiene la informacion del usuario
                                                 * autenticado...
                                                 * authentication.getAuthorities().stream()
                                                 * Obtiene la lista de roles/permisos del usuario. Stream permite
                                                 * recorrer como un flujo de datos
                                                 * anymatch Verifica si algún rol del usuario coincide con "ROLE_ADMIN"
                                                 */
                                                .successHandler((request, response, authentication) -> {

                                                        boolean esAdmin = authentication.getAuthorities().stream()
                                                                        .anyMatch(authority -> authority.getAuthority()
                                                                                        .equals("ROLE_ADMIN"));

                                                        String destino = esAdmin ? "/producto/listar" : "/catalogo";
                                                        response.sendRedirect(request.getContextPath() + destino);
                                                        // Lo que debe pasar cuando el admin se autentica correctamente
                                                })
                                                .permitAll())
                                .csrf(csrf -> csrf
                                                .ignoringRequestMatchers("/h2-console/**"))
                                .headers(headers -> headers
                                                .frameOptions(frame -> frame.sameOrigin()));

                return http.build();
        }
}
