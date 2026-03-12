package com.example.rivarly.dto.auth;

import com.example.rivarly.entity.Privilege;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class AuthResponse {
    @JsonIgnore
    private String accessToken;
    @JsonIgnore
    private String refreshToken;

    private Long personId;
    private String nickname;
    private Set<Privilege> privileges;
}
