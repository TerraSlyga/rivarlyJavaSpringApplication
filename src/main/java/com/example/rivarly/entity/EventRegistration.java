package com.example.rivarly.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Represents an event registration entity in the system.
 * Each event registration is associated with participants and contains information
 * about the number of participants registered for an event.
 */
@Entity
@Table(name = "eventRegistration")
@Getter
@Setter
public class EventRegistration {

    /**
     * Unique identifier for the event registration.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventRegistrationId")
    private Long eventRegistrationId;

    /**
     * Set of teams participating in the event registration.
     * Represents a many-to-many relationship with the Team entity.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "EventParticipants",
            joinColumns = @JoinColumn(name = "eventRegistrationId"),
            inverseJoinColumns = @JoinColumn(name = "teamId")
    )
    private Set<Team> participants;

    /**
     * The count of participants registered for the event.
     */
    @Column(name = "participantsCount")
    private int participantsCount;

}