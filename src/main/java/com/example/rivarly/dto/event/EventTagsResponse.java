package com.example.rivarly.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventTagsResponse {
    private Long eventTagId;
    private String eventTagName;
    private String eventTagDescription;
}
