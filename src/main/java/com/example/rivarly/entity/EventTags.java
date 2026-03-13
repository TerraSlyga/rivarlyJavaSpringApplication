package com.example.rivarly.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents tags associated with events.
 * Each tag can have a unique identifier, a name, and an optional description.
 */
@Entity
@Table(name = "eventTags")
@Getter
@Setter
public class EventTags {

    /**
     * The maximum length for the tag name.
     */
    public static final int SHORT_NAME_LENGTH = 50;

    /**
     * The maximum length allowed for the tag description.
     */
    public static final int MAX_LENGTH = 255;

    /**
     * Unique identifier for the tag.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eventTagId")
    private Long eventTagId;

    /**
     * Name of the tag, which is required and has a maximum length of SHORT_NAME_LENGTH.
     */
    @Column(name = "eventTagName", length = SHORT_NAME_LENGTH, nullable = false)
    private String eventTagName;

    /**
     * Description of the tag, with a maximum length of MAX_LENGTH. This field is optional.
     */
    @Column(name = "eventTagDescription", length = MAX_LENGTH)
    private String eventTagDescription;
}