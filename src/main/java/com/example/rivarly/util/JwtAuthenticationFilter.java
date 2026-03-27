package com.example.rivarly.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/**
 * JwtAuthenticationFilter is a custom Spring security filter responsible for handling JSON Web Token (JWT) based
 * authentication within the application. This filter extracts the JWT token from the incoming HTTP request cookies,
 * validates it, and sets the authenticated user in the Spring Security context.
 *
 * This class is designed to work as a pre-processing filter in the Spring Security filter chain.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    /**
     * Processes an incoming HTTP request to perform JWT (JSON Web Token) based authentication.
     * Extracts the JWT from the cookies, validates the token, and sets the authentication
     * details in the Spring Security context if the token is valid.
     *
     * @param request the HttpServletRequest object representing the client's request
     * @param response the HttpServletResponse object for sending responses to the client
     * @param chain the FilterChain to pass the request and response to the next filter in the chain
     * @throws ServletException if an error occurs during the filter processing
     * @throws IOException if an I/O error related to the input or output streams occurs
     */
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain)
            throws ServletException, IOException {


        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }
        String jwt = null;
        if (request.getCookies() != null) {
            jwt = Arrays.stream(request.getCookies())
                    .filter(cookie -> "accessToken".equals(cookie.getName()))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        if (jwt != null) {
            try {
                String username = jwtUtil.extractUsername(jwt);

                if (username != null) {

                    if (jwtUtil.isTokenExpired(jwt)) {
                        chain.doFilter(request, response);
                        return;
                    }

                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                    if (jwtUtil.isTokenValid(jwt, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            } catch (Exception e) {

                logger.error("Cannot set user authentication: {}", e);
            }
        }
        chain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        return path.startsWith("/assets/") || path.equals("/favicon.ico");
    }
}
