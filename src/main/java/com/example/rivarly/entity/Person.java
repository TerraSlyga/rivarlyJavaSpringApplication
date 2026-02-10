package com.example.rivarly.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "person")
@Getter
@Setter
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "personId")
    private Long personID;

    @Column(name = "personName", length = 50)
    private String personName;

    @Column(name = "personSurname", length = 50)
    private String personSurname;

    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @Column(name = "passwordHash", length = 255, nullable = false)
    private String passwordHash;

    @Column(name = "nickname", length = 50, nullable = false, unique = true)
    private String nickname;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "person_privilege",
            joinColumns = @JoinColumn(name = "personId"),
            inverseJoinColumns = @JoinColumn(name = "privilegeId")
    )
    private Set<Privilege> privileges = new HashSet<>();;

    @Column(name = "iconPath")
    private String IconPath;

}
