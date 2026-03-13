package com.example.rivarly.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for representing event state information.
 * Contains the name of the event state.
 */
@Data
@AllArgsConstructor
public class EventStateListResponse {
    /**
     * Name of the event state.
     */
    private String eventStateName;
}