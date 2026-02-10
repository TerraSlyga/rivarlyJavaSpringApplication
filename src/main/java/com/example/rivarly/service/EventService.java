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


@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    private final EventTagsRepository eventTagsRepository;
    private final EventTagMapper eventTagMapper;

    private final EventStateListRepository eventStateListRepository;
    private final EventStateListMapper eventStateListMapper;

    public Page<EventSmallInfoResponse> getEventsSmallInfo(Integer pageNumber){
        Pageable paging = PageRequest.of(pageNumber, 10);

        Page<Event> eventsPage = eventRepository.findAll(paging);
        return eventsPage.map(eventMapper::toDto);
    }

    public List<EventTagsResponse> getAllTags(){
        return eventTagsRepository.findAll().stream().map(eventTagMapper::toDto).toList();
    }

    public List<EventStateListResponse> getAllStates(){
        return eventStateListRepository.findAll().stream().map(eventStateListMapper::toDto).toList();
    }



}
