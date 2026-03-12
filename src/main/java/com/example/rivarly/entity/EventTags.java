package com.example.rivarly.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "eventTags")
@Getter
@Setter
public class EventTags {

    public static final int SHORT_NAME_LENGTH = 50;
    public static final int MAX_LENGTH = 255;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventTagId")
    private Long eventTagId;

    @Column(name = "eventTagName", length = SHORT_NAME_LENGTH, nullable = false)
    private String eventTagName;

    @Column(name = "eventTagDescription", length = MAX_LENGTH)
    private String eventTagDescription;
}
