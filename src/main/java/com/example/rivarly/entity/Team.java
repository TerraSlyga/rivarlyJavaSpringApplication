package com.example.rivarly.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "team")
@Getter
@Setter
public class Team {

    public static final int SHORT_NAME_LENGTH = 50;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long teamId;

    @Column(name = "teamName", length = SHORT_NAME_LENGTH, nullable = false)
    private String teamName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personId")
    private Person captain;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "teamMembers",
            joinColumns = @JoinColumn(name = "teamId"),
            inverseJoinColumns = @JoinColumn(name = "PersonId"))
    private Set<Person> members;

    @Column(name = "iconPath")
    private String iconPath;
}
