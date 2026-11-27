package com.interest_bearing_account.server.domain.auth;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain security(HttpSecurity http, AuthService auth, @Value("${app.cors-origin}") String origin) throws Exception {
        var cors = new CorsConfiguration();
        cors.setAllowedOrigins(List.of(origin));
        cors.setAllowedMethods(List.of("GET", "POST", "PUT", "OPTIONS"));
        cors.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cors);
        return http.csrf(c -> c.disable())
                .cors(c -> c.configurationSource(source))
                .sessionManagement(c -> c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .requestCache(c -> c.disable())
                .authorizeHttpRequests(
                        c ->
                                c.requestMatchers(HttpMethod.POST, "/contas", "/auth/login").permitAll()
                                        .requestMatchers("/taxa").permitAll()
                                        .requestMatchers("/contas/**").authenticated()
                                        .anyRequest()
                                        .denyAll())
                .exceptionHandling(
                        c ->
                                c.authenticationEntryPoint(
                                                (req, res, e) ->
                                                        error(
                                                                res,
                                                                401,
                                                                "NAO_AUTENTICADO",
                                                                "Sua sessão expirou ou não é válida. Entre novamente."))
                                        .accessDeniedHandler(
                                                (req, res, e) ->
                                                        error(
                                                                res,
                                                                403,
                                                                "ACESSO_NEGADO",
                                                                "Você não tem acesso a este recurso.")))
                .addFilterBefore(new BearerFilter(auth), AnonymousAuthenticationFilter.class)
                .build();
    }

    static void error(HttpServletResponse res, int status, String code, String message)
            throws IOException {
        res.setStatus(status);
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("{\"codigo\":\"" + code + "\",\"mensagem\":\"" + message + "\"}");
    }

    static final class BearerFilter extends OncePerRequestFilter {
        private final AuthService auth;

        BearerFilter(AuthService auth) {
            this.auth = auth;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
            String header = req.getHeader("Authorization");
            if (header != null) {
                String id = header.startsWith("Bearer ") ? auth.authenticate(header.substring(7)) : null;
                if (id == null) {
                    error(
                            res,
                            401,
                            "NAO_AUTENTICADO",
                            "Sua sessão expirou ou não é válida. Entre novamente.");
                    return;
                }
                SecurityContextHolder
                        .getContext()
                        .setAuthentication(new UsernamePasswordAuthenticationToken(id, null, List.of()));
            }
            chain.doFilter(req, res);
        }
    }
}
