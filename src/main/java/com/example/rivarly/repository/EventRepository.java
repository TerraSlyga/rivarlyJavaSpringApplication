package com.example.rivarly.repository;

import com.example.rivarly.entity.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

/**
 * Repository interface for managing Event entities in the database.
 * Extends JpaRepository to provide standard CRUD operations and custom query methods.
 */
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Finds an Event entity by its name.
     *
     * @param eventName the name of the event to search for
     * @return an Optional containing the Event entity if found, or empty if not found
     */
    Optional<Event> findByEventName(String eventName);

    /**
     * Checks if an Event entity exists by its name.
     *
     * @param eventName the name of the event to verify
     * @return true if an Event with the given name exists, false otherwise
     */
    boolean existsByEventName(String eventName);

    @EntityGraph(attributePaths = {"eventTags", "eventStates", "eventRegistration"})
    @Query("SELECT e FROM Event e")
    Page<Event> findAllOptimized(Pageable pageable);
}