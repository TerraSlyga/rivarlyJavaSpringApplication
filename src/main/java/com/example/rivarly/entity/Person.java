package com.example.rivarly.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "person")
@Getter
@Setter
public class Person {

    public static final int SHORT_NAME_LENGTH = 50;
    public static final int MAX_LENGTH = 255;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personId")
    private Long personID;

    @Column(name = "personName", length = SHORT_NAME_LENGTH)
    private String personName;

    @Column(name = "personSurname", length = SHORT_NAME_LENGTH)
    private String personSurname;

    @Column(name = "email", length = SHORT_NAME_LENGTH, nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "passwordHash", length = MAX_LENGTH, nullable = false)
    private String passwordHash;

    @Column(name = "nickname", length = SHORT_NAME_LENGTH, nullable = false, unique = true)
    private String nickname;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "person_privilege",
            joinColumns = @JoinColumn(name = "personId"),
            inverseJoinColumns = @JoinColumn(name = "privilegeId")
    )
    private Set<Privilege> privileges = new HashSet<>();

    @Column(name = "iconPath")
    private String iconPath;

}
