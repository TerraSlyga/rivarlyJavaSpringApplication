package com.example.rivarly.mapper;

import com.example.rivarly.dto.event.EventTagsResponse;
import com.example.rivarly.entity.EventTags;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EventTagMapper {

    EventTagsResponse toDto(EventTags eventTag);
}
