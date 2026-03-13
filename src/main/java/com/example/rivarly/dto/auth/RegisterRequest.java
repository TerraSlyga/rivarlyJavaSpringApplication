package com.example.rivarly.dto.auth;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO for representing registration request data.
 * Contains essential fields required for user account registration.
 */
@Getter
@Setter
public class RegisterRequest {
    /**
     * The first name of the person registering.
     */
    private String personName;

    /**
     * The surname of the person registering.
     */
    private String personSurname;

    /**
     * The nickname of the person registering.
     */
    private String nickname;

    /**
     * The email address of the person registering.
     */
    private String email;

    /**
     * The password chosen by the person registering.
     */
    private String password;
}