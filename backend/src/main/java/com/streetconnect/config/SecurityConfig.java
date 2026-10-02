package com.streetconnect.config;

import com.streetconnect.security.JwtAuthenticationEntryPoint;
import com.streetconnect.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint unauthorizedHandler;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Frontend static assets
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/pages/**",
                                "/css/**",
                                "/js/**",
                                "/assets/**",
                                "/uploads/**",
                                "/favicon.ico"
                        ).permitAll()
                        // Public Auth APIs
                        .requestMatchers("/api/auth/**").permitAll()
                        // Public read endpoints for transparency & registration
                        .requestMatchers(HttpMethod.GET, "/api/streets/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/complaints/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/announcements/**").permitAll()
                        // Admin endpoints
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // Moderator & Admin endpoints
                        .requestMatchers(HttpMethod.PUT, "/api/complaints/*/status").hasAnyRole("MODERATOR", "ADMIN")
                        .requestMatchers("/api/moderator/**").hasAnyRole("MODERATOR", "ADMIN")
                        // All other APIs require authenticated user
                        .requestMatchers("/api/**").authenticated()
                        .anyRequest().permitAll()
                );

        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
