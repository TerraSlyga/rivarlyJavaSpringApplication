package com.example.rivarly.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "eventTags")
@Getter
@Setter
public class EventTags {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventTagId")
    private Long eventTagId;

    @Column(name = "eventTagName", length = 50, nullable = false)
    private String eventTagName;

    @Column(name = "eventTagDescription", length = 255)
    private String eventTagDescription;
}
