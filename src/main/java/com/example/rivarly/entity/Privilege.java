package com.example.rivarly.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "privilege")
@Getter
@Setter
public class Privilege {

    public static final int SHORT_NAME_LENGTH = 50;
    public static final int MAX_LENGTH = 255;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "privilegeId")
    private long privilegeId;

    @Column(name = "privilegeName", length = SHORT_NAME_LENGTH, nullable = false, unique = true)
    private String privilegeName;

    @Column(name = "privilegeDescription", length = MAX_LENGTH)
    private String privilegeDescription;
    
}
