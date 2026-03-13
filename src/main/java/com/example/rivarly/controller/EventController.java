package com.example.rivarly.controller;

import com.example.rivarly.dto.event.EventSmallInfoResponse;
import com.example.rivarly.dto.event.EventStateListResponse;
import com.example.rivarly.dto.event.EventTagsResponse;
import com.example.rivarly.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller for handling event-related API endpoints.
 * Provides endpoints for fetching event tags, states, and paginated small event information.
 */
@RestController
@RequestMapping("api/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    /**
     * Retrieves all available event tags.
     *
     * @return a ResponseEntity containing a list of EventTagsResponse
     */
    @Operation(
            summary = "Get all event tags",
            description = "Retrieves a list of all available event tags.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved event tags",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EventTagsResponse.class),
                                    examples = @ExampleObject(
                                            value = "[{\"eventTagId\":1,\"eventTagName\":\"Sports\",\"eventTagDescription\":\"Related to sports\"},"
                                                    + "{\"eventTagId\":2,\"eventTagName\":\"Music\",\"eventTagDescription\":\"Related to music\"}]"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/tags")
    public ResponseEntity<List<EventTagsResponse>> getAllTags() {
        return ResponseEntity.ok(eventService.getAllTags());
    }

    /**
     * Retrieves all available event states.
     *
     * @return a ResponseEntity containing a list of EventStateListResponse
     */
    @Operation(
            summary = "Get all event states",
            description = "Retrieves a list of all available event states.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved event states",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EventStateListResponse.class),
                                    examples = @ExampleObject(
                                            value = "[{\"eventStateName\":\"Scheduled\"},"
                                                    + "{\"eventStateName\":\"Ongoing\"},"
                                                    + "{\"eventStateName\":\"Completed\"}]"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/states")
    public ResponseEntity<List<EventStateListResponse>> getAllStates() {
        return ResponseEntity.ok(eventService.getAllStates());
    }

    /**
     * Retrieves a paginated list of small event information.
     *
     * @param pageNumber the page number to retrieve, default is 0
     * @return a ResponseEntity containing a Page of EventSmallInfoResponse
     */
    @Operation(
            summary = "Get paginated small event information",
            description = "Retrieves a paginated list of small event information.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved paginated event information",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = EventSmallInfoResponse.class),
                                    examples = @ExampleObject(
                                            value = "{\"content\":[{\"eventId\":1,\"eventName\":\"Marathon\",\"eventDescription\":\"Annual city marathon\","
                                                    + "\"iconPath\":\"/icons/marathon.png\",\"organizer\":null,\"eventTags\":[],\"eventStates\":[],\"eventRegistration\":[]},"
                                                    + "{\"eventId\":2,\"eventName\":\"Rock Concert\",\"eventDescription\":\"Live music event\","
                                                    + "\"iconPath\":\"/icons/concert.png\",\"organizer\":null,\"eventTags\":[],\"eventStates\":[],\"eventRegistration\":[]}],"
                                                    + "\"pageable\":{\"pageNumber\":0},\"totalPages\":1,\"totalElements\":2}"
                                    )
                            )
                    )
            }
    )
    @GetMapping("/allEventsSmallInfo")
    public ResponseEntity<Page<EventSmallInfoResponse>> getAllEventsSmallInfo(@RequestParam(defaultValue = "0") Integer pageNumber) {
        return ResponseEntity.ok(eventService.getEventsSmallInfo(pageNumber));
    }
}