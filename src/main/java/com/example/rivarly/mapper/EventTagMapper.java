package com.example.rivarly.mapper;

import com.example.rivarly.dto.event.EventTagsResponse;
import com.example.rivarly.entity.EventTags;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting EventTags entities to EventTagsResponse DTOs.
 * Utilizes MapStruct to generate implementation at compile-time.
 */
@Mapper(componentModel = "spring")
public interface EventTagMapper {

    /**
     * Maps an EventTags entity to its corresponding EventTagsResponse DTO.
     *
     * @param eventTag the EventTags entity to be converted
     * @return the converted EventTagsResponse DTO
     */
    EventTagsResponse toDto(EventTags eventTag);
}