package com.example.rivarly.repository;

import com.example.rivarly.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    
    Optional<Event> findByEventName(String eventName);
    
    boolean existsByEventName(String eventName);

}
