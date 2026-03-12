package com.example.rivarly.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "eventStateList")
@Getter
@Setter
public class EventStateList {

    public static final int MAX_STATE_LENGTH = 15;
    @Id
    @Column(name = "eventStateName", length = MAX_STATE_LENGTH, nullable = false)
    private String eventStateName;
}
