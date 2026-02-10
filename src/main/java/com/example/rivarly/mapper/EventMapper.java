package com.example.rivarly.mapper;

import com.example.rivarly.dto.event.EventSmallInfoResponse;
import com.example.rivarly.entity.Event;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    EventSmallInfoResponse toDto(Event event);

}