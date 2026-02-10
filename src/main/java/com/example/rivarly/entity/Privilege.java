package com.example.rivarly.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "privilege")
@Getter
@Setter
public class Privilege {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "privilegeId")
    private long privilegeId;

    @Column(name = "privilegeName", length = 50, nullable = false, unique = true)
    private String privilegeName;

    @Column(name = "privilegeDescription", length = 255)
    private String privilegeDescription;
    
}
