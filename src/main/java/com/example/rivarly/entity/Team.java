package com.example.rivarly.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Represents a Team entity in the system.
 * A Team has various properties such as a unique identifier, a name, a captain, members, and an optional icon path.
 */
@Entity
@Table(name = "team")
@Getter
@Setter
public class Team {

    /**
     * The maximum length of the team's short name.
     */
    public static final int SHORT_NAME_LENGTH = 50;

    /**
     * Unique identifier for the Team.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    /**
     * Name of the Team.
     * Maximum length is SHORT_NAME_LENGTH and this field is required.
     */
    @Column(name = "teamName", length = SHORT_NAME_LENGTH, nullable = false)
    private String teamName;

    /**
     * The captain of the Team.
     * Stored as a many-to-one relationship with the Person entity.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personId")
    private Person captain;

    /**
     * Members of the Team.
     * Stored as a many-to-many relationship with the Person entity.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "teamMembers",
            joinColumns = @JoinColumn(name = "teamId"),
            inverseJoinColumns = @JoinColumn(name = "PersonId"))
    private Set<Person> members;

    /**
     * Path to the icon representing the Team.
     */
    @Column(name = "iconPath")
    private String iconPath;


    /**
     * Indicates whether the Team is temporary.
     * A temporary team is not considered permanent and
     * represent a short-term entity for events or activities.
     */
    @Column(name = "temporary")
    private boolean temporary;
}