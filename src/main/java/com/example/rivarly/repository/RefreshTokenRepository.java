package com.example.rivarly.repository;

import com.example.rivarly.entity.Person;
import com.example.rivarly.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for managing RefreshToken entities in the database.
 * Extends JpaRepository to provide standard CRUD operations and custom query methods.
 */
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * Finds a RefreshToken entity by its token value.
     *
     * @param token the value of the refresh token to search for
     * @return an Optional containing the RefreshToken entity if found, or empty if not found
     */
    Optional<RefreshToken> findByRefreshToken(String token);

    /**
     * Deletes all RefreshToken entities associated with a specific Person.
     *
     * @param person the Person entity whose associated refresh tokens should be deleted
     */
    void deleteByPerson(Person person);
}