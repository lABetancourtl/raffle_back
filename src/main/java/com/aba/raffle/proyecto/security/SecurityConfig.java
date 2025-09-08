package com.aba.raffle.proyecto.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtils jwtUtil;
    private final JWTFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ).permitAll()

                        .requestMatchers("/api/login/**").permitAll()   //Login publico
                        .requestMatchers("/api/purchase/**").permitAll()  //comprar rifa publico
                        .requestMatchers("/api/raffle/numerosPorEmail").permitAll()  //buscar numero daddo un email y rtorna los datos de la compra
                        .requestMatchers("/api/raffle/numerosPorEmail/soloNumeros").permitAll()  //buscar numero daddo un email solo retorna los numeros (publico)
                        .requestMatchers("/api/admin/crearUsuario").permitAll() //crear usuario publico solo por pruebas
                        .requestMatchers("/api/admin/crearUsuarioHome").permitAll() //crear usuario final (home)
                        .requestMatchers("/api/admin/validarEmail").permitAll() //crear usuario final (home)
                        .requestMatchers("/api/admin/usuarioEmailVerificado").permitAll() //ver todos los usuarios ene stado verificado pendiente
                         .requestMatchers("/api/userlogin").permitAll() //subir imagenes como docfront y docbac
                        .requestMatchers("/api/raffle/activa").permitAll() //busca la rifa activa
                        .requestMatchers("/api/purchase/cantidadNumerosDisponibles").permitAll() //cantidad numeros disponibles al momento de querer hacer el pago
                        .requestMatchers("/api/mercadopago/crear-preferencia").permitAll()
                        .requestMatchers("/api/mercadopago/webhook").permitAll()
                        .requestMatchers("/api/mercadopago/procesar-pago").permitAll()
                        .requestMatchers("/api/imagenes").permitAll() //subir imagenes como docfront y docback
            //            .requestMatchers("/api/admin/asignarNumero").permitAll() //solo por un momento, luego quitar los permisos

                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(new AutenticacionEntryPoint()))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*")); // Si usas Angular // Origen FRONT
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
