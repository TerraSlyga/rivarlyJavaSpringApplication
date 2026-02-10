package com.example.rivarly.config;

import com.example.rivarly.util.JwtAuthenticationFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.jspecify.annotations.Nullable;
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

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter){
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Підключаємо налаштування CORS
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives(
                                        "default-src 'self'; " +          // За замовчуванням дозволяємо ресурси лише з нашого домену
                                                "script-src 'self' 'unsafe-inline'; " + // Дозволяємо скрипти з нашого домену (unsafe-inline часто потрібен для Vite/React в розробці)
                                                "style-src 'self' 'unsafe-inline'; " +  // Дозволяємо стилі
                                                "img-src 'self' data:; " +        // Дозволяємо зображення з нашого домену та base64-дані
                                                "frame-ancestors 'self';"         // Захист від Clickjacking
                                )
                        )
                        .frameOptions(frame -> frame.sameOrigin()) // Для старих браузерів
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login", "/api/auth/register").permitAll()

                        // Всі інші запити на /auth/ (включаючи /me) вимагатимуть авторизації
                        // (або можна явно прописати .requestMatchers("/api/auth/me").authenticated())
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/api/events/**").permitAll() // Якщо це ще актуально
                        .anyRequest().authenticated()
                )
                // Додаємо наш фільтр перед стандартним
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Value("${cors.allowedOrigin}")
    private String allowedOrigin;

    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        //
        corsConfiguration.setAllowedOrigins(List.of(allowedOrigin));
        //Methods
        corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        //Headers
        corsConfiguration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        //Cookies
        corsConfiguration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }

    //Encoding for passwords
    @Bean
    public PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return  authenticationConfiguration.getAuthenticationManager();
    }
}
