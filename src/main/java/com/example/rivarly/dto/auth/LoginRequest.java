package com.example.rivarly.dto.auth;

import lombok.Data;

/**
 * DTO for representing login request data.
 * Contains essential fields required for user authentication.
 */
@Data
public class LoginRequest {
    /**
     * The email of the user attempting to log in.
     */
    private String email;

    /**
     * The nickname of the user attempting to log in.
     */
    private String nickname;

    /**
     * The password of the user attempting to log in.
     */
    private String password;
}