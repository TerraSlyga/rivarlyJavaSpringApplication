package com.example.rivarly.config;

import com.example.rivarly.util.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Configuration class for setting up application security.
 * This class contains Spring Security configurations such as CORS settings,
 * session management, filter chain setup, and password encoding.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configures the security filter chain.
     * Includes configurations such as CSRF disabling, CORS setup, headers setup,
     * session management, authentication authorization rules, and adding custom filters.
     *
     * @param http the HttpSecurity object for configuring security features
     * @return the configured SecurityFilterChain object used by Spring Security
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Configure CORS settings
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                        "default-src 'self'; " +          // Default source domain restriction
                                                "script-src 'self' 'unsafe-inline'; " + // Allow scripts from the same domain
                                                "style-src 'self' 'unsafe-inline'; " +  // Allow styles from the same domain
                                                "img-src 'self' data:; " +        // Allow images from the same domain and base64 data
                                                "frame-ancestors 'self';"         // Protection against Clickjacking
                                )
                        )
                        .frameOptions(frame -> frame.sameOrigin()) // Enable frame embedding for the same origin
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", // Endpoint for user login
                                "/api/auth/register", // Endpoint for user registration
                                "/v3/api-docs",          // Endpoint for Swagger API docs JSON
                                "/v3/api-docs/**",       // Endpoint for specific Swagger API docs
                                "/v3/api-docs.yaml",     // Endpoint for Swagger API docs in YAML format
                                "/api-docs/**",          // Endpoint for additional API documentation (logs indicate this is required)
                                "/swagger-ui/**",        // Endpoint for Swagger UI resources
                                "/swagger-ui.html"       // Main entry point for Swagger UI
                        ).permitAll()

                        // All other auth-related requests (e.g., /auth/me) may require authentication if defined later
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/events/**").permitAll() // Public access to event-related endpoints
                        .anyRequest().authenticated() // All other requests require authentication
                )
                // Add custom JWT authentication filter before the default UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Value("${cors.allowedOrigin}")
    private String allowedOrigin;

    /**
     * Configures the CORS (Cross-Origin Resource Sharing) settings for the application.
     * Specifies the allowed origins, HTTP methods, headers, and credentials.
     *
     * @return the CorsConfigurationSource object with configured settings
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // Allowed origins
        corsConfiguration.setAllowedOrigins(List.of(allowedOrigin));
        // Allowed HTTP methods
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        // Allowed headers
        corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        // Enable credentials (e.g., cookies)
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    /**
     * Creates a bean for password encoding using BCrypt.
     *
     * @return the PasswordEncoder instance configured with BCrypt encoding
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provides the application-wide AuthenticationManager bean.
     *
     * @param authenticationConfiguration the AuthenticationConfiguration object for configuration injection
     * @return the AuthenticationManager object
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}