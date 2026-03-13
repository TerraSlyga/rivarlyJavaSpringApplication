package com.example.rivarly.service;

import com.example.rivarly.dto.auth.AuthResponse;
import com.example.rivarly.dto.auth.LoginRequest;
import com.example.rivarly.dto.auth.RegisterRequest;
import com.example.rivarly.entity.Person;
import com.example.rivarly.entity.Privilege;
import com.example.rivarly.entity.RefreshToken;
import com.example.rivarly.repository.PersonRepository;
import com.example.rivarly.repository.PrivilegeRepository;
import com.example.rivarly.repository.RefreshTokenRepository;
import com.example.rivarly.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Service responsible for handling authentication-related operations.
 * Includes user registration, login, token management, and fetching user information.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final PersonRepository personRepository;
    private final PrivilegeRepository privilegeRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @Value("${jwt.refreshExpirationMs}")
    private Long refreshTokenDurationMs;

    /**
     * Registers a new user in the system.
     * Ensures the nickname is unique, assigns default privileges, and generates tokens.
     *
     * @param registerRequest contains registration details like name, email, and password
     * @return AuthResponse object containing access and refresh tokens along with user details
     */
    public AuthResponse register(RegisterRequest registerRequest) {
        if (personRepository.existsByNickname(registerRequest.getNickname())) {
            throw new RuntimeException("Nickname is already taken!");
        }

        Person person = new Person();
        person.setPersonName(registerRequest.getPersonName());
        person.setPersonSurname(registerRequest.getPersonSurname());
        person.setNickname(registerRequest.getNickname());
        person.setEmail(registerRequest.getEmail());
        person.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));

        Privilege privilege = privilegeRepository.findByPrivilegeName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Privilege not found!"));
        person.getPrivileges().add(privilege);
        personRepository.save(person);

        String accessToken = jwtUtil.generateToken(person);
        String refreshToken = createRefreshToken(person).getRefreshToken();

        return new AuthResponse(accessToken, refreshToken, person.getPersonID(), person.getNickname(), person.getPrivileges());
    }

    /**
     * Logs in a user by validating credentials and generating new JWT tokens.
     * Supports both email and nickname for login.
     *
     * @param loginRequest contains login credentials
     * @return AuthResponse object containing access and refresh tokens along with user details
     */
    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        Person person;
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getNickname(), loginRequest.getPassword()));
            person = personRepository.findByNickname(loginRequest.getNickname())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } catch (Exception e) {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            person = personRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        String accessToken = jwtUtil.generateToken(person);

        // Remove existing refresh tokens for the user to avoid clutter
        refreshTokenRepository.deleteByPerson(person);
        // Create a new refresh token
        RefreshToken refreshToken = createRefreshToken(person);

        return new AuthResponse(accessToken, refreshToken.getRefreshToken(), person.getPersonID(), person.getNickname(), person.getPrivileges());
    }

    /**
     * Creates a new refresh token for the given user.
     * Stores the token in the database with an expiry date.
     *
     * @param person the user for whom the token is generated
     * @return the generated RefreshToken entity
     */
    public RefreshToken createRefreshToken(Person person) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setPerson(person);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setRefreshToken(UUID.randomUUID().toString());
        return refreshTokenRepository.save(refreshToken);
    }

    /**
     * Retrieves the refresh token string associated with a given user ID.
     *
     * @param personId the ID of the user
     * @return the refresh token string
     */
    @Transactional
    public String getRefreshTokenString(Long personId) {
        return refreshTokenRepository.findAll().stream()
                .filter(rt -> rt.getPerson().getPersonID().equals(personId))
                .findFirst()
                .map(RefreshToken::getRefreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token not found"));
    }

    /**
     * Refreshes the access token using a valid refresh token.
     * Ensures the refresh token is not expired before generating a new access token.
     *
     * @param requestRefreshToken the refresh token provided by the client
     * @return a new access token string
     */
    public String refreshAccessToken(String requestRefreshToken) {
        return refreshTokenRepository.findByRefreshToken(requestRefreshToken)
                .map(token -> {
                    // Check if the refresh token is expired
                    if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
                        refreshTokenRepository.delete(token);
                        throw new RuntimeException("Refresh token was expired. Please make a new signin request");
                    }
                    return token;
                })
                .map(token -> token.getPerson())
                .map(person -> jwtUtil.generateToken(person)) // Generate a new Access JWT
                .orElseThrow(() -> new RuntimeException("Refresh token is not in database!"));
    }

    /**
     * Retrieves the current user's information based on their nickname.
     *
     * @param nickname the nickname of the current user
     * @return AuthResponse object with the user's details
     */
    public AuthResponse getCurrentPerson(String nickname) {
        Person user = personRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with nickname: " + nickname));
        return new AuthResponse(null, null, user.getPersonID(), user.getNickname(), user.getPrivileges());
    }

    /**
     * Deletes the refresh token linked to a user by their ID.
     *
     * @param id the ID of the user
     */
    @Transactional
    public void deleteRefreshTokenByPersonId(Long id) {
        Person person = personRepository.findById(id).orElseThrow();
        refreshTokenRepository.deleteByPerson(person);
    }
}