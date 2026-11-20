package com.queueflow.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            .authorizeHttpRequests(auth -> auth

                // Public login and registration
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/auth/login",
                    "/api/clinics/register",
                    "/api/doctors/register",
                    "/api/patients/register"
                ).permitAll()

                // Public doctor browsing
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/doctors",
                    "/api/doctors/specialties",
                    "/api/doctors/*/schedule",
                    "/api/doctors/*/slots"
                ).permitAll()

                // Only ADMIN can update doctor specialty
                .requestMatchers(
                    HttpMethod.PUT,
                    "/api/doctors/*/specialty"
                ).hasRole("ADMIN")

                // Patient check-in
                .requestMatchers(
                    HttpMethod.POST,
                    "/api/queue/patient/check-in"
                ).hasRole("PATIENT")

                // Patient APIs
                .requestMatchers("/api/patients/**")
                    .hasRole("PATIENT")

                // Doctor APIs
                .requestMatchers("/api/doctors/**")
                    .hasRole("DOCTOR")

                // Doctor queue management
                .requestMatchers("/api/queue/**")
                    .hasRole("DOCTOR")

                // Appointment APIs
                .requestMatchers("/api/appointments/**")
                    .hasRole("PATIENT")

                // Prescription APIs
                .requestMatchers("/api/prescriptions/**")
                    .hasAnyRole("PATIENT", "DOCTOR")

                // All other APIs require authentication
                .anyRequest().authenticated()
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}