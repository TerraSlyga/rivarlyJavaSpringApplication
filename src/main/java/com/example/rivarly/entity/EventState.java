package com.example.rivarly.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Represents the state of an Event at a given point in time.
 * Contains information about the state name, start time, end time, and the associated Event.
 */
@Entity
@Table(name = "eventState")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventState {

    /**
     * Unique identifier for the EventState.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventStateId")
    private Long eventStateId;

    /**
     * Name of the state, linked to an entry in EventStateList.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "eventStateName", referencedColumnName = "eventStateName", nullable = false)
    private EventStateList eventStateName;

    /**
     * The start time of the EventState.
     */
    @Column(name = "eventStateStart", nullable = false)
    private LocalDateTime startTime;

    /**
     * The end time of the EventState, may be null if the state is ongoing.
     */
    @Column(name = "eventStateEnd")
    private LocalDateTime endTime;

    /**
     * The Event associated with this state.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventId", nullable = false)
    @JsonIgnore
    private Event event;
}
