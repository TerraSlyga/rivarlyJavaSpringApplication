package com.example.rivarly.repository;

import com.example.rivarly.entity.Person;
import com.example.rivarly.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshToken(String token);
    void deleteByPerson(Person person);
}
