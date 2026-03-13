package com.example.rivarly.mapper;

import com.example.rivarly.dto.event.EventSmallInfoResponse;
import com.example.rivarly.entity.Event;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting Event entities to EventSmallInfoResponse DTOs.
 * Utilizes MapStruct to generate implementation at compile-time.
 */
@Mapper(componentModel = "spring")
public interface EventMapper {

    /**
     * Maps an Event entity to its corresponding EventSmallInfoResponse DTO.
     *
     * @param event the Event entity to be converted
     * @return the converted EventSmallInfoResponse DTO
     */
    EventSmallInfoResponse toDto(Event event);

}