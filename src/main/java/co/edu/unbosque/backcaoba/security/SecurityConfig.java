package co.edu.unbosque.backcaoba.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            CorsConfigurationSource corsConfigurationSource) throws Exception {

        AuthenticationEntryPoint unauthorized = (request, response, exception) -> {
            response.setStatus(401);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    "{\"timestamp\":\"" + Instant.now()
                            + "\",\"status\":401,\"error\":\"Unauthorized\","
                            + "\"message\":\"Se requiere autenticación válida.\","
                            + "\"path\":\"" + request.getRequestURI() + "\"}");
        };

        AccessDeniedHandler forbidden = (request, response, exception) -> {
            response.setStatus(403);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    "{\"timestamp\":\"" + Instant.now()
                            + "\",\"status\":403,\"error\":\"Forbidden\","
                            + "\"message\":\"No tienes permiso para realizar esta operación con tu rol actual.\","
                            + "\"path\":\"" + request.getRequestURI() + "\"}");
        };

        return http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorized)
                        .accessDeniedHandler(forbidden))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/", "/error", "/api/auth/registro", "/api/auth/login",
                                "/api/auth/reactivar", "/api/auth/recuperacion",
                                "/api/auth/restablecer",
                                "/api/auth/restablecer/validar", "/actuator/health",
                                "/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs/**")
                        .permitAll()
                        .requestMatchers("/api/reporte/anfitrion/**").hasRole("ANFITRION")
                        .requestMatchers("/api/admin/**", "/api/reporte/**").hasRole("ADMINISTRADOR")
                        .requestMatchers(HttpMethod.GET, "/api/alojamientos/**")
                        .hasAnyRole("HUESPED", "ANFITRION", "ADMINISTRADOR")
                        .requestMatchers(HttpMethod.POST, "/api/cotizaciones/**")
                        .hasRole("HUESPED")
                        .requestMatchers(HttpMethod.POST, "/api/reservas").hasRole("HUESPED")
                        .requestMatchers(HttpMethod.GET, "/api/reservas/mias").hasRole("HUESPED")
                        .requestMatchers(HttpMethod.POST, "/api/reservas/*/cancelar").hasRole("HUESPED")
                        .requestMatchers(HttpMethod.GET, "/api/reservas/anfitrion").hasRole("ANFITRION")
                        .requestMatchers(HttpMethod.GET, "/api/reservas/*").hasAnyRole("HUESPED", "ANFITRION")
                        .anyRequest().authenticated())
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200", "http://127.0.0.1:4200"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}