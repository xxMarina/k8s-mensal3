package com.marmitech.Marmitech.Config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.marmitech.Marmitech.Security.JwtAuthFilter;
import com.marmitech.Marmitech.Security.JwUtil;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import java.util.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Value("${app.cors.origins}")
    private String corsOrigins;

    private final JwUtil jwUtil;

    public SecurityConfig(JwUtil jwUtil) {
        this.jwUtil = jwUtil;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Usuário não logado ou login inválido"))
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/usuario/login").permitAll()
                        .requestMatchers("/health").permitAll()

                        // area do cliente
                        .requestMatchers("/api/pedido/meus/**").hasAnyRole("CLIENTE", "ADMIN")

                        // usuarios: exclusivo do admin
                        .requestMatchers("/api/usuario/**").hasRole("ADMIN")

                        // escrita: admin apenas
                        .requestMatchers(HttpMethod.POST,   "/api/produto/**", "/api/categoria/**", "/api/cliente/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT,    "/api/produto/**", "/api/categoria/**", "/api/cliente/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")

                        // status do pedido: funcionario pode
                        .requestMatchers(HttpMethod.PUT, "/api/pedido/**").hasAnyRole("ADMIN", "FUNCIONARIO")

                        // leitura: admin e funcionario
                        .requestMatchers(HttpMethod.GET, "/**").hasAnyRole("ADMIN", "FUNCIONARIO")

                        .anyRequest().authenticated());

        http.addFilterBefore(new JwtAuthFilter(jwUtil), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList(corsOrigins.split(",")));
        config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type", "x-requested-with"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD"));
        config.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}