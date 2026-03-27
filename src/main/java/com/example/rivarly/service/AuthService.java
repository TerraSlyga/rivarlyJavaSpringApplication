package com.example.rivarly.service;

import com.example.rivarly.dto.auth.AuthResponse;
import com.example.rivarly.dto.auth.LoginRequest;
import com.example.rivarly.dto.auth.RegisterRequest;
import com.example.rivarly.entity.Person;
import com.example.rivarly.entity.Privilege;
import com.example.rivarly.entity.RefreshToken;
import com.example.rivarly.exception.CompetitionException;
import com.example.rivarly.repository.PersonRepository;
import com.example.rivarly.repository.PrivilegeRepository;
import com.example.rivarly.repository.RefreshTokenRepository;
import com.example.rivarly.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final PersonRepository personRepository;
    private final PrivilegeRepository privilegeRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    public AuthResponse register(RegisterRequest registerRequest) {
        log.info("Starting registration for nickname: {}", registerRequest.getNickname());

        if (personRepository.existsByNickname(registerRequest.getNickname())) {
            log.warn("Registration failed: nickname {} already taken", registerRequest.getNickname());
            throw new CompetitionException("auth.nickname.taken", "AUTH-001", registerRequest.getNickname());
        }

        try {
            Person person = new Person();
            person.setPersonName(registerRequest.getPersonName());
            person.setPersonSurname(registerRequest.getPersonSurname());
            person.setNickname(registerRequest.getNickname());
            person.setEmail(registerRequest.getEmail());
            person.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));

            Privilege privilege = privilegeRepository.findByPrivilegeName("ROLE_USER")
                    .orElseThrow(() -> new CompetitionException("auth.privilege.notfound", "AUTH-002"));

            person.getPrivileges().add(privilege);
            personRepository.save(person);

            String accessToken = jwtUtil.generateToken(person);
            String refreshToken = createRefreshToken(person).getRefreshToken();

            log.info("User {} registered successfully", person.getNickname());
            return new AuthResponse(accessToken, refreshToken, person.getPersonID(), person.getNickname(), person.getPrivileges());
        } catch (Exception e) {
            log.error("Critical error during user registration", e);
            throw new CompetitionException("error.internal", "SYS-500");
        }
    }

    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        log.debug("Login attempt for identifier: {}", loginRequest.getNickname());
        Person person;
        try {
            try {
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getNickname(), loginRequest.getPassword()));
                person = personRepository.findByNickname(loginRequest.getNickname())
                        .orElseThrow(() -> new CompetitionException("auth.user.notfound", "AUTH-003"));
            } catch (AuthenticationException e) {
                log.debug("Nickname login failed, trying email for: {}", loginRequest.getEmail());
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
                person = personRepository.findByEmail(loginRequest.getEmail())
                        .orElseThrow(() -> new CompetitionException("auth.user.notfound", "AUTH-003"));
            }
        } catch (AuthenticationException e) {
            log.warn("Authentication failed for user: {}", loginRequest.getNickname());
            throw new CompetitionException("auth.credentials.invalid", "AUTH-004");
        }

        String accessToken = jwtUtil.generateToken(person);
        refreshTokenRepository.deleteByPerson(person);
        RefreshToken refreshToken = createRefreshToken(person);

        log.info("User {} logged in successfully", person.getNickname());
        return new AuthResponse(accessToken, refreshToken.getRefreshToken(), person.getPersonID(), person.getNickname(), person.getPrivileges());
    }

    public RefreshToken createRefreshToken(Person person) {
        log.debug("Creating refresh token for user ID: {}", person.getPersonID());
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setPerson(person);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setRefreshToken(UUID.randomUUID().toString());
        return refreshTokenRepository.save(refreshToken);
    }

    public String refreshAccessToken(String requestRefreshToken) {
        return refreshTokenRepository.findByRefreshToken(requestRefreshToken)
                .map(token -> {
                    if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
                        log.warn("Refresh token expired for token: {}", requestRefreshToken);
                        refreshTokenRepository.delete(token);
                        throw new CompetitionException("auth.token.expired", "AUTH-005");
                    }
                    return token;
                })
                .map(token -> {
                    log.info("Refreshing access token for user: {}", token.getPerson().getNickname());
                    return jwtUtil.generateToken(token.getPerson());
                })
                .orElseThrow(() -> new CompetitionException("auth.token.notfound", "AUTH-006"));
    }

    @Transactional
    public void deleteRefreshTokenByPersonId(Long id) {
        log.info("Deleting refresh tokens for person ID: {}", id);
        Person person = personRepository.findById(id)
                .orElseThrow(() -> new CompetitionException("auth.user.notfound", "AUTH-003"));
        refreshTokenRepository.deleteByPerson(person);
    }

    /**
     * Retrieves the current user's information based on their nickname.
     * Includes logging for monitoring user requests.
     */
    public AuthResponse getCurrentPerson(String nickname) {
        log.debug("Fetching profile data for user: {}", nickname);

        Person user = personRepository.findByNickname(nickname)
                .orElseThrow(() -> {
                    log.warn("Profile fetch failed: user {} not found", nickname);
                    return new CompetitionException("auth.user.notfound", "AUTH-003", nickname);
                });

        log.info("Profile data successfully retrieved for user: {}", nickname);
        return new AuthResponse(null, null, user.getPersonID(), user.getNickname(), user.getPrivileges());
    }
}