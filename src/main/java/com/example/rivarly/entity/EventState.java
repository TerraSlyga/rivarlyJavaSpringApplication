package com.example.rivarly.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "eventState")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventStateId")
    private Long eventStateId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "eventStateName", referencedColumnName = "eventStateName", nullable = false)
    private EventStateList eventStateName;

    @Column(name = "eventStateStart", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "eventStateEnd")
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "eventId", nullable = false)
    @JsonIgnore
    private Event event;
}
