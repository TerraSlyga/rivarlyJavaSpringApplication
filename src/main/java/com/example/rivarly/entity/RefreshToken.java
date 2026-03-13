package com.example.rivarly.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Represents a RefreshToken entity in the system.
 * This entity is used for managing authentication refresh tokens.
 */
@Entity
@Table(name = "refreshToken")
@Getter
@Setter
public class RefreshToken {

    /**
     * Unique identifier for the RefreshToken.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long refreshTokenID;

    /**
     * The Person associated with the RefreshToken.
     */
    @OneToOne
    @JoinColumn(name = "personID")
    private Person person;

    /**
     * The actual value of the refresh token.
     * This value is unique and cannot be null.
     */
    @Column(nullable = false, unique = true)
    private String refreshToken;

    /**
     * The expiration date and time of the refresh token.
     * This value cannot be null.
     */
    @Column(nullable = false)
    private Instant expiryDate;
}