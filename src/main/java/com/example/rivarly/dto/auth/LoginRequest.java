package com.example.rivarly.dto.auth;

import lombok.Data;

@Data
public class LoginRequest {
    private String email;
    private String nickname;
    private String password;
}
