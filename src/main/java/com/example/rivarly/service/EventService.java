package com.example.rivarly.service;

import com.example.rivarly.dto.event.EventSmallInfoResponse;
import com.example.rivarly.dto.event.EventStateListResponse;
import com.example.rivarly.dto.event.EventTagsResponse;
import com.example.rivarly.entity.Event;
import com.example.rivarly.mapper.EventMapper;
import com.example.rivarly.mapper.EventStateListMapper;
import com.example.rivarly.mapper.EventTagMapper;
import com.example.rivarly.repository.EventRepository;
import com.example.rivarly.repository.EventStateListRepository;
import com.example.rivarly.repository.EventTagsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service class for managing events.
 * Offers methods to fetch event information, tags, and states.
 */
@Service
@RequiredArgsConstructor
public class EventService {

    /**
     * Default page size for paginated requests.
     */
    public static final int DEFAULT_PAGE_SIZE = 10;

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    private final EventTagsRepository eventTagsRepository;
    private final EventTagMapper eventTagMapper;

    private final EventStateListRepository eventStateListRepository;
    private final EventStateListMapper eventStateListMapper;

    /**
     * Retrieves a paginated list of small event information.
     *
     * @param pageNumber the page number to retrieve
     * @return a Page of EventSmallInfoResponse containing small event information
     */
    public Page<EventSmallInfoResponse> getEventsSmallInfo(Integer pageNumber) {
        Pageable paging = PageRequest.of(pageNumber, DEFAULT_PAGE_SIZE);

        Page<Event> eventsPage = eventRepository.findAllOptimized(paging);
        return eventsPage.map(eventMapper::toDto);
    }

    /**
     * Retrieves all available event tags.
     *
     * @return a list of EventTagsResponse containing event tag information
     */
    public List<EventTagsResponse> getAllTags() {
        return eventTagsRepository.findAll().stream().map(eventTagMapper::toDto).toList();
    }

    /**
     * Retrieves all available event states.
     *
     * @return a list of EventStateListResponse containing event state information
     */
    public List<EventStateListResponse> getAllStates() {
        return eventStateListRepository.findAll().stream().map(eventStateListMapper::toDto).toList();
    }
    
    
    
    
}