package com.example.task_manager_backend.security.config;

import com.example.task_manager_backend.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   AuthenticationEntryPoint authenticationEntryPoint) throws Exception {
        configureStatelessSecurity(http);
        configureJwtAuthenticationFilter(http, jwtAuthenticationFilter);
        configureAuthorization(http);
        configureAuthenticationEntryPoint(http, authenticationEntryPoint);

        disableUnusedDefaults(http);

        return http.build();
    }

    private void configureStatelessSecurity(HttpSecurity http) {
        http
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
    }

    private void configureJwtAuthenticationFilter(HttpSecurity http,
                                                  JwtAuthenticationFilter jwtAuthenticationFilter) {
        http
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);
    }

    private void configureAuthorization(HttpSecurity http) {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/error").permitAll()
                        .requestMatchers(
                                HttpMethod.POST,
                                SecurityPaths.PUBLIC_PATHS.toArray(new String[0])
                        ).permitAll()
                        .anyRequest().authenticated()
                );
    }

    private void configureAuthenticationEntryPoint(HttpSecurity http, AuthenticationEntryPoint authenticationEntryPoint) {
        http
                .exceptionHandling(e ->
                        e.authenticationEntryPoint(authenticationEntryPoint));
    }

    private void disableUnusedDefaults(HttpSecurity http) {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> httpBasic.disable());
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
