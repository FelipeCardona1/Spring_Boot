package com.example.demo.Configuracion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuración separada para BCryptPasswordEncoder.
 *
 * ¿Por qué una clase separada?
 * Para romper la dependencia circular:
 * SecurityConfig → UsuarioService → BCryptPasswordEncoder → SecurityConfig
 *
 * Al moverlo aquí, BCryptPasswordEncoder ya no depende de SecurityConfig,
 * así Spring puede crearlo primero sin ningún ciclo.
 *
 * SecurityConfig y UsuarioService simplemente inyectan este bean
 * con @Autowired sin saber de dónde viene.
 */
@Configuration
public class PasswordEncoderConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
