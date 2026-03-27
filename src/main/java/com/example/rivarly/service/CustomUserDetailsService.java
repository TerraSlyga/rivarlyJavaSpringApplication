package com.example.rivarly.service;

import com.example.rivarly.entity.Person;
import com.example.rivarly.repository.PersonRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

/**
 * Custom implementation of UserDetailsService for managing user authentication.
 * This service loads user-specific data from the database.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    /**
     * Constructs a CustomUserDetailsService with the provided PersonRepository.
     *
     * @param personRepository the repository used to access Person entities
     */
    public CustomUserDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    /**
     * Loads the user details by their username.
     *
     * @param username the username of the user to look up
     * @return the UserDetails object containing user credentials and authorities
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    @Transactional
    @Cacheable(value = "users", key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Person person = personRepository.findByNickname(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

        var authorities = person.getPrivileges().stream()
                .map(privilege -> new SimpleGrantedAuthority(privilege.getPrivilegeName()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                person.getNickname(),
                person.getPasswordHash(),
                authorities
        );
    }
}