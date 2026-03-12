package com.example.rivarly.controller;

import com.example.rivarly.dto.auth.AuthResponse;
import com.example.rivarly.dto.auth.LoginRequest;
import com.example.rivarly.dto.auth.RegisterRequest;
import com.example.rivarly.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int TOKEN_EXPIRY_MS = 1000;
    private final AuthService authService;

    @Value("${jwt.expirationMs}")
    private long accessCookieLiveTime;

    @Value("${jwt.refreshExpirationMs}")
    private long refreshCookieLiveTime;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        AuthResponse authResponse = authService.register(registerRequest);
        return setCookiesAndReturn(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest);
        return setCookiesAndReturn(authResponse);
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.status(HTTP_UNAUTHORIZED).body("Refresh Token is missing");
        }

        try {
            String newAccessToken = authService.refreshAccessToken(refreshToken);
            ResponseCookie accessCookie = buildCookie("accessToken", newAccessToken, accessCookieLiveTime);

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                    .body("Access token refreshed successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HTTP_UNAUTHORIZED).body("Invalid Refresh Token");
        }
    }

    // --- LOGOUT ---
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie accessCookie = buildCookie("accessToken", "", 0);
        ResponseCookie refreshCookie = buildCookie("refreshToken", "", 0);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body("Logged out successfully");
    }

    // --- GET CURRENT USER ---
    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HTTP_UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(authService.getCurrentPerson(authentication.getName()));
    }

    private ResponseEntity<AuthResponse> setCookiesAndReturn(AuthResponse authResponse) {

        ResponseCookie accessCookie = buildCookie("accessToken", authResponse.getAccessToken(), accessCookieLiveTime);

        ResponseCookie refreshCookie = buildCookie("refreshToken", authResponse.getRefreshToken(), refreshCookieLiveTime);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(authResponse);
    }

    // Універсальний будівник Cookies
    private ResponseCookie buildCookie(String name, String value, long maxAgeMs) {
        return ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(maxAgeMs / TOKEN_EXPIRY_MS)
                .sameSite("Strict")
                .build();
    }
}