package com.example.rivarly.dto.event;

import com.example.rivarly.entity.EventRegistration;
import com.example.rivarly.entity.EventState;
import com.example.rivarly.entity.EventTags;
import com.example.rivarly.entity.Person;
import lombok.Data;

import java.util.List;
import java.util.Set;

/**
 * DTO for transferring minimal event information.
 * Contains essential fields needed for quick event display.
 */
@Data
public class EventSmallInfoResponse {
    /**
     * Unique identifier of the event.
     */
    private Long eventId;

    /**
     * Name of the event.
     */
    private String eventName;

    /**
     * Description of the event.
     */
    private String eventDescription;

    /**
     * Path to the icon representing the event.
     */
    private String iconPath;

    /**
     * Organizer of the event.
     * Contains details of the person who organized the event.
     */
    private Person organizer;

    /**
     * Set of tags associated with the event.
     * Provides categorization or additional context for the event.
     */
    private Set<EventTags> eventTags;

    /**
     * List of states indicating the phases or statuses of the event.
     */
    private List<EventState> eventStates;

    /**
     * Set of registrations related to the event.
     * Includes information about participants.
     */
    private Set<EventRegistration> eventRegistration;
}