package com.example.rivarly.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "eventRegistration")
@Getter
@Setter
public class EventRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventRegistrationId")
    private Long eventRegistrationId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "EventParticipants",
            joinColumns = @JoinColumn(name = "eventRegistrationId"),
            inverseJoinColumns = @JoinColumn(name = "teamId")
    )
    private Set<Team> participants;

    @Column(name = "participantsCount")
    private int participantsCount;  
    
}
