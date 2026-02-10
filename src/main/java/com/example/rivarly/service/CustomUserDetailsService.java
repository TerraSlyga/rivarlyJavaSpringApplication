package com.example.rivarly.service;

import com.example.rivarly.entity.Person;
import com.example.rivarly.repository.PersonRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PersonRepository personRepository;

    public CustomUserDetailsService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    @Transactional
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
