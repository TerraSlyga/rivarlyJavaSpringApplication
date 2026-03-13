package com.example.rivarly.repository;

import com.example.rivarly.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing Person entities in the database.
 * Extends JpaRepository to provide standard CRUD operations and custom query methods.
 */
@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    /**
     * Finds a Person entity by its nickname.
     *
     * @param nickname the nickname of the person to search for
     * @return an Optional containing the Person entity if found, or empty if not found
     */
    Optional<Person> findByNickname(String nickname);

    /**
     * Finds a Person entity by its email.
     *
     * @param email the email of the person to search for
     * @return an Optional containing the Person entity if found, or empty if not found
     */
    Optional<Person> findByEmail(String email);

    /**
     * Checks if a Person entity exists by its nickname.
     *
     * @param nickname the nickname of the person to verify
     * @return true if a Person with the given nickname exists, false otherwise
     */
    boolean existsByNickname(String nickname);

    /**
     * Checks if a Person entity exists by its email.
     *
     * @param email the email of the person to verify
     * @return true if a Person with the given email exists, false otherwise
     */
    boolean existsByEmail(String email);
}