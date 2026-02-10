package com.example.rivarly.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "eventStateList")
@Getter
@Setter
public class EventStateList {

    @Id
    @Column(name = "eventStateName", length = 15, nullable = false)
    private String eventStateName;
}
