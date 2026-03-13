package com.example.rivarly.mapper;

import com.example.rivarly.dto.event.EventStateListResponse;
import com.example.rivarly.entity.EventStateList;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting EventStateList entities to EventStateListResponse DTOs.
 * Utilizes MapStruct to generate implementation at compile-time.
 */
@Mapper(componentModel = "spring")
public interface EventStateListMapper {

    /**
     * Maps an EventStateList entity to its corresponding EventStateListResponse DTO.
     *
     * @param eventStateList the EventStateList entity to be converted
     * @return the converted EventStateListResponse DTO
     */
    EventStateListResponse toDto(EventStateList eventStateList);

}