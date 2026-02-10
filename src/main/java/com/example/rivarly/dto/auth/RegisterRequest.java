package com.example.rivarly.dto.auth;

import lombok.Data;

@Data
public class RegisterRequest {
    private String personName;
    private String personSurname;
    private String nickname;
    private String email;
    private String password;
}
