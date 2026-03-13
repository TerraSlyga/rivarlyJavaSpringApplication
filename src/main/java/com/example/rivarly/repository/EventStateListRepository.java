package com.example.rivarly.repository;

import com.example.rivarly.entity.EventStateList;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing EventStateList entities in the database.
 * Provides standard CRUD operations and query method support by extending JpaRepository.
 */
public interface EventStateListRepository extends JpaRepository<EventStateList, Long> {
}