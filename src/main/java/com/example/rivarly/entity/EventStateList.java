package com.example.rivarly.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a list of possible states for an Event.
 * This entity contains predefined states with their respective names.
 */
@Entity
@Table(name = "eventStateList")
@Getter
@Setter
public class EventStateList {

    /**
     * The maximum length allowed for the state name.
     */
    public static final int MAX_STATE_LENGTH = 15;

    /**
     * The name of the state, serving as the unique identifier.
     * It is stored in the database column 'eventStateName'.
     */
    @Id
    @Column(name = "eventStateName", length = MAX_STATE_LENGTH, nullable = false)
    private String eventStateName;
}