package com.example.rivarly.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Person entity in the system.
 * A Person has various properties such as name, surname, email, password, nickname,
 * associated privileges, and an optional icon path.
 */
@Entity
@Table(name = "person", indexes = {
        @Index(name = "idx_person_nickname", columnList = "nickname"),
        @Index(name = "idx_person_email", columnList = "email")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    /**
     * The maximum length of a short name field.
     */
    public static final int SHORT_NAME_LENGTH = 50;

    /**
     * The maximum allowed length for longer fields.
     */
    public static final int MAX_LENGTH = 255;

    /**
     * Unique identifier for the Person.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personId")
    private Long personID;

    /**
     * First name of the Person.
     * Maximum length is SHORT_NAME_LENGTH.
     */
    @Column(name = "personName", length = SHORT_NAME_LENGTH)
    private String personName;

    /**
     * Last name of the Person.
     * Maximum length is SHORT_NAME_LENGTH.
     */
    @Column(name = "personSurname", length = SHORT_NAME_LENGTH)
    private String personSurname;

    /**
     * Email address of the Person.
     * This field is required and must be unique.
     * Maximum length is SHORT_NAME_LENGTH.
     */
    @Column(name = "email", length = SHORT_NAME_LENGTH, nullable = false, unique = true)
    private String email;

    /**
     * Hashed password of the Person.
     * This field is required and is ignored during JSON serialization.
     * Maximum length is MAX_LENGTH.
     */
    @JsonIgnore
    @Column(name = "passwordHash", length = MAX_LENGTH, nullable = false)
    private String passwordHash;

    /**
     * Nickname of the Person.
     * This field is required and must be unique.
     * Maximum length is SHORT_NAME_LENGTH.
     */
    @Column(name = "nickname", length = SHORT_NAME_LENGTH, nullable = false, unique = true)
    private String nickname;

    /**
     * Set of privileges assigned to the Person.
     * Stored as a many-to-many relationship in the database.
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "person_privilege",
            joinColumns = @JoinColumn(name = "personId"),
            inverseJoinColumns = @JoinColumn(name = "privilegeId")
    )
    private Set<Privilege> privileges = new HashSet<>();

    /**
     * Path to the icon representing the Person.
     */
    @Column(name = "iconPath")
    private String iconPath;

}