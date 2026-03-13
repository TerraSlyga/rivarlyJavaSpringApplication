package com.example.rivarly.controller;

import com.example.rivarly.dto.auth.AuthResponse;
import com.example.rivarly.dto.auth.LoginRequest;
import com.example.rivarly.dto.auth.RegisterRequest;
import com.example.rivarly.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.RequestBody;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * RestController for handling authentication-related requests, such as registration,
 * login, token refreshing, logout, and retrieving the current authenticated user.
 */
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints for authentication-related operations")
public class AuthController {

    public static final int HTTP_UNAUTHORIZED = 401;
    public static final int TOKEN_EXPIRY_MS = 1000;
    private final AuthService authService;

    @Value("${jwt.expirationMs}")
    private long accessCookieLiveTime;

    @Value("${jwt.refreshExpirationMs}")
    private long refreshCookieLiveTime;

    /**
     * Handles user registration requests.
     *
     * @param registerRequest the user's registration details
     * @return a ResponseEntity containing the AuthResponse with tokens and user data
     */
    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Registers a new user and returns authentication tokens.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The registration request contains user details such as email, nickname, and password.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject(
                                    name = "RegisterRequest Example",
                                    summary = "Example of a valid registration request",
                                    value = "{ \"personName\": \"John\", \"personSurname\": \"Doe\", \"nickname\": \"jdoe\", \"email\": \"jdoe@example.com\", \"password\": \"password123\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User registered successfully."),
                    @ApiResponse(responseCode = "400", description = "Invalid registration data.")
            }
    )
    public ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest registerRequest) {
        AuthResponse authResponse = authService.register(registerRequest);
        return setCookiesAndReturn(authResponse);
    }

    /**
     * Handles user login requests.
     *
     * @param loginRequest the user's login credentials (email/nickname and password)
     * @return a ResponseEntity containing the AuthResponse with tokens and user data
     */
    @PostMapping("/login")
    @Operation(
            summary = "Login a user",
            description = "Authenticates a user and returns authentication tokens.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "The login request contains user credentials like nickname and password.",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    name = "LoginRequest Example",
                                    summary = "Example of a valid login request",
                                    value = "{ \"nickname\": \"jdoe\", \"password\": \"password123\" }"
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "User authenticated and tokens returned."),
                    @ApiResponse(responseCode = "401", description = "Invalid user credentials.")
            }
    )
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest);
        return setCookiesAndReturn(authResponse);
    }

    /**
     * Refreshes the access token using the provided refresh token.
     *
     * @param refreshToken the token stored in a cookie to refresh authentication
     * @return a ResponseEntity indicating success or failure of token refresh
     */
    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token",
            description = "Refreshes the access token using a refresh token.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Access token refreshed successfully."),
                    @ApiResponse(responseCode = "401", description = "Invalid or missing refresh token.")
            }
    )
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

    /**
     * Logs the user out by clearing authentication cookies.
     *
     * @return a ResponseEntity indicating successful logout
     */
    @PostMapping("/logout")
    @Operation(
            summary = "Logout a user",
            description = "Logs out the user by clearing authentication cookies.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User successfully logged out.")
            }
    )
    public ResponseEntity<?> logout() {
        ResponseCookie accessCookie = buildCookie("accessToken", "", 0);
        ResponseCookie refreshCookie = buildCookie("refreshToken", "", 0);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body("Logged out successfully");
    }

    /**
     * Retrieves information about the currently authenticated user.
     *
     * @param authentication the Spring Security Authentication object
     * @return a ResponseEntity containing the current user's data
     */
    @GetMapping("/me")
    @Operation(
            summary = "Get authenticated user info",
            description = "Retrieves the information of the currently authenticated user.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Authenticated user data retrieved."),
                    @ApiResponse(responseCode = "401", description = "Unauthorized access.")
            }
    )
    public ResponseEntity<AuthResponse> getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HTTP_UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(authService.getCurrentPerson(authentication.getName()));
    }

    /**
     * Sets authentication cookies (accessToken and refreshToken) and returns a ResponseEntity with them.
     *
     * @param authResponse the AuthResponse containing tokens and user data
     * @return a ResponseEntity with headers containing the cookies
     */
    private ResponseEntity<AuthResponse> setCookiesAndReturn(AuthResponse authResponse) {

        ResponseCookie accessCookie = buildCookie("accessToken", authResponse.getAccessToken(), accessCookieLiveTime);

        ResponseCookie refreshCookie = buildCookie("refreshToken", authResponse.getRefreshToken(), refreshCookieLiveTime);

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(authResponse);
    }

    /**
     * Creates a ResponseCookie with the specified name, value, and expiration time.
     *
     * @param name     the cookie's name
     * @param value    the cookie's value
     * @param maxAgeMs the cookie's maximum age in milliseconds
     * @return the built ResponseCookie
     */
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