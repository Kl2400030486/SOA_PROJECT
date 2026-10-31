package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.GET, "/parking/zones", "/parking/zones/*", "/parking/slots", "/parking/slots/available").authenticated()
                        .requestMatchers(HttpMethod.POST, "/parking/zones", "/parking/slots").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/parking/slots/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/parking/zones/*/capacity", "/parking/slots/*/status").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/parking/occupancy").hasRole("ADMIN")
                        .anyRequest().authenticated())
                    .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, exception) -> writeError(response, 401, "Unauthorized", request.getRequestURI()))
                        .accessDeniedHandler((request, response, exception) -> writeError(response, 403, "Forbidden", request.getRequestURI())))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private void writeError(HttpServletResponse response, int status, String error, String path) throws IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write("{\"timestamp\":\"" + Instant.now() + "\",\"status\":" + status
                + ",\"error\":\"" + error + "\",\"message\":\"Request is not authorized\",\"path\":\"" + path + "\"}");
    }
}
