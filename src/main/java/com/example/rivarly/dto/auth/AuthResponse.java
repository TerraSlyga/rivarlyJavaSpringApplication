package com.example.rivarly.dto.auth;

import com.example.rivarly.entity.Privilege;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private Long personId;
    private String nickname;
    private Set<Privilege> privileges;
}
