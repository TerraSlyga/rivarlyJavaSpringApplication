package com.example.rivarly.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "event")
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventId")
    private Long eventId;

    @Column(name = "eventName", length = 50, nullable = false)
    private String eventName;

    @Column(name = "eventDescription", length = 255)
    private String eventDescription;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personId")
    private Person organizer;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "eventTagsMapping",
            joinColumns = @JoinColumn(name = "eventId"),
            inverseJoinColumns = @JoinColumn(name = "eventTagsId")
    )
    private Set<EventTags> eventTags = new HashSet<>();

    @OneToMany(mappedBy = "event", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<EventState> eventStates = new HashSet<>();

    @Column(name = "IconPath")
    private String IconPath;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "eventRegistrationMapping",
            joinColumns = @JoinColumn(name = "eventId"),
            inverseJoinColumns = @JoinColumn(name = "eventRegistrationId")

    )
    private Set<EventRegistration> eventRegistration;

}
