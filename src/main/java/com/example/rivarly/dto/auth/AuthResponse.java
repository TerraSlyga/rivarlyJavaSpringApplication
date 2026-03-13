package com.example.rivarly.dto.auth;

import com.example.rivarly.entity.Privilege;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

/**
 * DTO for representing authentication response data.
 * Includes access and refresh tokens, user identification, nickname, and assigned privileges.
 */
@Data
@AllArgsConstructor
public class AuthResponse {
    /**
     * The access token for authentication.
     * This field is ignored during JSON serialization.
     */
    @JsonIgnore
    private String accessToken;

    /**
     * The refresh token used for obtaining a new access token.
     * This field is ignored during JSON serialization.
     */
    @JsonIgnore
    private String refreshToken;

    /**
     * Unique identifier of the person.
     */
    private Long personId;

    /**
     * Nickname of the person.
     */
    private String nickname;

    /**
     * Set of privileges assigned to the person.
     */
    private Set<Privilege> privileges;
}