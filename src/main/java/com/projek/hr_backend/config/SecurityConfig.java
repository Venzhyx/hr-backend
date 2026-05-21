package com.projek.hr_backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity          // aktifkan @PreAuthorize di controller
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter          jwtAuthFilter;
    private final UserDetailsService     userDetailsService;
    private final AuthEntryPoint         authEntryPoint;
    private final AccessDeniedHandlerImpl accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF — tidak diperlukan untuk REST API stateless
            .csrf(AbstractHttpConfigurer::disable)

            // Gunakan CORS config dari WebConfig (CorsFilter bean)
            .cors(cors -> {})

            .authorizeHttpRequests(auth -> auth
                // ── Public endpoints ──────────────────────────────────────
                .requestMatchers("/api/auth/**").permitAll()

                // Static files (foto upload)
                .requestMatchers("/uploads/**").permitAll()

                .requestMatchers("/api/files/**").permitAll()

                // ── ADMIN only ────────────────────────────────────────────
                .requestMatchers(HttpMethod.POST,   "/api/payroll/run").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/payroll/runs").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/payroll/settings").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT,    "/api/settings/attendance").hasRole("ADMIN")
                .requestMatchers("/api/payroll/components/**").hasRole("ADMIN")
                .requestMatchers("/api/payroll/employee-salary/**").hasRole("ADMIN")
                .requestMatchers("/api/departments/**").hasRole("ADMIN")
                .requestMatchers("/api/companies/**").hasRole("ADMIN")
                .requestMatchers("/api/users/**").hasRole("ADMIN")

                // ── Semua endpoint lain butuh login (ADMIN atau EMPLOYEE) ──
                .anyRequest().authenticated()
            )

            // Stateless — tidak pakai session
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Handler untuk 401 dan 403
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(authEntryPoint)
                .accessDeniedHandler(accessDeniedHandler))

            .authenticationProvider(authenticationProvider())

            // Pasang JWT filter sebelum UsernamePasswordAuthenticationFilter
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // Spring Security 7.x: constructor menerima UserDetailsService,
        // PasswordEncoder diset via setter
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    
}
