package com.example.rivarly.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a Privilege entity in the system.
 * A Privilege has an identifier, a name, and an optional description.
 */
@Entity
@Table(name = "privilege")
@Getter
@Setter
public class Privilege {

    /**
     * The maximum length of a short name field.
     */
    public static final int SHORT_NAME_LENGTH = 50;

    /**
     * The maximum allowed length for longer fields.
     */
    public static final int MAX_LENGTH = 255;

    /**
     * Unique identifier for the Privilege.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "privilegeId")
    private long privilegeId;

    /**
     * Name of the Privilege. This field is required, unique, and has a maximum length of SHORT_NAME_LENGTH.
     */
    @Column(name = "privilegeName", length = SHORT_NAME_LENGTH, nullable = false, unique = true)
    private String privilegeName;

    /**
     * Description of the Privilege. This field is optional and has a maximum length of MAX_LENGTH.
     */
    @Column(name = "privilegeDescription", length = MAX_LENGTH)
    private String privilegeDescription;

}