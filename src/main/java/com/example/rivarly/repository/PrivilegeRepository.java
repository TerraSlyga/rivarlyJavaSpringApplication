package com.example.rivarly.repository;

import com.example.rivarly.entity.Privilege;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing Privilege entities in the database.
 * Extends JpaRepository to provide standard CRUD operations and custom query methods.
 */
@Repository
public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {

    /**
     * Finds a Privilege entity by its name.
     *
     * @param privilegeName the name of the privilege to search for
     * @return an Optional containing the Privilege entity if found, or empty if not found
     */
    Optional<Privilege> findByPrivilegeName(String privilegeName);

}