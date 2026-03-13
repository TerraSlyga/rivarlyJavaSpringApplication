package com.example.rivarly.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for representing information about event tags.
 * Contains details like the unique identifier, tag name, and description.
 */
@Data
@AllArgsConstructor
public class EventTagsResponse {
    /**
     * Unique identifier of the event tag.
     */
    private Long eventTagId;

    /**
     * Name of the event tag.
     */
    private String eventTagName;

    /**
     * Description of the event tag.
     */
    private String eventTagDescription;
}