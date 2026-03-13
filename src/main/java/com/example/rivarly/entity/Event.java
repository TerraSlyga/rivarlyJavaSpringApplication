package com.example.rivarly.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents an Event entity in the system.
 * An Event has multiple properties like name, description, tags, registrations, states, and an associated organizer.
 */
@Entity
@Table(name = "event")
@Getter
@Setter
public class Event {

    /**
     * The maximum length of a short name.
     */
    public static final int SHORT_NAME_LENGTH = 50;

    /**
     * The maximum allowed length for certain fields.
     */
    public static final int MAX_LENGTH = 255;

    /**
     * Unique identifier for the Event.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventId")
    private Long eventId;

    /**
     * Name of the Event with a maximum length of SHORT_NAME_LENGTH.
     */
    @Column(name = "eventName", length = SHORT_NAME_LENGTH, nullable = false)
    private String eventName;

    /**
     * Description of the Event with a maximum length of MAX_LENGTH.
     */
    @Column(name = "eventDescription", length = MAX_LENGTH)
    private String eventDescription;

    /**
     * The person who organizes the Event.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personId")
    private Person organizer;

    /**
     * The tags associated with the Event.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "eventTagsMapping",
            joinColumns = @JoinColumn(name = "eventId"),
            inverseJoinColumns = @JoinColumn(name = "eventTagsId")
    )
    private Set<EventTags> eventTags = new HashSet<>();

    /**
     * States associated with the Event.
     */
    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<EventState> eventStates = new HashSet<>();

    /**
     * Path of the icon representing the Event.
     */
    @Column(name = "iconPath")
    private String iconPath;

    /**
     * Registrations associated with the Event.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "eventRegistrationMapping",
            joinColumns = @JoinColumn(name = "eventId"),
            inverseJoinColumns = @JoinColumn(name = "eventRegistrationId")
    )
    private Set<EventRegistration> eventRegistration;
}