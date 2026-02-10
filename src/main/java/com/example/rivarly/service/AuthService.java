package com.example.rivarly.service;

import com.example.rivarly.dto.auth.LoginRequest;
import com.example.rivarly.dto.auth.RegisterRequest;
import com.example.rivarly.entity.Person;
import com.example.rivarly.entity.Privilege;
import com.example.rivarly.repository.PersonRepository;
import com.example.rivarly.repository.PrivilegeRepository;
import com.example.rivarly.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.rivarly.dto.auth.AuthResponse;

/**
 * Service for handling authentication and user management operations.
 * This service provides functionalities for user registration, login,
 * and retrieving current user details.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final PersonRepository personRepository;
    private final PrivilegeRepository privilegeRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    private final JwtUtil jwtUtil;


    /**
     * Registers a new user by creating a Person entity, assigning a default privilege,
     * and generating a JWT token for the registered user.
     *
     * @param registerRequest the request object containing user details such as name, surname, nickname, email, and password
     * @return an AuthResponse containing the generated access token, the user's ID, and nickname
     * @throws RuntimeException if the nickname is already taken or the default privilege "ROLE_USER" is not found
     */
    public AuthResponse register(RegisterRequest registerRequest){

        if(personRepository.existsByNickname(registerRequest.getNickname())){
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

        String token = jwtUtil.generateToken(person);
        return new AuthResponse(token, person.getPersonID(), person.getNickname(), person.getPrivileges());
    }

    /**
     * Authenticates a login attempt using either nickname or email alongside the provided password.
     * If authentication is successful, generates a JWT token for the authenticated user.
     *
     * @param loginRequest the login details containing either nickname or email, along with the password
     * @return an AuthResponse containing the generated access token, the user's ID, and their nickname
     * @throws RuntimeException if the user is not found with the given credentials
     */
    public AuthResponse login(LoginRequest loginRequest) {
        Person person;
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getNickname(),
                            loginRequest.getPassword()
                    )
            );
            person = personRepository.findByNickname(loginRequest.getNickname())
                    .orElseThrow(() -> new RuntimeException("User not found with nickname: " + loginRequest.getNickname()));
        } catch (Exception e) {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            person = personRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found with email: " + loginRequest.getEmail()));
        }

        String token = jwtUtil.generateToken(person);
        return new AuthResponse(token, person.getPersonID(), person.getNickname(), person.getPrivileges());
    }

    /**
     * Retrieves the current user's authentication details based on the provided nickname.
     *
     * @param nickname the unique nickname of the user to be retrieved
     * @return an AuthResponse containing null as the access token, the user's ID, and their nickname
     * @throws UsernameNotFoundException if no user is found with the specified nickname
     */
    public AuthResponse getCurrentPerson(String nickname) {
        Person user = personRepository.findByNickname(nickname)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with nickname: " + nickname));

        return new AuthResponse(null, user.getPersonID(), user.getNickname(), user.getPrivileges());
    }

}
