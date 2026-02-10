package com.example.rivarly.controller;

import com.example.rivarly.dto.event.EventSmallInfoResponse;
import com.example.rivarly.dto.event.EventStateListResponse;
import com.example.rivarly.dto.event.EventTagsResponse;
import com.example.rivarly.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @GetMapping("/tags")
    public ResponseEntity<List<EventTagsResponse>> getAllTags() {
        return ResponseEntity.ok(eventService.getAllTags());
    }

    @GetMapping("/states")
    public ResponseEntity<List<EventStateListResponse>> getAllStates() {
        return ResponseEntity.ok(eventService.getAllStates());
    }

    @GetMapping("/allEventsSmallInfo")
    public ResponseEntity<Page<EventSmallInfoResponse>> getAllEventsSmallInfo(@RequestParam(defaultValue = "0") Integer pageNumber) {
        return ResponseEntity.ok(eventService.getEventsSmallInfo(pageNumber));
    }
}
