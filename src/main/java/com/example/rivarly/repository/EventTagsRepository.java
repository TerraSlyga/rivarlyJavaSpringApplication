package com.example.rivarly.repository;

import com.example.rivarly.entity.EventTags;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository interface for managing EventTags entities in the database.
 * Extends JpaRepository to provide standard CRUD operations and query method support.
 */
public interface EventTagsRepository extends JpaRepository<EventTags, Long> {
}