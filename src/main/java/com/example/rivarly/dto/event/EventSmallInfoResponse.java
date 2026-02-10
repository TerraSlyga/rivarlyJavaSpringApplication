package com.example.rivarly.dto.event;

import com.example.rivarly.entity.EventRegistration;
import com.example.rivarly.entity.EventState;
import com.example.rivarly.entity.EventTags;
import com.example.rivarly.entity.Person;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class EventSmallInfoResponse {
    private Long eventId;
    private String eventName;
    private String eventDescription;
    private String IconPath;
    private Person organizer;
    private Set<EventTags> eventTags;
    private List<EventState> eventStates;
    private Set<EventRegistration> eventRegistration;
}
